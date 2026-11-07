package com.dextra.vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_core.StringVector;
import org.bytedeco.opencv.opencv_dnn.Net;

import static org.bytedeco.opencv.global.opencv_core.BORDER_CONSTANT;
import static org.bytedeco.opencv.global.opencv_core.CV_32F;
import static org.bytedeco.opencv.global.opencv_core.copyMakeBorder;
import static org.bytedeco.opencv.global.opencv_dnn.readNetFromONNX;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

/**
 * Finds the strongest palm in a BGR frame with MediaPipe's palm detection model.
 * A Java port of OpenCV Zoo's mp_palmdet.py (Apache 2.0), keeping only the top palm,
 * since DEXTRA tracks one hand.
 */
public final class PalmDetector implements AutoCloseable {

    static final int INPUT = 192;
    static final int ANCHOR_COUNT = 2016;
    private static final int VALUES_PER_ANCHOR = 18; // box (4) + 7 landmarks (14)

    private final Net net;
    private final float[][] anchors = anchors();
    private final double scoreThreshold;

    public PalmDetector(String modelPath, double scoreThreshold) {
        this.net = readNetFromONNX(modelPath);
        this.scoreThreshold = scoreThreshold;
    }

    /**
     * Anchor centres for the 192 x 192 model: a 24 x 24 grid with 2 anchors per cell (stride 8),
     * then a 12 x 12 grid with 6 per cell (the three stride-16 layers). 2016 in total, row by row.
     * Checked against the 2016 hard-coded anchors in mp_palmdet.py: identical.
     */
    static float[][] anchors() {
        float[][] a = new float[ANCHOR_COUNT][];
        int i = 0;
        for (int y = 0; y < 24; y++) {
            for (int x = 0; x < 24; x++) {
                for (int k = 0; k < 2; k++) {
                    a[i++] = new float[]{(x + 0.5f) / 24f, (y + 0.5f) / 24f};
                }
            }
        }
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 12; x++) {
                for (int k = 0; k < 6; k++) {
                    a[i++] = new float[]{(x + 0.5f) / 12f, (y + 0.5f) / 12f};
                }
            }
        }
        return a;
    }

    /** Returns the strongest palm, or null when no palm scores above the threshold. */
    public Palm detect(Mat bgr) {
        int h = bgr.rows();
        int w = bgr.cols();

        // Letterbox into 192 x 192, keeping the aspect ratio (same as the Python preprocess).
        double ratio = Math.min((double) INPUT / h, (double) INPUT / w);
        int rh = (int) (h * ratio);
        int rw = (int) (w * ratio);
        Mat resized = new Mat();
        resize(bgr, resized, new Size(rw, rh));
        int padH = INPUT - rh;
        int padW = INPUT - rw;
        int left = padW / 2;
        int top = padH / 2;
        Mat padded = new Mat();
        copyMakeBorder(resized, padded, top, padH - top, left, padW - left, BORDER_CONSTANT, new Scalar(0, 0, 0, 0));
        Mat rgb = new Mat();
        cvtColor(padded, rgb, COLOR_BGR2RGB);
        Mat input = new Mat();
        rgb.convertTo(input, CV_32F, 1 / 255.0, 0);
        int padBiasX = (int) (left / ratio);
        int padBiasY = (int) (top / ratio);

        net.setInput(Blobs.nhwc(input));
        StringVector names = net.getUnconnectedOutLayersNames();
        MatVector outs = new MatVector();
        net.forward(outs, names);

        float[] regressors = null;
        float[] scores = null;
        for (long i = 0; i < outs.size(); i++) {
            Mat out = outs.get(i);
            long total = out.total();
            if (total == (long) ANCHOR_COUNT * VALUES_PER_ANCHOR) {
                regressors = Blobs.toArray(out);
            } else if (total == ANCHOR_COUNT) {
                scores = Blobs.toArray(out);
            }
        }
        if (regressors == null || scores == null) {
            throw new IllegalStateException("unexpected palm model outputs");
        }

        int best = -1;
        double bestScore = 0;
        for (int i = 0; i < ANCHOR_COUNT; i++) {
            double s = 1.0 / (1.0 + Math.exp(-scores[i]));
            if (s > bestScore) {
                bestScore = s;
                best = i;
            }
        }
        if (best < 0 || bestScore < scoreThreshold) {
            return null;
        }

        double scale = Math.max(w, h);
        int o = best * VALUES_PER_ANCHOR;
        float ax = anchors[best][0];
        float ay = anchors[best][1];
        double cx = regressors[o] / INPUT + ax;
        double cy = regressors[o + 1] / INPUT + ay;
        double bw = regressors[o + 2] / INPUT;
        double bh = regressors[o + 3] / INPUT;
        double x1 = (cx - bw / 2) * scale - padBiasX;
        double y1 = (cy - bh / 2) * scale - padBiasY;
        double x2 = (cx + bw / 2) * scale - padBiasX;
        double y2 = (cy + bh / 2) * scale - padBiasY;

        double[][] landmarks = new double[7][2];
        for (int k = 0; k < 7; k++) {
            landmarks[k][0] = (regressors[o + 4 + 2 * k] / INPUT + ax) * scale - padBiasX;
            landmarks[k][1] = (regressors[o + 5 + 2 * k] / INPUT + ay) * scale - padBiasY;
        }
        return new Palm(x1, y1, x2, y2, landmarks, bestScore);
    }

    @Override
    public void close() {
        net.close();
    }
}
