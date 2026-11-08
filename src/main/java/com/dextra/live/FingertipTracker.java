package com.dextra.live;

import com.dextra.vision.HandFinder;
import com.dextra.vision.HandPose;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.bytedeco.opencv.global.opencv_core.flip;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imencode;
import static org.bytedeco.opencv.global.opencv_imgproc.INTER_AREA;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.bytedeco.opencv.global.opencv_videoio.CAP_ANY;
import static org.bytedeco.opencv.global.opencv_videoio.CAP_DSHOW;
import static org.bytedeco.opencv.global.opencv_videoio.CAP_MSMF;
import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_HEIGHT;
import static org.bytedeco.opencv.global.opencv_videoio.CAP_PROP_FRAME_WIDTH;

/** Owns the webcam: reads frames, finds the fingertip, and streams one TrackPoint per frame. */
@Component
public class FingertipTracker implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(FingertipTracker.class);

    private final LiveSocket socket;
    private final List<TrackPointListener> listeners;
    private final boolean enabled;
    private final int cameraIndex;
    private final int width;
    private final int height;
    private final double palmThreshold;
    private final double handThreshold;

    private volatile boolean running;
    private volatile String status = "not started";
    private volatile TrackPoint last;
    private Thread thread;
    /** Waiting snapshot requests; the tracker thread fills each with the next frame as a JPEG. */
    private final ConcurrentLinkedQueue<CompletableFuture<byte[]>> snapshotRequests = new ConcurrentLinkedQueue<>();

    /** One half-size preview frame for the page's corner view; seq goes up by one per new frame. */
    public record Preview(long seq, byte[] jpeg) {
    }

    private static final long PREVIEW_INTERVAL_NANOS = 66_000_000L;   // about 15 previews a second
    private final AtomicInteger previewViewers = new AtomicInteger();
    private volatile Preview preview;

    public FingertipTracker(LiveSocket socket,
                            List<TrackPointListener> listeners,
                            @Value("${dextra.camera.enabled:true}") boolean enabled,
                            @Value("${dextra.camera.index:0}") int cameraIndex,
                            @Value("${dextra.camera.width:640}") int width,
                            @Value("${dextra.camera.height:480}") int height,
                            @Value("${dextra.palm.score-threshold:0.6}") double palmThreshold,
                            @Value("${dextra.hand.confidence-threshold:0.8}") double handThreshold) {
        this.socket = socket;
        this.listeners = listeners;
        this.enabled = enabled;
        this.cameraIndex = cameraIndex;
        this.width = width;
        this.height = height;
        this.palmThreshold = palmThreshold;
        this.handThreshold = handThreshold;
    }

    @Override
    public void start() {
        if (!enabled) {
            setStatus("camera disabled");
            return;
        }
        running = true;
        thread = new Thread(this::run, "fingertip-tracker");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() {
        running = false;
        if (thread != null) {
            try {
                thread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    public String status() {
        return status;
    }

    public TrackPoint last() {
        return last;
    }

    /**
     * The next camera frame (mirrored, as the user sees it) as JPEG bytes. Completes within one frame
     * while the camera runs; the caller should use a timeout, since it never completes with the camera off.
     */
    public CompletableFuture<byte[]> snapshot() {
        CompletableFuture<byte[]> f = new CompletableFuture<>();
        snapshotRequests.add(f);
        return f;
    }

    /** The latest preview frame, or null before the first one. Previews are only made while someone watches. */
    public Preview preview() {
        return preview;
    }

    public void addPreviewViewer() {
        previewViewers.incrementAndGet();
    }

    public void removePreviewViewer() {
        previewViewers.decrementAndGet();
    }

    /** Encodes a frame as JPEG bytes. */
    public static byte[] toJpeg(Mat frame) {
        try (BytePointer buf = new BytePointer()) {
            if (!imencode(".jpg", frame, buf)) {
                throw new IllegalStateException("JPEG encoding failed");
            }
            byte[] out = new byte[(int) buf.limit()];
            buf.get(out);
            return out;
        }
    }

    private void setStatus(String msg) {
        status = msg;
        log.info("tracker: {}", msg);
        socket.status(msg);
    }

    private VideoCapture openCamera() {
        boolean windows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
        int[] apis = windows ? new int[]{CAP_DSHOW, CAP_MSMF, CAP_ANY} : new int[]{CAP_ANY};
        for (int api : apis) {
            VideoCapture cap = new VideoCapture(cameraIndex, api);
            if (cap.isOpened()) {
                cap.set(CAP_PROP_FRAME_WIDTH, width);
                cap.set(CAP_PROP_FRAME_HEIGHT, height);
                return cap;
            }
            cap.release();
        }
        return null;
    }

    private void run() {
        VideoCapture cap = openCamera();
        if (cap == null) {
            setStatus("camera " + cameraIndex + " could not be opened");
            running = false;
            return;
        }
        try (HandFinder finder = new HandFinder(palmThreshold, handThreshold)) {
            Mat frame = new Mat();
            Mat mirrored = new Mat();
            Mat small = new Mat();
            long previewSeq = 0;
            long lastPreview = 0;
            long t0 = System.nanoTime();
            long prev = t0;
            double fps = 0;
            boolean reported = false;
            while (running) {
                if (!cap.read(frame) || frame.empty()) {
                    setStatus("no frames from camera " + cameraIndex);
                    Thread.sleep(100);
                    continue;
                }
                if (!reported) {
                    setStatus("camera " + cameraIndex + " open, " + frame.cols() + "x" + frame.rows());
                    reported = true;
                }
                flip(frame, mirrored, 1);
                if (previewViewers.get() > 0 && System.nanoTime() - lastPreview > PREVIEW_INTERVAL_NANOS) {
                    resize(mirrored, small, new Size(mirrored.cols() / 2, mirrored.rows() / 2), 0, 0, INTER_AREA);
                    preview = new Preview(++previewSeq, toJpeg(small));
                    lastPreview = System.nanoTime();
                }
                if (!snapshotRequests.isEmpty()) {
                    byte[] jpeg = toJpeg(mirrored);
                    CompletableFuture<byte[]> req;
                    while ((req = snapshotRequests.poll()) != null) {
                        req.complete(jpeg);
                    }
                }
                HandPose pose = finder.find(mirrored);
                long now = System.nanoTime();
                double inst = 1e9 / Math.max(1, now - prev);
                prev = now;
                fps = fps == 0 ? inst : 0.9 * fps + 0.1 * inst;
                long t = (now - t0) / 1_000_000;
                double w = mirrored.cols();
                double h = mirrored.rows();
                TrackPoint p = pose == null
                        ? new TrackPoint(t, false, 0, 0, 0, fps)
                        : new TrackPoint(t, true, pose.fingertipX() / w, pose.fingertipY() / h, pose.confidence(), fps,
                        pose.fingertips(w, h), pose.palmWidth(w));
                last = p;
                socket.broadcast(p.toJson());
                for (TrackPointListener l : listeners) {
                    try {
                        l.onPoint(p);
                    } catch (Exception e) {
                        log.warn("listener failed", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("tracker stopped", e);
            setStatus("tracker error: " + e.getMessage());
        } finally {
            cap.release();
            running = false;
        }
    }
}
