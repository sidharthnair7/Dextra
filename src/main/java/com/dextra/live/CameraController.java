package com.dextra.live;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * A live camera preview for the page's corner view, as an MJPEG stream an img tag can show.
 * It's the mirrored image, so the fingertip dot the page draws over it lines up. It never leaves this machine.
 */
@RestController
public class CameraController {

    private final FingertipTracker tracker;

    public CameraController(FingertipTracker tracker) {
        this.tracker = tracker;
    }

    @GetMapping("/api/camera.mjpg")
    public void stream(HttpServletResponse response) throws IOException {
        if (!tracker.isRunning()) {
            response.sendError(503, "camera is off");
            return;
        }
        response.setContentType("multipart/x-mixed-replace; boundary=frame");
        response.setHeader("Cache-Control", "no-store");
        OutputStream out = response.getOutputStream();
        tracker.addPreviewViewer();
        try {
            long seen = -1;
            while (tracker.isRunning()) {
                FingertipTracker.Preview p = tracker.preview();
                if (p == null || p.seq() == seen) {
                    Thread.sleep(15);
                    continue;
                }
                seen = p.seq();
                out.write(("--frame\r\nContent-Type: image/jpeg\r\nContent-Length: " + p.jpeg().length + "\r\n\r\n")
                        .getBytes(StandardCharsets.US_ASCII));
                out.write(p.jpeg());
                out.write("\r\n".getBytes(StandardCharsets.US_ASCII));
                out.flush();
            }
        } catch (IOException e) {
            // The page closed the stream.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            tracker.removePreviewViewer();
        }
    }
}
