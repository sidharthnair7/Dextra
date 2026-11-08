# DEXTRA

**Control with precision. Improve with evidence.**

DEXTRA turns a laptop webcam into a hand precision trainer. You move two fingers, a 3D robotic instrument copies them, and DEXTRA measures how precisely you moved, with numbers it can explain. When tracking isn't good enough to trust, it refuses to score and tells you why.

Built at HackTrent 2026 by Sid Nair and Trinidad Laguardia.

> **What it is and isn't:** practice plus measured evidence of hand precision, for people like medical students who want feedback on fine motor control. It is not surgical training, not a grade, and not a diagnosis.

## What it does

- **3D robot that copies your hand.** The instrument follows the point between your two fingers, and its jaws open and close with the gap between them. Pinch, and it grips.
- **Pick your own two fingers.** Pinch them together 3 times, or tap them on screen. Any two work, so it fits people who are missing a thumb or index finger.
- **Setup check.** Hold still for 3 seconds. Java measures tracking coverage, frame rate and jitter, and decides pass or fail. Gemini looks at one camera frame and explains what's hurting tracking (like backlight) and one fix.
- **The drill.** Hit 5 rings in order. You get four numbers, each shown with its formula: accuracy, smoothness, speed and how much of the time you were tracked.
- **Refuses to fake a score.** If you didn't finish, or your hand was tracked in under 80% of frames, the attempt isn't scored, and the reason is shown.
- **Gemini coach.** After a drill, Gemini turns your numbers into two or three sentences of advice. It never decides a score. Java does that.
- **Lost-hand guidance.** If your hand leaves the camera, the arm greys out, the edge you left from glows, and an arrow points the way back. A live camera view sits in the corner.
- **Clinic mode.** A progress review for a teacher or coach: one person's attempts over time, the trend in each number, and every refused attempt.

## How it works

```
Webcam -> Java (Spring Boot) ------------------------------> Browser (Three.js)
          | hand finder: OpenCV running MediaPipe's        WebSocket /ws/live, about 30 messages/s
          |   palm and hand models, 21 points per frame    + REST /api/...
          | finger lock: point between 2 fingers + gap
          | drill + scorer: hits, 4 numbers, refusals
          | setup check: Java decides pass/fail
          | attempt log: Tiger Cloud hypertable ----------------> Tiger Cloud (Timescale)
          |   (or ~/.dextra/attempts.jsonl without one)
          | Gemini coach: explains only ---------------------> Gemini API
```

Java owns the camera and every number. The page only draws. That way the rings light up exactly when Java counts a hit, and the scores can be tested.

### The four numbers

| Number | Formula | Better is |
|---|---|---|
| Accuracy | 100 x (1 - mean distance from the straight path between rings / 0.10 screen widths) | higher |
| Smoothness | average of 100 / (speed peaks per move): one smooth move = 100 | higher |
| Speed | time from ring 1 to the last ring / number of moves | lower |
| Tracked | frames with a confident hand / all frames | higher |

## Run it

You need **Java 17 or newer** and **Maven**. The webcam must not be in use by another app.

1. Optional, for Gemini: copy your key into a `.env` file in the project folder (git ignores it):
   ```
   GEMINI_API_KEY=your-key-here
   ```
   Without a key, the coach and setup advice fall back to offline text written from the same numbers.

   Optional, for Tiger Data: add your Tiger Cloud connection string to `.env` too:
   ```
   TIGER_DATABASE_URL=postgres://user:password@host:port/tsdb?sslmode=require
   ```
   DEXTRA creates the `dextra_attempts` hypertable on startup. Without it, or if it can't connect, attempts go to `~/.dextra/attempts.jsonl`.
2. Start it:
   ```bash
   mvn spring-boot:run
   ```
   On a Mac with an Apple chip: `mvn spring-boot:run -Dnative.platform=macosx-arm64`
3. Open **http://localhost:8080**, type your name in "Practising as", press **Setup check**, then **Start drill**.

Tip: put a light in front of you, not behind. Dim rooms can drop the webcam to 15 fps, and the setup check needs 20.

## Tests

```bash
mvn test
```

57 tests cover the hand finder on real photos, scoring on synthetic paths with known answers (clean, shaky, stop-and-go, unreliable), refusals, the pinch detector, the finger lock, saving history, the progress trends and the API.

To check your Gemini key works before a demo (numbers only, no camera frame):

```bash
DEXTRA_LIVE_GEMINI=1 mvn test -Dtest=GeminiLiveCheck
```

To check Tiger Cloud (writes one test attempt, reads it back, then deletes it):

```bash
DEXTRA_LIVE_TIGER=1 mvn test -Dtest=TigerLiveCheck
```

## API

| Endpoint | What it does |
|---|---|
| `GET /api/status` | camera status, fps, hand found, Gemini online |
| `GET /api/course` | the 5 rings |
| `POST /api/check?seconds=3` | setup check, with Gemini's advice |
| `POST /api/attempts/start` | start a drill; body `{"person": "Sam"}` (optional) |
| `GET /api/attempts/{id}` | one attempt: points, hits, numbers, refusals, coach feedback |
| `GET /api/fingers`, `POST /api/fingers` | read or set the two fingers; body `{"fingers": ["thumb", "index"]}` |
| `POST /api/fingers/listen` | watch for the 3-pinch gesture (30 s) |
| `POST /api/progress` | clinic mode for one person, with a by-day view; body `{"person": "Sam"}` |
| `GET /api/people` | everyone with saved attempts |
| `GET /api/camera.mjpg` | the mirrored camera preview |
| `ws /ws/live` | live messages: `point`, `hit`, `attemptEnded`, `feedback`, `pinch`, `fingersLocked`, `status` |

## Privacy

Tracking and scoring run on your laptop. Saved attempts (name, fingers, the four numbers or the refusal reason) go to your own Tiger Cloud database when `TIGER_DATABASE_URL` is set, otherwise to one local file, `~/.dextra/attempts.jsonl`. Gemini gets your numbers for the coach, and one camera frame for the setup check, only when a key is set. No video is ever saved.

## Limits

- A webcam sees in 2D, so there's no depth.
- Not validated with medical students yet, and no clinical claim.
- Tracking needs your whole palm in view.

## Built with

Java 17, Spring Boot, JavaCV and OpenCV, MediaPipe's palm and hand landmark models (via OpenCV Zoo), Three.js, the Google Gemini API, and Tiger Cloud (TimescaleDB).
