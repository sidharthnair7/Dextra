package com.dextra.live;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imdecode;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** The frame the setup check sends to Gemini must be a real JPEG of the same image. */
class SnapshotTest {

    @Test
    void frameEncodesToAJpegThatDecodesBack() {
        Path photo = Path.of("test-data", "pointing_up.jpg");
        assumeTrue(Files.exists(photo), "test-data/pointing_up.jpg not present");
        Mat img = imread(photo.toString());
        byte[] jpeg = FingertipTracker.toJpeg(img);
        System.out.println("snapshot: " + img.cols() + "x" + img.rows() + " -> " + jpeg.length + " bytes");
        assertTrue(jpeg.length > 1000);
        assertEquals((byte) 0xFF, jpeg[0]);
        assertEquals((byte) 0xD8, jpeg[1]);
        try (BytePointer bp = new BytePointer(jpeg)) {
            Mat back = imdecode(new Mat(1, jpeg.length, org.bytedeco.opencv.global.opencv_core.CV_8UC1, bp), IMREAD_COLOR);
            assertEquals(img.cols(), back.cols());
            assertEquals(img.rows(), back.rows());
        }
    }
}
