package com.dextra.vision;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_core.CV_32F;

/** Small helpers for moving data between OpenCV mats and Java arrays. */
final class Blobs {

    private Blobs() {
    }

    /**
     * Wraps an H x W float RGB image as a 1 x H x W x 3 tensor. The hand models were converted
     * from TensorFlow Lite, so they take channels-last input (NHWC), not OpenCV's usual NCHW blob.
     */
    static Mat nhwc(Mat float3) {
        if (float3.type() != org.bytedeco.opencv.global.opencv_core.CV_32FC3 || !float3.isContinuous()) {
            throw new IllegalArgumentException("expected a continuous CV_32FC3 image");
        }
        Mat blob = new Mat(4, new int[]{1, float3.rows(), float3.cols(), 3}, CV_32F);
        Pointer.memcpy(blob.data(), float3.data(), float3.total() * float3.elemSize());
        return blob;
    }

    /** Copies a float tensor of any shape into a flat array. */
    static float[] toArray(Mat floats) {
        float[] out = new float[(int) floats.total() * floats.channels()];
        new FloatPointer(floats.data()).get(out);
        return out;
    }

    /** Reads a 2 x 3 CV_64F affine matrix as [m00, m01, m02, m10, m11, m12]. */
    static double[] affine(Mat m) {
        try (DoubleIndexer idx = m.createIndexer()) {
            return new double[]{idx.get(0, 0), idx.get(0, 1), idx.get(0, 2), idx.get(1, 0), idx.get(1, 1), idx.get(1, 2)};
        }
    }
}
