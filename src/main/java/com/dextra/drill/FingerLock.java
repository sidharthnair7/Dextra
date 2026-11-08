package com.dextra.drill;

import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The two fingers this person uses, picked at the start: by pinching them together three times, or by tapping.
 * The person picks, not the app: the hand model returns all five fingertips even for a finger that isn't there,
 * so the app can't tell on its own which fingers someone has.
 */
@Component
public class FingerLock {

    public static final List<String> NAMES = List.of("thumb", "index", "middle", "ring", "pinky");
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private int first = 0;          // thumb
    private int second = 1;         // index
    private String lockedBy = "default";

    public synchronized void set(int a, int b, String how) {
        if (a < 0 || a > 4 || b < 0 || b > 4) {
            throw new IllegalArgumentException("finger number out of range");
        }
        if (a == b) {
            throw new IllegalArgumentException("pick two different fingers");
        }
        first = Math.min(a, b);
        second = Math.max(a, b);
        lockedBy = how;
    }

    public void set(String a, String b, String how) {
        set(indexOf(a), indexOf(b), how);
    }

    static int indexOf(String name) {
        int i = name == null ? -1 : NAMES.indexOf(name.trim().toLowerCase());
        if (i < 0) {
            throw new IllegalArgumentException("unknown finger: " + name + " (use thumb, index, middle, ring or pinky)");
        }
        return i;
    }

    /** Where the instrument goes and how open its jaws are, from one frame's fingertips. */
    public record Steer(double x, double y, double gap) {
    }

    /**
     * The point halfway between the two locked fingertips (the instrument's tip, and the drill's pointer), and the gap
     * between them in palm widths (the jaws: a pinch closes them). gap is -1 when the palm width isn't known.
     *
     * @param aspect frame height divided by width, so y distances count the same as x
     */
    public Steer steer(double[] tips, double palm, double aspect) {
        int a;
        int b;
        synchronized (this) {
            a = first;
            b = second;
        }
        double x = (tips[2 * a] + tips[2 * b]) / 2;
        double y = (tips[2 * a + 1] + tips[2 * b + 1]) / 2;
        double gap = palm > 0
                ? Math.hypot(tips[2 * a] - tips[2 * b], (tips[2 * a + 1] - tips[2 * b + 1]) * aspect) / palm
                : -1;
        return new Steer(x, y, gap);
    }

    /** The pair as finger numbers, thumb = 0 to pinky = 4, lower first. */
    public synchronized int[] pair() {
        return new int[]{first, second};
    }

    public synchronized Map<String, Object> view() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("fingers", List.of(NAMES.get(first), NAMES.get(second)));
        out.put("lockedBy", lockedBy);
        return out;
    }

    /** A WebSocket message announcing the lock. */
    public synchronized String json(String type) {
        ObjectNode n = JSON.createObjectNode().put("type", type);
        n.putArray("fingers").add(NAMES.get(first)).add(NAMES.get(second));
        return n.put("lockedBy", lockedBy).toString();
    }
}
