package com.dextra.vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_core.StringVector;
import org.bytedeco.opencv.opencv_dnn.Net;

import static org.bytedeco.opencv.global.opencv_core.BORDER_CONSTANT;
import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_core.copyMakeBorder;
import static org.bytedeco.opencv.global.opencv_dnn.readNetFromONNX;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_AREA;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.getRotationMatrix2D;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.bytedeco.opencv.global.opencv_imgproc.warpAffine;

/**
 * Turns a palm detection into the 21 hand points with MediaPipe's hand landmark model.
 * A Java port of OpenCV Zoo's mp_handpose.py (Apache 2.0): crop around the palm, rotate the
 * hand upright, run the model on a 224 x 224 crop, then map the points back to the frame.
 */
public final class HandLandmarker implements AutoCloseable {

    static final int INPUT = 224;
    private static final int PALM_BASE = 0;          // palm landmark index of the wrist
    private static final int MIDDLE_FINGER_BASE = 2; // palm landmark index of the middle finger's base

    private final Net net;
    private final double confThreshold;

    public HandLandmarker(String modelPath, double confThreshold) {
        this.net = readNetFromONNX(modelPath);
        this.confThreshold = confThreshold;
    }

    /** A box cut out of an image and padded square with black. bias maps crop pixels back to the source. */
    private record Crop(Mat image, int x1, int y1, int x2, int y2, int biasX, int biasY) {
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /** Same steps as _cropAndPadFromPalm: shift, enlarge, clip to the image, crop, pad to a square. */
    private static Crop cropAndPad(Mat image, double x1, double y1, double x2, double y2,
                                   double shiftX, double shiftY, double enlarge, boolean forRotation) {
        double w = x2 - x1;
        double h = y2 - y1;
        x1 += shiftX * w;
        x2 += shiftX * w;
        y1 += shiftY * h;
        y2 += shiftY * h;
        double cx = (x1 + x2) / 2;
        double cy = (y1 + y2) / 2;
        double halfW = (x2 - x1) * enlarge / 2;
        double halfH = (y2 - y1) * enlarge / 2;
        int bx1 = clamp((int) (cx - halfW), 0, image.cols());
        int by1 = clamp((int) (cy - halfH), 0, image.rows());
        int bx2 = clamp((int) (cx + halfW), 0, image.cols());
        int by2 = clamp((int) (cy + halfH), 0, image.rows());
        int cols = bx2 - bx1;
        int rows = by2 - by1;
        if (cols < 2 || rows < 2) {
            return null;
        }
        Mat roi = new Mat(image, new Rect(bx1, by1, cols, rows));
        // Before rotating, pad to the diagonal so the corners survive the rotation.
        int side = forRotation ? (int) Math.sqrt((double) rows * rows + (double) cols * cols) : Math.max(rows, cols);
        int padH = side - rows;
        int padW = side - cols;
        int left = padW / 2;
        int top = padH / 2;
        Mat padded = new Mat();
        copyMakeBorder(roi, padded, top, padH - top, left, padW - left, BORDER_CONSTANT, new Scalar(0, 0, 0, 0));
        return new Crop(padded, bx1, by1, bx2, by2, bx1 - left, by1 - top);
    }

