package com.dextra.vision;

/**
 * One palm found by the palm detector, in the frame's pixel coordinates.
 *
 * @param x1        box left
 * @param y1        box top
 * @param x2        box right
 * @param y2        box bottom
 * @param landmarks 7 palm points [x, y]: wrist, the 4 finger bases (index to pinky), and 2 thumb points
 * @param score     detector score, 0 to 1
 */
public record Palm(double x1, double y1, double x2, double y2, double[][] landmarks, double score) {
}
