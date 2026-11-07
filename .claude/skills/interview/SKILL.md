---
name: interview
description: Interview user in-depth to create a detailed spec
allowed-tools: AskUserQuestion, Write, Agent
---

Follow the user instructions and interview me in detail using the AskUserQuestionTool about literally anything: technical implementation, UI & UX, concerns, tradeoffs, etc. but make sure the questions are not obvious. be very in-depth and continue interviewing me continually until it's complete. then, write the spec to a file.

**Save location:** `<cwd>/.plans/<relevant-name>.md`

- `<cwd>` = the current working directory — the project you are actually working in. Specs live with the code they describe so they are easy to find, open in an editor, and commit alongside the work.
- `<relevant-name>` = a short kebab-case slug describing the spec topic, ending in `-spec.md` (e.g. `friends-tab-redesign-spec.md`).
- Create the `.plans/` directory if it does not exist — the Write tool creates parent directories automatically.
- Always resolve and write the absolute path, and state the path you wrote to in your reply.
- Do not save specs under `~/.claude/` — they become impossible to find there.

<instructions>$ARGUMENTS</instructions>

---

## Hackathon mode (added Oct 14, 2026)

Use this mode when the arguments or the conversation name a hackathon, an event, a rules page or a Devpost link, or the project holds hackathon-kit's `CHECKLIST.md`. If that's unclear, ask once, with options.

1. **Read the two companion files first.** `winning-questions.md` (the question bank) and `IDEA-TEMPLATE.md` (the spec structure) sit in this skill's folder, next to this SKILL.md. Read both in full before the first question. If the Read tool isn't available, spawn an Agent to return their full text.

2. **Stage 0: the live rules page, read today.** Spawn a read-only Agent (one with no Edit or Write tools, such as the Explore type) to read the live rules page and every linked rules, FAQ, judging, update and resources page for this event. It returns the Rules sheet from `winning-questions.md`: each field copied word for word with its source URL, or "not on the page", and the deadline both as written and in Toronto time. It reads with GET requests only (WebFetch). It never registers, logs in, submits, posts or fills in a form. If it needs a browser, it first blocks every non-GET request in the browser itself, rather than only being told not to write. It never opens a `.env` file. If the page won't load, ask Sid to paste it. Never fill a field from memory or an old note. Map the event's criteria onto the five in the bank, show Sid the sheet and the map, then ask Q0.1.

3. **Every question has options, in plain words.** Ask everything through AskUserQuestion with 2 to 4 options: the bank's questions, their follow-ups, re-asks, Gate A and guided hunts. No chat-only or free-text questions. Where Sid must type specifics, the options show the shapes, and he types his version in Other. Copy each question's header, options and multiSelect from the bank, and keep its plain-words rule (bank, How to use, item 4): everyday words and his project's own terms, no kit jargon, and no other project named as an example of his. If he says he's confused, re-explain once with an everyday analogy, then ask again.

4. **Recommended first.** Put "(Recommended)" on the first option wherever the bank has one, and keep the bank's evidence in its description, including "Weak evidence:" where the bank says so. Where a recommendation depends on an earlier answer, put the matching option first. On architecture questions, the recommendation is a choice or a constraint the winners used, offered as a suggestion Sid can take or reject, never a design.

5. **Follow the CORE path in stage order.** 1 to 4 questions per call, as in the bank's CORE path table. Ask an OPTIONAL question only when its "ask when" holds. Apply the skip rules (solo, no bonus, model or API contest, one required product). Script-time questions wait until a script exists.

6. **Gate A: his three lines, first.** Right after Stage 0 (the rules), before Stage 1 and before any [ARCH] question, ask GA.1 to GA.3. They are his design: the main flow step by step, what one record holds, and one trade-off he'd defend with its cost. Before each, say the bank's "say first" note (the line in plain words with an everyday analogy); he types his own line in Other, and the options only offer help, with no Recommended mark and no other project named. Stages 1 to 3 then check his lines. Critique with the bank's [ARCH] questions, not with a design. (Changed Oct 30, 2026, after the first real run: Gate A used to come after Stage 3, with winners' examples as its options, and they confused him.) If he's stuck, run a guided hunt as a question with options: the exact source and section, one thing to find, and a 5 to 10 minute silent timer. If the timer runs out, give one small worked example of the stuck part only; he explains it back, and the design goes back to him. Never write the architecture, even when asked. After 9pm, ask the bank's Pause question (P1) before Gate A, and again before Stage 4.

7. **Keep asking until the plan gives him the best chance of winning.** After each stage, check his answers against that section's gap check in `IDEA-TEMPLATE.md` and the bank's red flags. Re-ask with the bank's R1 question when an answer matches a red flag, the gap check would be "no", an answer falls below the winners' bar, two answers contradict, or his Other text is vague. R1 states the gap, the winners' bar with its count, and the recommended alternative. Allow at most 2 re-asks per section; after that, log it in the spec's Known risks (question ID, his answer, the bar, the criterion it costs, rounds used, his reason) and move on. Stage One lines can't be kept as a risk: fix them, or ask (R1, Stage One form) whether to rethink the idea. A deliberate cut goes through R2 and is written "cut, because ...".

8. **Win-readiness check before the spec.** Run W1 against the Judge's 60-second test, the Scorecard verdict for Technical, Design, Impact, Creativity and Presentation, and every Stage One line, all in `IDEA-TEMPLATE.md`. Any failing line, including a logged risk, sends the interview back to its stage for one more R1 round. The interview ends only when every section passes, is a deliberate cut, or is a logged risk after its rounds, and W1 has run.

9. **Write the spec in `IDEA-TEMPLATE.md`'s structure.** Same section order, his words, and a Status on every section (pass; cut, because ...; risk). Carry the [uncertain] and [inference] labels. Fill in the Known risks table, the Win-readiness check and the Scorecard for this event. Save it where this skill says (`<cwd>/.plans/<event-slug>-spec.md`) and state the absolute path.

The bank's CORE path is the floor, not the ceiling. Non-obvious questions beyond it are still welcome, as the instructions above say, and each one also gets options.