    /** Returns the hand's 21 points, or null when the model isn't confident a hand is there. */
    public HandPose estimate(Mat bgr, Palm palm) {
        Crop first = cropAndPad(bgr, palm.x1(), palm.y1(), palm.x2(), palm.y2(), 0, 0, 4.0, true);
        if (first == null) {
            return null;
        }
        Mat rgb = new Mat();
        cvtColor(first.image(), rgb, COLOR_BGR2RGB);
        int biasX = first.biasX();
        int biasY = first.biasY();

        // Palm box and palm points in the crop's coordinates.
        double bx1 = first.x1() - biasX;
        double by1 = first.y1() - biasY;
        double bx2 = first.x2() - biasX;
        double by2 = first.y2() - biasY;
        double[][] pl = new double[7][2];
        for (int k = 0; k < 7; k++) {
            pl[k][0] = palm.landmarks()[k][0] - biasX;
            pl[k][1] = palm.landmarks()[k][1] - biasY;
        }

        // Rotate so the wrist-to-middle-finger line points straight up.
        double[] p1 = pl[PALM_BASE];
        double[] p2 = pl[MIDDLE_FINGER_BASE];
        double radians = Math.PI / 2 - Math.atan2(-(p2[1] - p1[1]), p2[0] - p1[0]);
        radians = radians - 2 * Math.PI * Math.floor((radians + Math.PI) / (2 * Math.PI));
        double angle = Math.toDegrees(radians);
        Mat rotation = getRotationMatrix2D(new Point2f((float) ((bx1 + bx2) / 2), (float) ((by1 + by2) / 2)), angle, 1.0);
        Mat rotated = new Mat();
        warpAffine(rgb, rotated, rotation, new Size(rgb.cols(), rgb.rows()));
        double[] m = Blobs.affine(rotation);

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (double[] p : pl) {
            double rx = p[0] * m[0] + p[1] * m[1] + m[2];
            double ry = p[0] * m[3] + p[1] * m[4] + m[5];
            minX = Math.min(minX, rx);
            minY = Math.min(minY, ry);
            maxX = Math.max(maxX, rx);
            maxY = Math.max(maxY, ry);
        }
        Crop second = cropAndPad(rotated, minX, minY, maxX, maxY, 0, -0.4, 3.0, false);
        if (second == null) {
            return null;
        }
        Mat in = new Mat();
        resize(second.image(), in, new Size(INPUT, INPUT), 0, 0, INTER_AREA);
        Mat input = new Mat();
        in.convertTo(input, CV_32F, 1 / 255.0, 0);

        net.setInput(Blobs.nhwc(input));
        StringVector names = net.getUnconnectedOutLayersNames();
        MatVector outs = new MatVector();
        net.forward(outs, names);

        // Output order, as in mp_handpose.py: screen points (63), confidence (1), handedness (1), world points (63).
        float[] screen = null;
        float[] world = null;
        float[] conf = null;
        float[] handedness = null;
        for (long i = 0; i < outs.size(); i++) {
            float[] a = Blobs.toArray(outs.get(i));
            if (a.length == 63) {
                if (screen == null) {
                    screen = a;
                } else {
                    world = a;
                }
            } else if (a.length == 1) {
                if (conf == null) {
                    conf = a;
                } else {
                    handedness = a;
                }
            }
        }
        if (screen == null || conf == null) {
            throw new IllegalStateException("unexpected hand model outputs");
        }
        // Screen points are in 224-pixel units; world points are metres. Swap if the order came back the other way.
        if (world != null && maxAbs(screen) < 5 && maxAbs(world) > 5) {
            float[] t = screen;
            screen = world;
            world = t;
        }
        double confidence = conf[0];
        if (confidence < confThreshold) {
            return null;
        }

        // Map the points back: undo the crop scale, undo the rotation, add the offsets.
        double scale = Math.max((second.x2() - second.x1()) / (double) INPUT, (second.y2() - second.y1()) / (double) INPUT);
        double[] r = Blobs.affine(getRotationMatrix2D(new Point2f(0, 0), angle, 1.0));
        double invT0 = -(m[0] * m[2] + m[3] * m[5]);
        double invT1 = -(m[1] * m[2] + m[4] * m[5]);
        double ccx = (second.x1() + second.x2()) / 2.0;
        double ccy = (second.y1() + second.y2()) / 2.0;
        double originX = ccx * m[0] + ccy * m[3] + invT0;
        double originY = ccx * m[1] + ccy * m[4] + invT1;

        double[][] points = new double[21][3];
        for (int j = 0; j < 21; j++) {
            double lx = (screen[3 * j] - INPUT / 2.0) * scale;
            double ly = (screen[3 * j + 1] - INPUT / 2.0) * scale;
            double rx = lx * r[0] + ly * r[3];
            double ry = lx * r[1] + ly * r[4];
            points[j][0] = rx + originX + biasX;
            points[j][1] = ry + originY + biasY;
            points[j][2] = screen[3 * j + 2] * scale;
        }
        return new HandPose(points, confidence, handedness == null ? 0.5 : handedness[0], palm.score());
    }

    private static double maxAbs(float[] a) {
        double m = 0;
        for (float v : a) {
            m = Math.max(m, Math.abs(v));
        }
        return m;
    }

    @Override
    public void close() {
        net.close();
    }
}
