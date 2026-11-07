package com.dextra.vision;

/**
 * The 21 hand points from the hand model, in the frame's pixel coordinates.
 * Point numbering follows MediaPipe: 0 is the wrist, 8 is the index fingertip.
 *
 * @param points     21 rows of [x, y, z]; z is depth relative to the wrist, in pixel-like units
 * @param confidence hand-presence confidence from the model, 0 to 1
 * @param handedness 0 means left, 1 means right (as the model sees the image)
 * @param palmScore  score of the palm detection this pose came from
 */
public record HandPose(double[][] points, double confidence, double handedness, double palmScore) {

    public static final int INDEX_FINGERTIP = 8;

    public double fingertipX() {
        return points[INDEX_FINGERTIP][0];
    }

    public double fingertipY() {
        return points[INDEX_FINGERTIP][1];
    }
}
