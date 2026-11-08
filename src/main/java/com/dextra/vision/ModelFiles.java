package com.dextra.vision;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/** Finds the bundled ONNX models on disk. OpenCV needs a file path, so a model inside a jar is copied out once. */
public final class ModelFiles {

    public static final String PALM = "palm_detection_mediapipe_2023feb.onnx";
    public static final String HAND = "handpose_estimation_mediapipe_2023feb.onnx";

    private ModelFiles() {
    }

    public static String path(String name) {
        URL url = ModelFiles.class.getResource("/models/" + name);
        if (url == null) {
            throw new IllegalStateException("model not found on the classpath: models/" + name);
        }
        try {
            if ("file".equals(url.getProtocol())) {
                return Paths.get(url.toURI()).toString();
            }
            Path out = Files.createTempFile("dextra-", "-" + name);
            try (InputStream in = url.openStream()) {
                Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
            }
            out.toFile().deleteOnExit();
            return out.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
