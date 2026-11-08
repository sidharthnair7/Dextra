package com.dextra.vision;

import org.bytedeco.opencv.opencv_core.Mat;

/** Palm detector plus hand landmarker: one BGR frame in, the hand's 21 points out (or null). Not thread-safe. */
public final class HandFinder implements AutoCloseable {

    private final PalmDetector palms;
    private final HandLandmarker hands;

    public HandFinder(double palmScoreThreshold, double handConfidenceThreshold) {
        this.palms = new PalmDetector(ModelFiles.path(ModelFiles.PALM), palmScoreThreshold);
        this.hands = new HandLandmarker(ModelFiles.path(ModelFiles.HAND), handConfidenceThreshold);
    }

    public HandPose find(Mat bgr) {
        Palm palm = palms.detect(bgr);
        return palm == null ? null : hands.estimate(bgr, palm);
    }

    @Override
    public void close() {
        palms.close();
        hands.close();
    }
}
