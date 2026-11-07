# IDEA-TEMPLATE: the spec structure for a hackathon /interview

Agent 3 of 3, final version. Written Wed Oct 14, 2026, evening. The question IDs point into `winning-questions.md` in this folder. Every winning example comes from the AWS Agents for Humans (AfH) winners unless it says otherwise; (video) means the Oct 14 caption read of all 10 winners' videos and FlakeProof's.

## How the interview uses this

- The final spec follows these sections, in this order. Copy the structure into `<cwd>/.plans/<event-slug>-spec.md`.
- Each section is filled in Sid's words, or marked "cut, because ...".
- Each section has: **Filled by** (CORE questions first, OPTIONAL ones in brackets), **Instruction**, **The winners' bar** (what a winning answer looks like, with counts), **Gap check**, the fields, and a **Status**.
- **Status** is one of: `pass` · `cut, because ...` · `risk` (logged in Known risks after 2 re-asks). Stage One lines can't be `risk`.
- The keep-asking loop (winning-questions.md, How to use, items 9 to 13) runs each section's gap check after its stage. The interview ends only when every section has a status and the Win-readiness check at the end has run.
- Labels from the research ([uncertain], [inference]) stay on the line where they apply.

**Header**
- Event: ______________________________
- Rules page URL, and when it was read: ______________________________
- Spec written (date, time): ______________________________
- Solo or pair (and who): ______________________________
- Submit target (Q0.2) ______ Halfway check (Q13.1) ______ Feature freeze (Q13.1) ______

---

## 0. Rules and field
**Filled by:** the Rules sheet (Stage 0 agent), Q0.1, Q0.2, Q0.3 [Q0.4]

**Instruction:** every field copied from the live rules page today, with its URL; nothing from memory or an old note.

**The winners' bar:** AfH's Technical criterion said, word for word, "A live demo and/or Amazon Bedrock AgentCore deployment will strengthen this score." Shopbell, Instanter and OneDecision all deployed exactly there, and 9 of 10 winners used AgentCore live.

**Gap check:** Was every field copied from the live page today, with its URL, and is the deadline also written in Toronto time? yes / no

- Contest type (cloud/agent, model/API, small student, sponsor showcase), and the skip rules it sets: ______________________________
- Theme text, word for word: ______________________________
- Deadline as written ______ In Toronto ______ Editable after submitting? ______
- Video: min ______ max ______ platform ______ public or unlisted ______ must show ______
- Required tech, by name: ______________________________
- Stage One (pass/fail) checks, and the judging method; may judges score from video and text alone? ______________________________
- Criteria and weights as written, and the tie-break: ______________________________
- Mapped onto the five (Technical, Design, Impact, Creativity, Presentation): ______________________________
- Any criterion line that names a product, a deployment or a live demo, word for word: ______________________________
- Registered ______ prizes ______ tracks ______ developer-tools track? ______
- Bonus items and their exact conditions: ______________________________
- Pre-existing-work rule; repo and licence rules; what is reused and declared (Q0.4): ______________________________
- Organizer posts, build sessions, judge talks: read or watched? example users named? deploy target? ______________________________
- Sponsor product family, and the newest feature with its date: ______________________________
- Submit target, with every link public (Q0.2): ______________________________

**Status:** ______

---

## Gate A. His three lines (asked right after section 0)
**Filled by:** GA.1, GA.2, GA.3, asked right after Stage 0; checked by Stages 1 to 3; critiqued by the [ARCH] questions Q4.1, Q5.1, Q6.1, Q8.1, Q9.1

**Instruction:** his design, in his words. Each line is explained first in plain words with an everyday analogy (a pizza app, a library app); he types his own line; the options only offer help, with no Recommended mark and no other project named. The interviewer checks the lines against Stages 1 to 3, critiques with option questions, runs a guided hunt if he's stuck, gives one small worked example of the stuck part only if the hunt's timer runs out, and never writes the design.

**The winners' bar:** none for his method (CLAUDE.md rule 13). The test: every step in line 1 acts in the demo run, each decision in line 2 sits next to its reason, and line 3 names its cost.

