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
    /** Fingertip point numbers, thumb to pinky. */
    public static final int[] FINGERTIPS = {4, 8, 12, 16, 20};
    private static final int INDEX_KNUCKLE = 5;
    private static final int PINKY_KNUCKLE = 17;

    /** The five fingertips, thumb first, as x0, y0, ..., x4, y4: x as a share of width, y as a share of height. */
    public double[] fingertips(double width, double height) {
        double[] out = new double[10];
        for (int k = 0; k < 5; k++) {
            out[2 * k] = points[FINGERTIPS[k]][0] / width;
            out[2 * k + 1] = points[FINGERTIPS[k]][1] / height;
        }
        return out;
    }

    /** Palm width, index knuckle to pinky knuckle, as a share of the frame width. A ruler that scales with distance. */
    public double palmWidth(double width) {
        return Math.hypot(points[INDEX_KNUCKLE][0] - points[PINKY_KNUCKLE][0],
                points[INDEX_KNUCKLE][1] - points[PINKY_KNUCKLE][1]) / width;
    }

    public double fingertipX() {
        return points[INDEX_FINGERTIP][0];
    }

    public double fingertipY() {
        return points[INDEX_FINGERTIP][1];
    }
}
