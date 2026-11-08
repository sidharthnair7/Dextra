package com.dextra.vision;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class HandFinderTest {

    private static HandFinder finder;

    @BeforeAll
    static void load() {
        finder = new HandFinder(0.6, 0.8);
    }

    @AfterAll
    static void close() {
        finder.close();
    }

    @Test
    void anchorGridMatchesMediaPipePalmModel() {
        float[][] a = PalmDetector.anchors();
        assertEquals(2016, a.length);
        assertArrayEquals(new float[]{0.5f / 24, 0.5f / 24}, a[0], 1e-6f);
        assertArrayEquals(new float[]{1.5f / 24, 0.5f / 24}, a[2], 1e-6f);
        assertArrayEquals(new float[]{23.5f / 24, 23.5f / 24}, a[1151], 1e-6f);
        assertArrayEquals(new float[]{0.5f / 12, 0.5f / 12}, a[1152], 1e-6f);
        assertArrayEquals(new float[]{11.5f / 12, 11.5f / 12}, a[2015], 1e-6f);
    }

    /** Sample photo (not in the repo): a woman pointing up; her index fingertip is near pixel (298, 28) of 419 x 419. */
    @Test
    void findsTheIndexFingertipInAPhoto() {
        Path photo = Path.of("test-data", "pointing_up.jpg");
        assumeTrue(Files.exists(photo), "test-data/pointing_up.jpg not present");
        Mat image = imread(photo.toString());

        HandPose pose = finder.find(image);
        long start = System.nanoTime();
        int runs = 20;
        for (int i = 0; i < runs; i++) {
            finder.find(image);
        }
        double msPerFrame = (System.nanoTime() - start) / 1e6 / runs;

        assertNotNull(pose, "no hand found");
        System.out.printf("fingertip=(%.1f, %.1f) conf=%.3f palm=%.3f hand=%.2f, %.1f ms per frame%n",
                pose.fingertipX(), pose.fingertipY(), pose.confidence(), pose.palmScore(), pose.handedness(), msPerFrame);
        assertTrue(Math.abs(pose.fingertipX() - 298) < 20, "fingertip x " + pose.fingertipX());
        assertTrue(Math.abs(pose.fingertipY() - 28) < 20, "fingertip y " + pose.fingertipY());
    }

    @Test
    void findsAHandInOtherPhotos() {
        for (String name : new String[]{"victory.jpg", "thumbs_up.jpg"}) {
            Path photo = Path.of("test-data", name);
            assumeTrue(Files.exists(photo), "test-data/" + name + " not present");
            HandPose pose = finder.find(imread(photo.toString()));
            assertNotNull(pose, "no hand found in " + name);
            System.out.printf("%s: fingertip=(%.1f, %.1f) conf=%.3f%n", name, pose.fingertipX(), pose.fingertipY(), pose.confidence());
        }
    }
}