**Gap check:** Did he write all three lines himself before Stage 1 and any [ARCH] question, does every step in line 1 act in the demo run, and does line 3 name its cost? yes / no

- Line 1, the main flow, step by step (his words): ______________________________
- Line 2, what one record holds (his fields): ______________________________
- Line 3, the trade-off he'd defend, and its cost: ______________________________
- Where Stages 1 to 3 changed a line, and how: ______________________________
- Guided hunts run (source, section, question, his answer): ______________________________
- Worked example given, if any (which part only): ______________________________

**Status:** ______

---

## 1. The person and the chore
**Filled by:** Q1.1, Q1.2, Q1.3, Q1.4 [Q1.5]

**Instruction:** one person, as a role at a place, with one recurring chore; never a group.

**The winners' bar:** 10 of 10 name one person with one chore; 6 of 10 name the person or are the user (Tally's Rosa, OneDecision's Dana). Instanter: an eviction-defense clinic in Fulton County and its deadlines, every weekday.

**Gap check:** Can you say the role, the place, the chore and how often it happens in one sentence, with no group words, from a source better than a guess? yes / no

- Whose idea (his own, organizer example, he's the user, AI-suggested): ______________________________
- The person (role, place, first name if any): ______________________________
- The chore: ______________________________
- How often, and what starts it (clock time or arrival): ______________________________
- How exact the setting is (Q1.5): ______________________________
- How he knows the chore (does it, asked someone, read their words): ______________________________
- The detail an outsider would get wrong (raw material for the opening, Q10.1): ______________________________

**Status:** ______

---

## 2. The pain in numbers
**Filled by:** Q2.1, Q2.2 [Q2.3]

**Instruction:** count what the person handles each time, how much of it needs them, and what it costs them today.

**The winners' bar:** Shopbell: 761 items read, 30 surfaced, 4 needing a decision that day (its video says 773 read in two weeks [uncertain which]). Instanter: 4 of 46 deadlines that hand-counting gets wrong.

**Gap check:** Is there a count of items per run, a count needing the person, and a cost the person would say out loud? yes / no

- Items per run: ______ Of those, needing the person: ______
- What it costs them today, in their units: ______________________________
- How they get it wrong today, and whether it can be counted on a set (Q2.3): ______________________________

**Status:** ______

---

## 3. The idea, and why it isn't an established developer tool
**Filled by:** Q3.1, Q3.2, Q3.3, Q3.4 [Q3.5, Q3.6]

**Instruction:** one line in the person's words, checked against what already exists and against the theme text.

**The winners' bar:** Shopbell: for an independent grocery owner in San José, it reads recalls, council agendas and permits each morning and surfaces only the few items that need a decision; its pitch is that staying quiet is the product. An ordinary chore in an exact setting, not a developer tool and not a new algorithm.

**Gap check:** Can you name the closest existing entry or tool, say in one line what's different about your person or setting, and point to the theme phrases the demo itself matches? yes / no

- Logline: "For ____________, ____ does ____________ by ____________, so ____________."
- Outcome shape (a number they'd notice, nothing missed, done before it hurts, taught once): ______
- Closest prior art found ("none found by search" if none), and what's different: ______________________________
- With the sponsor tech removed, is it a developer tool (Q3.5)? ______________________________
- The one call the person can't afford to get wrong (where "AI proposes, rules decide, it refuses with evidence" goes): ______________________________
- What a finished run leaves in the world (Q3.6) [inference: P26]: ______________________________
- Theme phrases matched by what the demo does: ______________________________

**Status:** ______

---

## 4. Sponsor tech
**Filled by:** Q4.1, Q4.2 (skip if one required product), Q11.2

**Instruction:** the sponsor's newest managed runtime or feature, deployed and used in the run, named in code and in the write-up (on AWS: AgentCore Runtime, started by EventBridge).

**The winners' bar:** 9 of 10 used AgentCore live; all 3 Professional winners ran on AgentCore Runtime. Shopbell won Professional Gold with one Strands agent, AgentCore sessions and EventBridge every morning through Lambda (video, 3:00 to 3:35), so depth came from deployment and the trigger, not agent count.

**Gap check:** Does every required product do real work in the demo run, in a file you can name, with a trace from a real run by halfway? yes / no

- Sponsor features that do real work, and what each does (or, in a model contest, the newest model capability and where a judge sees it): ______________________________
- Where the agent runs while judges look: ______________________________
- Each required product's visible job (Q4.2): ______________________________
- File that calls each sponsor product: ______________________________
- Anything described that never runs (remove it): ______________________________

**Status:** ______

---

## 5. What runs on its own, and when it surfaces
**Filled by:** Q5.1, Q5.2 (and its "On a no" follow-up); shown in Q10.3; quiet items in Q9.1

**Instruction:** a trigger starts each run, not a person; it stays quiet on most items and surfaces only the decision.

**The winners' bar:** all 3 Professional winners run without a person starting them: Shopbell at 6am, Instanter at 7am on weekdays, OneDecision on a check-in event (video, 0:30). Tally: at most two questions a night. 10 of 10 show the human decision explicitly in the video.

**Gap check:** Could a judge see a run that nobody started, with its time, and the one decision it surfaced? yes / no

- What starts each run (clock time or event; model contest: what makes the result already waiting [inference]): ______________________________
- What reaches the person, how many items on a normal day, and which action waits for their yes: ______________________________
- What a "no" changes (only that item, the rule, or someone else): ______________________________
- How a judge sees the unattended run (Q10.3): ______________________________

**Status:** ______

---

## 6. The deterministic core and the refusal
**Filled by:** Q3.3, Q6.1, Q6.2, Q5.2's follow-up

**Instruction:** name the decision the model may never make, the code that makes it, what stops the model routing around it, and what it refuses.

**The winners' bar:** 8 of 10 winners say aloud that code, not the model, decides (video). Instanter computes deadlines in plain Python with a statute trace. StepSpotter's gate held against a prompt written to defeat it. Tally refused 13 of 13 adversarial cases; Doorstep refused 20 of 20 forbidden actions. FlakeProof also had a deterministic core and lost, so this is needed, not the win.

**Gap check:** If the model returned its worst possible output, would the rule still stop the wrong action, and is each kind of refusal counted on the case set? yes / no

- The call the model may never make, and the code that makes it: ______________________________
- What the model is allowed to do (read, extract, propose, explain): ______________________________
- What stops the model routing around the rule (his mechanism): ______________________________
- What it refuses, the evidence shown each time, and how many of each go in the case set: ______________________________
- How a refusal looks on screen (a result with its reason, not an error): ______________________________

**Status:** ______

---

## 7. Proof and breadth
**Filled by:** Q7.1, Q7.2 [Q7.3, Q2.3]

**Instruction:** dozens of labelled cases (synthetic is fine, labelled as such), a held-out set, adversarial cases, and one headline number the app computes.

**The winners' bar:** OneDecision: 24 labelled cases, 11 automated, 13 escalated, 0 wrong. Tally: results on unseen photos, 13 of 13 adversarial cases refused. StepSpotter: a published test set, 6 of 6 trick photos rejected, one real miss reported (video). 8 of 10 winners used synthetic data.

**Gap check:** Is the headline number computed by the app on held-out cases you never tuned on, with "wrong" defined before the run? yes / no

- Case set size, who labels it, and the labels written before the agent runs on them: ______________________________
- Held-out cases, and when they were set aside: ______________________________
- What counts as "wrong", written before running; how escalations count: ______________________________
- Headline number (filter ratio, zero-error count, errors versus by hand, adversarial refused): ______________________________
- Real, synthetic or mixed; one live feed, if any (Q7.3) [inference: only Shopbell did this]: ______________________________
- Any per-case code or config (should be none): ______________________________

**Status:** ______

---

## 8. Live deployment
**Filled by:** Q8.1, Q8.2 [Q8.3]

**Instruction:** the judge's link calls the deployed agent right then, with no login, takes their own input, and says on screen what's live and what isn't.

**The winners' bar:** 9 of 10 had a public live URL; of the 8 contrast entries, 5 had one but only 2 ran the agent live. 5 of 10 invite the viewer to try it (video): Instanter, no account and no key; Tally, photograph your own lunch.

**Gap check:** From a logged-out browser, does one click run the deployed agent right then, on the judge's own input, with every synthetic, simulated or recorded part labelled on that screen? yes / no

- What the judge's link does (live agent with own input, sample only, login first, replay): ______________________________
- What's synthetic, simulated, recorded or planted, and where each is said (screen, video, write-up): ______________________________
- Up through judging? Cost per day covered? What the page shows if the model call fails (Q8.3): ______________________________

**Status:** ______

---

## 9. The product: main screen, audit trail
**Filled by:** Q5.2 (the main screen), Q9.1 [Q9.2, Q9.3]

**Instruction:** the one screen the person would use for the chore, with the decision and the headline number on it, and a trail they can see and overturn.

**The winners' bar:** 10 of 10 had a real product UI; all 3 Professional winners showed an audit trail as a feature. OneDecision: one decision card and a hash-chained log. Shopbell shows its own miss and the owner's overturn feeding the next decision (video, 1:55). 5 of 10 built privacy or consent in and showed it (video).

**Gap check:** Could the person do the chore from this one screen, see why each decision was made, and overturn one? yes / no

- The one main screen, in one line, with the headline number on it: ______________________________
- How each decision's record can't be silently edited, and where the person sees it: ______________________________
- Where the quiet items live, and how a wrong skip is found: ______________________________
- Legible at video size, checked by a 10-second test recording (Q9.2) [uncertain: winners' frames not analysed]: ______________________________
- Privacy or consent promise, kept in code (Q9.3): ______________________________

**Status:** ______

---

## 10. The video
**Filled by:** Q10.1 to Q10.7; at script time [Q10.8, Q10.9, Q10.10]; plus Q8.2 (labels) and Q12.1 (script check)

**Instruction:** open on the person and the stakes, give one real number early, show a real run called out as real, show the refusal and the human decision on purpose, name the sponsor tech with its job, and claim nothing the run didn't do.

**The winners' bar:** see the skeleton below. What separated the 10 winners from FlakeProof's video: they opened on a person or the stakes (FlakeProof opened on the builder), gave a real-world number in the first minute (FlakeProof's first came at 2:15), called out a live or real run (FlakeProof showed recorded runs), named the sponsor tech aloud (FlakeProof never did), and ran scripted with no filler (FlakeProof ran 4:58, unscripted). Refusals, the human decision and a closing line were in FlakeProof too.

**Gap check:** Does the plan hit every beat of the skeleton, with a real run and a sourced number in the first minute, and is the script checked against the recorded run before recording? yes / no

### Video script skeleton

Built from the captions of all 10 winners' videos. The times are targets, not rules. The captions show what is said and when; frames weren't analysed. The order varies: OneDecision shows the decision (1:05) before its refusal (2:15), while Instanter refuses at 2:00 and decides at 2:10.

| Target | Beat | What the winners did (video) |
|---|---|---|
| 0:00 to 0:20 | The person and the stakes: a scene, not the builder. | 10 of 10. Tally: nine children before 8am, one adult. Instanter: 7 days to answer, or lose by default. |
| by 0:40 | One real-world number, with its source. | 9 of 10 in the first minute, most by 0:30. Shoulder: three in four families. On The Line: 19 million referrals a year. |
| early, once | One honest line, if anything is synthetic or simulated. | OneDecision 0:25: everything runs on synthetic data. |
| about 0:30 to 1:30 | The product running, called out as real; the run nobody watched. | 10 of 10 call out a live or real run. Shopbell 0:45: a live recall caught at 6am on a Sunday. OneDecision 0:30: a check-in starts the work. Instanter 1:05. |
| about 1:00 to 2:30 | The human decision, framed in one line. | 10 of 10 show it. OneDecision 1:05; Instanter 2:10, "The interrupt is the product"; Tally 2:30. |
| about 1:45 to 2:30 | The refusal or its own miss, with the rule said first. | 10 of 10. Shopbell 1:55, Instanter 2:00, Tally 2:10, OneDecision 2:15. |
| last third | The sponsor tech in one sentence, tied to what it does, and what the model never decides. | 10 of 10 name it, mostly here; Instanter (0:45) and OneDecision (0:00) also early. 8 of 10 say code decides. |
| after the run | The measured result. | 8 of 10, from 0:10 (Instanter) to 4:00 (Tally). Doorstep 3:40: 20 of 20 forbidden actions refused. |
| near the end | The try-it line. | 5 of 10 invite the viewer. Instanter 2:45: no account, no key. Doorstep: "Try it today." |
| last line | The closing line, restating the promise. | 10 of 10. OneDecision: teach the agent once, it handles the next hundred. |
| total | 3:00 to 4:30 on a 5:00 cap, scripted, no filler. | 7 of 10 in that band; range 2:39 to 4:55; median about 4:00. Other caps: about 60 to 90% of the max [inference]. |

- 0:00 to 0:20, the opening (Q10.1): ______________________________
- The number by 0:40, and its source (Q10.2): ______________________________
- The honest line, if needed (Q10.10): ______________________________
- The real run, how it's called out, and the unattended moment (Q10.3): ______________________________
- The human decision and its one line (Q10.5): ______________________________
- The refusal or own miss, and the line before it (Q10.4): ______________________________
- The sponsor sentence: "[product] does [job]; the model never [call]" (Q10.6): ______________________________
- The measured result, and where it lands (Q10.8): ______________________________
- The try-it line (if the link takes the judge's own input): ______________________________
- The closing line (Q10.9): ______________________________
- Target length, under a cap of ______; scripted word for word (Q10.7): ______
- Script read against the recorded run; labels added; claims cut (Q12.1): ______________________________

**Status:** ______

---

## 11. The write-up
**Filled by:** Q11.1, Q11.2; plus Q8.2 (labels) and Q10.2 (the context number)

**Instruction:** logline first, the measured number next, the context number with its source under its own heading, one heading per criterion, the sponsor tech with files and runs, and every label next to its claim.

**The winners' bar:** 3 of 3 Professional winners lead with a filter ratio or a zero-error number: Shopbell's 761 read, 30 surfaced, 4 decisions; Instanter's "4 of 46" deadlines that hand-counting gets wrong; OneDecision's 24 cases with 0 wrong.

**Gap check:** Does the first screen of the write-up show the logline, the measured number and the sponsor products by name, with every claim traceable to a run? yes / no

- First lines: the logline, then the measured number: ______________________________
- "Measured" (how the set was made; not used for tuning) kept apart from "Context" (the sourced real-world number): ______________________________
- Headings, one per criterion, using the sheet's exact names [uncertain: CHECKLIST advice]: ______________________________
- "Where the sponsor tech is used": product, file, run: ______________________________
- Which part is the model and which is code, with file names: ______________________________
- Synthetic, simulated or planted parts labelled next to each claim: ______________________________
- The one honest limitation: ______________________________

**Status:** ______

---

## 12. Checks, bonus and submission hygiene
**Filled by:** Q0.2, Q12.1 [Q12.2]

**Instruction:** check each piece before its step, claim every bonus after the core works, and make everything public and checked well inside the deadline.

**The winners' bar:** no AfH winner's submit time is recorded. His StreetProof went in 3 hours early and won (secondary). The top three AfH winners had no bonus posts at all, so posts sit at the margin. The contrast: FlakeProof's video went public 1h51m after the deadline and claimed a PR the run never opened.

**Gap check:** Is every link public and checked logged out at least 12 hours before the deadline, with the script and the write-up checked against the run? yes / no

- Bonus items: when (after the core works), exact conditions, public before the deadline, found logged out (Q12.2): ______________________________
- When each check happens (script before recording, write-up before submitting, links a day early): ______________________________

**Pre-submit checks** (tick on the day)
- [ ] Script read against the recorded run; every claim traces to it.
- [ ] Write-up numbers checked against the README and the run, line by line.
- [ ] Video public, not unlisted.
- [ ] Repo public, with a licence the rules accept; prior code declared (Q0.4).
- [ ] Every link (video, repo, live link, posts) opened from a logged-out browser.
- [ ] No commits after the deadline.

**Status:** ______

---

## 13. Scope and time
**Filled by:** Q13.1 (and its "Clock times" follow-up), Q0.2 [Q13.2, Q13.3]

**Instruction:** one chore, one thin end-to-end path by halfway, a written cut list, and fixed clock times with silent visual timers.

**The winners' bar:** OneDecision: one chore (repeated exceptions), deployed and triggered, with 199 tests. Shopbell: one agent, no Graph or Swarm, Professional Gold.

**Gap check:** Is there a clock time for the halfway check and the feature freeze, leaving about 15% for the screen and the video, and a written cut list? yes / no

- What runs end to end by halfway (trigger, agent, rule, approval, record): ______________________________
- Halfway clock time ______ Feature freeze ______ (each with a silent visual timer)
- The second chore hiding in the idea, cut (Q13.2): ______________________________
- Cut list, in order (never the deployment, the trigger, the held-out set or the video time) (Q13.3): ______________________________

**Status:** ______

---

## 14. Team split
**Filled by:** Q14.1 (or "cut, because solo")

**Instruction:** each person owns a whole piece end to end; the architecture stays his.

**The winners' bar:** 6 of 10 AfH winners were solo, including Shopbell and Instanter; Tally (Grand Prize) was a pair; no winning team was larger than 3. [uncertain] No winner's split is recorded.

**Gap check:** Does each person own a whole piece end to end, with neither building a second feature? yes / no

- Teammate owns: ______________________________ [inference: what judges see most]
- He owns: ______________________________
- Who checks the end-to-end path at halfway: ______________________________

**Status:** ______

---

## Known risks and deliberate cuts

**Risks** (a section that still failed after 2 re-asks, or a line he chose to keep at W1). Stage One lines never go here.

| Section | Question ID | His answer | The winners' bar (count) | Criterion it costs | Rounds used | His reason |
|---|---|---|---|---|---|---|
| | | | | | | |

**Cuts** (from R2)

| Section | Cut, because ... |
|---|---|
| | |

**Paused** (if P1 or W1 stopped the interview): where it stopped, and what's still open: ______________________________

---

## Win-readiness check (W1)

Run before writing the spec. Mark each line pass, fail or risk. Every fail goes back to its stage for one more R1 round; Stage One lines must pass.

### Judge's 60-second test

**First minute of the video** (targets from the 10 winners' captions; on-screen lines stay [uncertain], because frames weren't analysed)
- [ ] By about 0:20: a person, a scene or the stakes; not the builder or a title card (10 of 10).
- [ ] By about 0:40: one real-world number, with a source (9 of 10 in the first minute).
- [ ] The product starts doing the chore, called out as real (Shopbell 0:45; OneDecision 0:30; Instanter's real run at 1:05) [uncertain: from narration, not frames].
- [ ] If anything shown is synthetic or simulated, it's said once, in one line (OneDecision 0:25).
- [ ] Nothing is said that the recorded run didn't do.

**The rest of the video** (the winners put these after the first minute; agent 1's and the checklist's advice to fit the refusal and the sponsor names into the first minute isn't what the winners did)
- [ ] A refusal or its own miss, shown on purpose, with the reason (10 of 10; the top four at 1:55 to 2:15).
- [ ] The human decision, framed in one line (10 of 10 show it).
- [ ] Each sponsor product named aloud with its job, and what the model never decides (10 of 10 name it; 8 of 10 say code decides).
- [ ] The measured result (8 of 10).
- [ ] A try-it line, if the link takes the judge's own input (5 of 10).
- [ ] A closing line that restates the promise (10 of 10).
- [ ] Length 3:00 to 4:30 on a 5:00 cap, or 60 to 90% of another cap [inference]; scripted, no filler (7 of 10 in that band; no filler in any winner's captions).

**First screen of the write-up**
- [ ] The logline, in the person's words.
- [ ] The measured number, and what it was measured on.
- [ ] The sponsor products, by name, in the first paragraph.
- [ ] The live link, no login, with what's live and what's recorded stated.
- [ ] "Synthetic" or "simulated" wherever it applies.
- [ ] Headings that match the judging criteria [uncertain: CHECKLIST advice].

### Scorecard

Which criteria each section moves, from agent 1's "criterion moved" lines and the video counts. ● = moves it directly; ○ = supports it.

This event's weights (from the criteria map): Technical ____ Design ____ Impact ____ Creativity ____ Presentation ____ Tie-break ____

| Section | Patterns and counts | Technical | Design | Impact | Creativity | Presentation | Stage One / bonus |
|---|---|---|---|---|---|---|---|
| 0 Rules and field | P20, P23, P5 | ○ | | | | | ● |
| Gate A three lines | rule 13 | ○ | ○ | | | | |
| 1 Person and chore | P1 (10 of 10), P2, P23, P24 | | | ● | ● | | ○ |
| 2 Pain in numbers | P7, P4 | | ○ | ● | | ○ | |
| 3 Idea, not a dev tool | P2, P24, P26, P11, P3, P4 | ○ | | ● | ● | | ● |
| 4 Sponsor tech | P5 (9 of 10), P14, P19, P20 | ● | | | ○ | | ● |
| 5 Runs on its own | P3 (3 of 3 Professional), P4 | ● | ● | ○ | | | ● |
| 6 Core and refusal | P11 (8 of 10 aloud), P12 (10 of 10), P4 | ● | ○ | ● | ● | ○ | |
| 7 Proof and breadth | P7, P8 (8 of 10 synthetic), P9, P10, P16 | ● | ○ | ● | | ○ | |
| 8 Live deployment | P6 (9 of 10), P5, P16, P18 | ● | ○ | ○ | | | |
| 9 Product and trail | P15 (10 of 10), P4, P13, privacy (5 of 10) | ○ | ● | ○ | | ○ | |
| 10 Video | P17, P18, P19, P12, P1, P7 (video counts) | ○ | | ○ | ○ | ● | ○ |
| 11 Write-up | P1, P5, P7, P11, P18, P19 | ○ | ○ | ○ | ○ | ○ | ○ |
| 12 Checks and bonus | P18, P20, P21 | | | ○ | | ○ | ● (bonus at the margin) |
| 13 Scope and time | P25, P15 | ○ | ○ | | | ○ | ○ |
| 14 Team split | P22 | | | | | | |

**Verdict per criterion** (each needs its ● sections at or above the winners' bar, or a logged risk)

| Criterion | This event's weight | ● sections | Weakest answer (section, question ID) | Verdict |
|---|---|---|---|---|
| Technical | | 4, 5, 6, 7, 8 | | pass / fail / risk |
| Design | | 5, 9 | | pass / fail / risk |
| Impact | | 1, 2, 3, 6, 7 | | pass / fail / risk |
| Creativity | | 1, 3, 6 | | pass / fail / risk |
| Presentation | | 10 | | pass / fail / risk |
| Stage One | pass or fail | 0, 3, 4, 5, 12 | | pass / fail (no risk allowed) |

Notes:
- Row 10: for a judge who scores from the video alone, the video carries every criterion (AfH allowed this).
- Row 11: AfH's Presentation criterion is about the video; the write-up carries each criterion's case for a text-only judge, so it supports all five.
- Row 14 moves no criterion directly (P22); it decides who builds rows 7, 9 and 10.
- Fix order at W1: the heaviest-weighted criterion whose ● sections have the weakest answers goes first.

**The interview ends when:** every section's Status is filled (pass, cut, or risk after its rounds), every Stage One line passes, every line above is pass or a logged risk, and W1 has run.
