# Winning questions: the /interview bank for hackathon kickoffs

Agent 3 of 3, final version. Written Wed Oct 14, 2026, evening. Built from agent 1's pattern file (P1 to P26, led by the AWS Agents for Humans loss review in the vault, `Memory/reference_flakeproof_loss_review_2026-10-14.md`) and the Oct 14 caption read of all 10 AfH winners' videos plus FlakeProof's. The spec structure is `IDEA-TEMPLATE.md` in this folder.

## How to use

**Before the first question**
1. Read this file and `IDEA-TEMPLATE.md` in full.
2. Stage 0: fill the Rules sheet (below) from the live rules page, today. Spawn a read-only Agent to read it, or ask Sid to paste it. Never fill a field from memory or an old note. Map the event's criteria onto the five (below). Show him the sheet and the map, then ask Q0.1.
3. Time box: about an hour for day 0 (CHECKLIST). Suggest a silent visual timer. After 9pm, ask P1 (Pause) before Gate A, and again before Stage 4.

**Asking**
4. Every question goes through AskUserQuestion with 2 to 4 options. That includes follow-ups, re-asks, Gate A and guided hunts. No chat-only or free-text questions. Where he must type specifics (a logline, a person, a number, a time), the options show the shapes, and he types his version in the automatic Other. **Plain words:** the question and the option labels say what's asked in everyday words and in his project's own terms, with no kit jargon (contest type, CORE, P-numbers, question IDs, [ARCH], Gate A, scaffold, Stage One). A winner's name appears only in an option's description, as evidence for that option (with its count where the bank gives one), never as an example of what his product is or of a line he should say; Gate A names no other project at all. If he says he's confused, re-explain once with an everyday analogy (a pizza app, a library app), not another winner, then ask again. (Oct 29 and 30, the first real run: "im confused what this is about?" at the contest type, and "This product is not tally" at Gate A.)
5. Ask the CORE path in stage order (table below), 1 to 4 questions per call, from the same or the next stage. A follow-up runs right after its question when its "ask when" holds. Ask an OPTIONAL question only when its "ask when" holds. Apply the skip rules.
6. Options are suggestions; he decides. Stages 1 and 3 never propose an idea. By his own record, his ideas placed or reached a final; Rack and Not From Here came from the AI and didn't place (TrueCopy, also the AI's, has no recorded result).
7. **Recommended first.** The first option carries "(Recommended)" whenever the patterns or the winners support one, and its description gives the count. When the evidence is thin, the description starts with "Weak evidence:". When there is no recommendation, the "why" line says why (a fact about the rules or his chore, or Gate A, which is his design). On [ARCH] questions the recommendation is a choice or a constraint the winners used, offered as a suggestion he can take or reject, never a design. Some recommendations depend on an earlier answer ("if Q2.1 said ..."): put the matching option first and mark that one.
8. **Gate A, first.** Right after Stage 0, before Stage 1 and before any [ARCH] question, ask GA.1 to GA.3: his three lines (the main flow step by step, what one record holds, one trade-off he'd defend with its cost). Before each, say the line's "say first" note: the line in plain words with an everyday analogy. He types his own line in Other; the options only offer help (explain again, point me to my notes, skip for now), with no Recommended mark and no other project named. Ask them in one call or one per call; a skipped line comes back before Stage 1. Stages 1 to 3 then check his lines (R1, Gate A form, on a line an answer contradicts), and the [ARCH] questions in Stages 4 to 9 are the critique. If he's stuck, run a guided hunt as an AskUserQuestion: the question names the exact source and section, the one thing to find, and a 5 to 10 minute silent timer; the options are 2 to 4 possible answers, and he picks one or types his own. Good hunt sources: his own earlier notes on the idea, the paper or docs page for the model he's using, or the event's build-session recording. If the timer runs out, give one small worked example of the stuck part only; he explains it back, and the design goes back to him. Never write the architecture, even when asked.

**The keep-asking loop** (until the plan gives him the best chance of winning)
9. After each stage, check his answers against that section's gap check in `IDEA-TEMPLATE.md` and the red flags here. Re-ask when any of these is true:
   - an answer matches a red flag;
   - the gap check would be "no";
   - an answer is below the winners' bar in the question's "winners" or "why" line (fewer than 20 cases, a statistic with no source, a group instead of a person);
   - two answers contradict (Q1.3 says one-off, Q5.1 says a clock);
   - his Other text is vague (a group word, no number, no place).
10. A re-ask is R1: one line on the gap, the winners' bar with its count, and the recommended alternative. He takes it, types his own revision in Other, keeps his answer as a risk, or cuts the section (R2 asks why).
11. At most 2 re-asks per section. If it still fails, write it into the spec's Known risks (question ID, his answer, the winners' bar, the criterion it costs, rounds used, his reason) and move on. Stage One lines are the exception. They can't be kept as a risk, because a fail there scores nothing: fix it, or ask him (R1, Stage One form) whether to rethink the idea.
12. Before writing the spec, run W1, the win-readiness check: the Judge's 60-second test, the Scorecard (Technical, Design, Impact, Creativity, Presentation) and every Stage One line. Each failing line, including each logged risk, sends the interview back to its stage for one more R1 round.
13. The interview ends only when every section passes, is "cut, because ...", or is a logged risk after its rounds, and W1 has run.

**Writing**
14. Labels carry over: anything marked [uncertain] or [inference] goes into the spec with the same label.
15. Write the spec in `IDEA-TEMPLATE.md`'s structure and order, in his words, to `<cwd>/.plans/<event-slug>-spec.md`, and state the absolute path.

**Evidence tags.** P1 to P26: agent 1's patterns (index below). (video): the caption read of all 10 AfH winners' videos and FlakeProof's on Oct 14; it covers what is said and when, not what is on screen. (secondary): his other notes, used only where they agree with or add to the AfH evidence. A count like "10 of 10" means the 10 AfH winners unless it says otherwise. "The top four" means Tally (Grand Prize), Shopbell, Instanter and OneDecision (Professional Gold, Silver, Bronze).

**Format rules for editing this file.** Question text and option labels in plain words, with no kit jargon and no other project's name (How to use, item 4). Header at most 12 characters. 2 to 4 options. Labels 1 to 5 words, counting "(Recommended)" as one word. "(Recommended)" only at the end of the first option's label. Never an option called "Other" (the tool adds it). Every question ends with a question mark. No em dashes or en dashes.

## Skip rules

| When | Skip or change |
|---|---|
| Solo, already known | Skip Q14.1. Section 14: "cut, because solo". |
| No bonus on the sheet | Skip Q12.2. Section 12's bonus line: "cut, because none offered". |
| Model or API contest (Q0.1's "Build on their models"; no runtime to deploy on) | Q4.1 asks which newest model capability a judge will see used, and where. Q5.1 asks what makes the result already waiting when the person opens it. Q8.1 asks whether the link calls the model live. P3 and P5 weigh less [inference]. |
| One required product | Skip Q4.2. |
| Developer-tools track, and he's entering it | Skip Q3.5. |
| Judges must test it (sheet) | Q8.1 carries more weight. Keep every video question anyway. |

## The CORE path

39 CORE questions plus 2 follow-ups that are always asked (Q5.2 "On a no", Q13.1 "Clock times"). Loop questions (R1, R2, P1, W1) come on top as needed.

| Call | Questions | Then |
|---|---|---|
| 1 | Q0.1, Q0.2, Q0.3 | Loop on section 0 (Q0.4 if its "ask when" holds). After 9pm: P1 |
| 2 | GA.1, GA.2, GA.3 (one call, or one per call) | Gate A: his three lines, before anything else is designed. A skipped line comes back before Stage 1. Stages 1 to 3 check them; the [ARCH] questions below critique them |
| 3 | Q1.1, Q1.2, Q1.3, Q1.4 | Loop on section 1 (Q1.5) |
| 4 | Q2.1, Q2.2 | Loop on section 2 (Q2.3) |
| 5 | Q3.1, Q3.2, Q3.3, Q3.4 | Loop on section 3 (Q3.5, Q3.6). R1 (Gate A form) on a line an answer contradicts. After 9pm: P1 |
| 6 | Q4.1, Q4.2, Q5.1, Q5.2 | Loop on sections 4 and 5 |
| 7 | Q6.1, Q6.2, Q7.1, Q7.2 | Loop on sections 6 and 7 (Q7.3) |
| 8 | Q8.1, Q8.2, Q9.1 | Loop on sections 8 and 9 (Q8.3, Q9.2, Q9.3) |
| 9 | Q10.1, Q10.2, Q10.3, Q10.4 | |
| 10 | Q10.5, Q10.6, Q10.7, Q11.1 | Loop on section 10 (Q10.8 to Q10.10 wait for a script) |
| 11 | Q11.2, Q12.1, Q13.1, Q14.1 | Loop on sections 11 to 14 (Q12.2, Q13.2, Q13.3) |
| 12 | W1 | Back to any failing stage; then write the spec |

## The Rules sheet (Stage 0)

The Stage 0 agent returns each field word for word, with its source URL, or "not on the page".

1. Event name, rules URL, and when it was read.
2. Theme text, word for word.
3. Deadline as written, and in Toronto time. Can the entry be edited after submitting?
4. Video: minimum and maximum length, platform, public or unlisted, and what it must show.
5. Required tech, each product by name.
6. Stage One checks (theme fit, required tools, public links, built during the event) and the judging method (experts, peers, AI-driven analysis). May judges score from the video and text alone?
7. Criteria and weights as written, and the tie-break. Any criterion line that names a product, a deployment or a live demo, word for word.
8. Registered count, prizes, tracks. Is there a developer-tools track?
9. Bonus items and their exact conditions (title words, deadline, where to post).
10. Pre-existing-work rule; repo and licence rules.
11. Organizer posts, build sessions and judge talks (titles, links); the example users or tasks they name; what the build session deploys to.
12. The sponsor's product family, and any feature launched in the months before the event, with its date.

**Criteria map.** Map each criterion onto AfH's five: Technical Implementation, Design, Potential Impact, Creativity, Presentation (AfH: equal weights, scored 1 to 5, ties to Technical). Spend the interview's time by weight. Example with Open Agent's weights as the Oct 12 note recorded them (secondary; re-check on the live page): Impact 30 to Potential Impact; Technical 20 to Technical; Innovation 15 to Creativity; Demo 15 to Presentation; Product-UX 10 to Design; Sponsor 10 to Technical and Stage One.

## Pattern index (agent 1)

P1 one named person, one recurring chore · P2 non-developer audience · P3 runs on its own, unattended run shown · P4 surfaces only for a decision · P5 sponsor's newest infrastructure, used live · P6 public live URL, real agent, no login · P7 lead with a measured number · P8 breadth: dozens of cases, synthetic is fine · P9 unseen and adversarial evals · P10 one real catch on a live feed · P11 code decides, the model proposes · P12 refuses on screen, with the reason · P13 an audit trail the user sees · P14 depth is real tools, not agent counts · P15 a real product UI · P16 general engine, the user's own input · P17 the video shows it working · P18 claim nothing the demo didn't do · P19 sponsor names said aloud · P20 Stage One hygiene and timing · P21 bonus only at the margin · P22 solo or pair is fine · P23 do what the organizers said · P24 originality from person and setting · P25 one chore, done completely · P26 it does the chore in the world

---

## Stage 0: Rules and field

### Q0.1 Rules check · CORE · [FACT]
- **say first:** Under the summary, one plain line on what kind of contest it is (for example: "you build your own app on their models; there's no cloud of theirs you have to deploy on"), and why it matters: a few later questions change with it.
- **question:** The rules summary above was read from the live pages today. Does anything in it, or the line on what kind of contest it is, clash with what you saw in the kickoff, the posts or the sessions?
- **header:** `Rules check`
- **options:**
  1. **Nothing clashes**: The summary and the contest line stand.
  2. **The contest line is wrong**: Pick the right kind next.
  3. **Something else clashes**: Say what in Other. It gets fixed before Q0.2.
  4. **Page missing or blocked**: Paste it. Some pages block automatic readers.
- **multiSelect:** false
- **why:** P20, P23, agent 1 section 5. Stage One is pass or fail, every later stage leans on the sheet, and the type sets the skip rules. AfH was a cloud/agent contest: 9 of 10 winners used AgentCore live. No Recommended: this is a fact about the page.
- **winners:** AfH's sheet held the line that decided Technical, word for word: "A live demo and/or Amazon Bedrock AgentCore deployment will strengthen this score."
- **red flag:** Any field filled from memory or an old note.
- **follow-up** (ask when he picks option 2): `Contest` · What kind of contest is it?
  1. **Deploy on their cloud**: Your agent runs on the sponsor's own cloud service. Every pattern at full strength (like AWS's Agents for Humans).
  2. **Build on their models**: Your own app calls their models. The newest model ability, used where a judge sees it; the run-on-its-own patterns weigh less [inference].
  3. **Small student event**: Judges look longer at each entry. Story and polish weigh more (secondary).
  4. **Small sponsor showcase**: The sponsor's agent at the core, a trust story, real network calls (secondary).
- **fills:** section 0

### Q0.2 Submit by · CORE · [FACT]
- **question:** The deadline is [as written], which is [Toronto time]. When will the complete entry be in, with every link public?
- **header:** `Submit by`
- **options:**
  1. **24 hours early (Recommended)**: Entry in, video public (not unlisted), repo public with a licence. Only fixes after, if edits are allowed.
  2. **12 hours early**: The checklist's minimum margin.
  3. **Final evening**: No margin for a failed upload or a privacy setting.
  4. **Public after submitting**: Red flag: FlakeProof's video went public 1h51m after the deadline.
- **multiSelect:** false
- **why:** P20. FlakeProof's video went public 1h51m after the AfH deadline, was Unlisted while the rules said public, and had two commits after the deadline. CHECKLIST: submit 12 to 24 hours early. No AfH winner's submit time is recorded.
- **winners:** His StreetProof went in 3 hours early and won Livepeer Track 2 (secondary).
- **red flag:** A deadline converted from memory. "I'll submit when it's done."
- **note:** The target goes in his calendar now, with a silent visual timer the evening before. If entries can't be edited after submitting, the early version is the final one.
- **fills:** sections 0, 12, 13

### Q0.3 Organizers · CORE
- **question:** Of the organizer posts, build sessions and judge talks in the rules summary, which have you read or watched?
- **header:** `Organizers`
- **options:**
  1. **All read and watched (Recommended)**: Their example users feed Q1.2, and the build session shows the expected deploy target.
  2. **Posts, not sessions**: Watch one build session before we check your design.
  3. **None yet**: Finish before the questions about the person. It's where judges said what they want.
  4. **None published**: Use the theme text and the criteria only.
- **multiSelect:** false
- **why:** P23. The AfH Strands launch post asked for one real task that needs judgment, and said if you wouldn't use it yourself it doesn't count. Its Professional examples (scheduling, follow-ups, reporting, a contractor's compliance paperwork, a shop owner's repeated booking questions) read like the winners [inference]. The Sep 9 build session deployed to AgentCore, which is where the winners ran.
- **winners:** Shopbell is a shop owner's daily reading. Instanter is deadlines and follow-ups. OneDecision is an ops supervisor's repeated exceptions.
- **red flag:** "Skipped the posts, the rules are enough."
- **note:** A feature a judge talked about goes on the list for Q4.1. An AfH judge stressed evals and guardrails on Oct 4 [inference that it mattered].
- **fills:** section 0

### Q0.4 Prior work · OPTIONAL
- **ask when:** the sheet has a pre-existing-work or built-during-the-event rule, and he may reuse code (a past entry, a kit, his own library).
- **question:** The rule says [rule]. Will you reuse any code you wrote before this event?
- **header:** `Prior work`
- **options:**
  1. **Fresh build (Recommended)**: Weak evidence: safe under any rule. His Haggle was rebuilt from scratch for this rule and placed 2nd of 109 (secondary).
  2. **Reuse and declare**: Only if the rule allows it. Name the repo and the parts in the write-up.
  3. **Ask organizers first**: When the rule is unclear. Get the answer in writing.
- **multiSelect:** false
- **why:** P20 (Stage One). CHECKLIST: declare any pre-existing code. No AfH winner data on reuse.
- **red flag:** Quiet reuse of a past entry.
- **fills:** sections 0, 12

---

## Gate A: his three lines

Right after Stage 0, before Stage 1 and before any [ARCH] question. These three questions are his three lines: the main flow step by step, what one record holds, and one trade-off he'd defend with its cost. The design is his (CLAUDE.md rule 13), so there's no Recommended mark and no other project's name in anything he reads here. Before each question, say its "say first" note: the line in plain words with an everyday analogy, in his project's terms. He types his own line in Other; the options only offer help. Rough lines are fine: Stages 1 to 3 check them, and the [ARCH] questions in Stages 4 to 9 are the critique (each of those does carry a Recommended choice from the winners). If he's stuck, run a guided hunt (How to use, item 8). The "winners" and "red flag" notes below are for Claude only: use them to judge his line, never show them to him as examples.

### GA.1 Main flow · CORE · [ARCH, his design]
- **say first:** Line 1 is the main flow, step by step: what the person gives the product, what happens to it, and what they get back. For each step, say which part does it (a model, your own code, a screen). An everyday example: a pizza app is the menu page, then the server adds up the price, then the payment company charges the card, then the kitchen screen shows the order.
- **question:** Line 1: what is [project]'s main flow, step by step: what goes in, what happens at each step and which part does it, and what the person gets back (type it in Other; rough is fine)?
- **header:** `Line 1`
- **options:**
  1. **Explain it again**: Another everyday example, then the same question.
  2. **Point me to my notes**: Claude quotes your own earlier words about the idea, then asks again.
  3. **Skip, come back later**: Do lines 2 and 3 first; this one comes back right after.
- **multiSelect:** false
- **why:** P3, P11, P4. No Recommended: Gate A is his design. After he answers, check his flow for two steps it may leave out, and ask about them in plain words: the record each decision writes (P13, all 3 Professional winners) and where it all runs while judges look (P5, 9 of 10 on AgentCore live).
- **winners (Claude only):** OneDecision, as one list: a check-in starts it, a policy decides, one card goes to Dana, a hash-chained log keeps the record.
- **red flag:** A step that never acts in the demo run (P14: FlakeProof's other specialists never ran).
- **fills:** Gate A

### GA.2 The data · CORE · [ARCH, his design]
- **say first:** Line 2 is what the product saves. Picture one row in its database: which columns does it have? An everyday example: a library app saves a loan as book, borrower, date out, date due.
- **question:** Line 2: when [project] handles one [item], what does it save (list the fields in Other)?
- **header:** `Line 2`
- **options:**
  1. **Explain it again**: Another everyday example, then the same question.
  2. **Point me to my notes**: Claude quotes your own earlier words about the idea, then asks again.
  3. **Skip, come back later**: Do the other lines first; this one comes back right after.
- **multiSelect:** false
- **why:** P13, P11, P4. No Recommended: his design. One test for any record, asked back in plain words after he answers: does each decision sit next to its reason (for example, "a flag with only a score doesn't say why it was flagged: what sits next to it?")?
- **winners (Claude only):** one row per item read, with what was decided and by which rule (Shopbell's items and rejection log); inputs, rule version, result and reason (Instanter's statute trace; Tally's versioned rules); each human answer becoming a tested rule (OneDecision's "Teach"); an append-only event log (On The Line's chain; OneDecision's hash chain).
- **red flag:** No place where a decision and its reason live together.
- **fills:** Gate A

### GA.3 Trade-off · CORE · [ARCH, his design]
- **say first:** Line 3 is one thing the product deliberately won't do, so that something more important works, and what that costs. An everyday example: a pizza app with no accounts, so ordering takes one click, but nobody can see their past orders.
- **question:** Line 3: what will [project] deliberately not do, so something more important works, and what does that cost (type it in Other)?
- **header:** `Line 3`
- **options:**
  1. **Explain it again**: Another everyday example, then the same question.
  2. **Point me to my notes**: Claude quotes your own earlier words about the idea, then asks again.
  3. **Skip, come back later**: Do the other lines first; this one comes back right after.
- **multiSelect:** false
- **why:** No Recommended: the trade-off is the one he'd defend. If his line names no cost, ask once, plainly: "What does it cost, and who pays that cost?" After it's built, he explains it back and defends it (rule 13).
- **winners (Claude only):** refuse rather than guess (Tally won't guess a sugar limit a photo can't establish); one agent, not a swarm (Shopbell, Professional Gold); synthetic but broad (8 of 10 winners); quiet over complete (Shopbell: "Silence is the product."). Each was said aloud in the winner's video.
- **red flag:** A trade-off he can't name the cost of.
- **fills:** Gate A

---

## Stage 1: The person and the chore

### Q1.1 Idea source · CORE
- **question:** Whose idea is this, and where did the person in it come from?
- **header:** `Idea source`
- **options:**
  1. **Mine, someone I know (Recommended)**: Weak evidence [inference]: a person you can ask gives the detail Q1.4 and the opening scene need.
  2. **Mine, organizer example**: Close to a user the organizers named (Q0.3).
  3. **Mine, I'm the user**: Fine if you'd really use it. StepSpotter's builder was its user.
  4. **The AI suggested it**: Push back: Rack and Not From Here were the AI's ideas and didn't place.
- **multiSelect:** false
- **why:** Agent 1, section 4. By his Oct 2 account, every idea that started with him and has a result placed or reached a final (Evidence L428). The first three options are all fine.
- **winners:** AfH organizer guidance: if you wouldn't use it yourself, it doesn't count.
- **red flag:** "The AI suggested it", or a technique looking for a user.
- **follow-up** (ask when he picks option 4): `Make it his` · What would make this idea yours?
  1. **Swap the person (Recommended)**: Keep the chore, change the person to someone he can ask.
  2. **Use an organizer example**: Start from a user the organizers named.
  3. **Start over, mine**: Drop it and begin from a person, not the AI's idea.
- **fills:** section 1

### Q1.2 Person · CORE
- **question:** Who is the one person with the chore (pick the closest shape, then type the role, the place and the chore in Other)?
- **header:** `Person`
- **options:**
  1. **Named person, one chore (Recommended)**: 6 of 10 winners name the person or are the user (Tally's Rosa, OneDecision's Dana).
  2. **Clear role at a place**: The other 4 winners: a store owner, a clinic attorney, a directory curator, a help-desk volunteer.
  3. **Me as the user**: StepSpotter's builder: he never owned a home.
  4. **A group or a developer**: Red flag: FlakeProof's user was "Java developers or maintainers".
- **multiSelect:** false
- **why:** P1, P2. 10 of 10 AfH winners name one person with one chore, and none is a developer. The kinds of place that won: a small or home business (Shopbell, Tally), a front-line desk (Instanter, Plainletter), a family or neighbour (Shoulder, Doorstep), ops staff (OneDecision).
- **winners:** OneDecision at 0:10: Dana runs returns for a camera rental company and makes the same call 20 times a week (video).
- **red flag:** A group ("small businesses", "anyone who..."), two chores at once, or a developer outside a developer-tools track (then ask Q3.5).
- **fills:** section 1

### Q1.3 Rhythm · CORE
- **question:** How often does the chore come round, and what starts it?
- **header:** `Rhythm`
- **options:**
  1. **Daily at a time (Recommended)**: Weak evidence: 3 of the top four run on a clock (Shopbell 6am, Instanter 7am weekdays, Tally closes the day at half past six). Only if it's true of the chore.
  2. **When something arrives**: OneDecision: a check-in at the return dock starts the work (video, 0:30).
  3. **Weekly or monthly**: Still recurring. The demo needs one run you can show.
  4. **One-off task**: Red flag on a background theme: nothing to trigger.
- **multiSelect:** false
- **why:** P3. All 3 Professional winners run without a person starting them, and show it. FlakeProof was started by hand. This is a fact about the chore: never bend it to fit option 1.
- **winners:** Instanter runs itself at 7am every weekday whether or not anyone opens the page (video, 2:35).
- **red flag:** "Whenever the user feels like it."
- **fills:** sections 1, 5

### Q1.4 Insight · CORE
- **question:** How do you know what this chore is really like for them?
- **header:** `Insight`
- **options:**
  1. **Asked someone who does (Recommended)**: Weak evidence: one real conversation gives the detail an opening needs (Dana's same call, 20 times a week, 30 seconds each).
  2. **I do it myself**: You're the user. The organizers' "would you use it" test passes.
  3. **Read their own words**: Forums, guides or official pages the person uses.
  4. **Guessing so far**: Red flag: get one of the three above before Stage 2.
- **multiSelect:** false
- **why:** P1, P23. Creativity asks for real understanding of the problem space. Real users aren't required: only Cahoots had them (25), and 0 of 8 contrast entries did.
- **winners:** Instanter shows it knows the chore with a statute trace on each deadline.
- **red flag:** No source at all.
- **note:** Ask him to add, in Other, one detail an outsider would get wrong. It's the raw material for Q10.1.
- **fills:** sections 1, 10

### Q1.5 Setting · OPTIONAL
- **ask when:** Q1.2's answer names a sector, a group or no place.
- **question:** How exact is the setting: one county, one kind of business, one kind of letter?
- **header:** `Setting`
- **options:**
  1. **One exact setting (Recommended)**: Instanter's Fulton County clinic; Shopbell's San José grocery.
  2. **One sector, any place**: Wider. A weaker problem-space story.
  3. **General audience**: Red flag: reads as generic and crowded.
- **multiSelect:** false
- **why:** P24, P1. The winning chores are ordinary; their originality came from the exact person and setting [inference]. Not From Here's photo-in, name-out read as a crowded category (secondary).
- **red flag:** "Anyone with a phone."
- **fills:** section 1

---

## Stage 2: The pain in numbers

### Q2.1 Volume · CORE
- **question:** Each time the chore comes round, how many items does the person go through, and how many truly need them?
- **header:** `Volume`
- **options:**
  1. **Hundreds, few need them (Recommended)**: Weak evidence: room for a filter ratio, the plainest fit for "surfaces only for a decision" (Shopbell: 761 read, 30 surfaced, 4 decisions).
  2. **Dozens, about half**: OneDecision: 24 cases, 11 automated, 13 escalated, 0 wrong.
  3. **A handful, all need them**: No ratio. Lead with errors versus by hand instead.
  4. **Don't know yet**: Find out before we check your design.
- **multiSelect:** false
- **why:** P7, P4. 3 of 3 Professional winners lead with a filter ratio or a zero-error number; only 1 of 8 contrast entries had a measured outcome. Both shapes won. Shopbell's video says 773 items read in its first two weeks; agent 1 recorded 761 from its page [uncertain which].
- **red flag:** "One at a time", or "all of them, just faster".
- **note:** He types the two counts in Other if he knows them. This answer sets the default first option of Q5.2 and Q7.2.
- **fills:** section 2

### Q2.2 Cost today · CORE
- **question:** What does the chore cost the person today, in units they'd say out loud?
- **header:** `Cost today`
- **options:**
  1. **Missed or wrong items (Recommended)**: Weak evidence: errors can be counted on a set, so the demo can show them going down (Instanter: 4 of 46 deadlines hand-counting gets wrong).
  2. **Time each day**: Minutes or hours of reading, sorting, copying.
  3. **Money or fines**: A claim denied, a reimbursement lost, a penalty.
  4. **Safety or health**: An allergy, a heat warning, an unsafe product.
- **multiSelect:** true
- **why:** P1, P7. Impact is judged on what's demonstrated, so the cost must be one the demo can show going down. The winners put this cost in the first 20 seconds: a Georgia tenant has 7 days to answer or loses by default (Instanter, video 0:00).
- **red flag:** A market-size number standing in for the person's cost. Real-world numbers belong in Q10.2 and the write-up's Context.
- **fills:** section 2

### Q2.3 Errors today · OPTIONAL
- **ask when:** Q2.2 includes missed or wrong items.
- **question:** How does the person get it wrong today, and could you count those errors on a set of cases?
- **header:** `Errors now`
- **options:**
  1. **Countable on a set (Recommended)**: Instanter put "4 of 46 eviction deadlines hand-counting gets wrong" in its video title.
  2. **Known, not countable**: Cite it as context, not as your result.
  3. **Rarely wrong, just slow**: Lead with time saved or a filter ratio.
- **multiSelect:** false
- **why:** P7. A before-and-after error count is a demonstrated number; Instanter said it at 0:10 (video).
- **red flag:** "They make lots of mistakes", with no way to count them.
- **fills:** sections 2, 7

---

## Stage 3: The idea, and why it isn't an established developer tool

### Q3.1 Logline · CORE
- **question:** Which outcome shape fits your one-line pitch (pick it, then type "For [person], X does [job] by [approach], so [outcome]" in Other)?
- **header:** `Logline`
- **options:**
  1. **A number they'd notice (Recommended)**: The outcome as a count, in the person's words (for example, "4 things to look at, not 761"). 3 of 3 Professional winners lead with a number.
  2. **Nothing missed**: The promise is that nothing slips through (for example, "no deadline is missed").
  3. **Done before it hurts**: The job is finished before it causes trouble (for example, "done before the food is cold").
  4. **Taught once, then handled**: The person explains it once and the product handles the rest (for example, "teach it once, it handles the next hundred").
- **multiSelect:** false
- **why:** P1, P2, P7. CHECKLIST's one-sentence form. Judges may score from the write-up and video alone. 10 of 10 winners close on a line that restates the promise (video).
- **red flag:** An approach with no person ("a multi-agent RAG system that..."), engineering words ("so the pipeline is verified"), or "for developers".
- **fills:** section 3

### Q3.2 Prior art · CORE
- **question:** Search the obvious version now (this sponsor's past galleries, Devpost, GitHub, research). What's the closest thing you found?
- **header:** `Prior art`
- **options:**
  1. **None found by search**: Write "none found by search", never "nobody does it".
  2. **A similar entry or winner**: Change the person or the setting, not the technique.
  3. **An established field**: Originality is capped (FlakeProof: iFixFlakies 2019; FlakeWarden won UiPath AgentHack 2026).
  4. **Not searched yet**: Red flag: ten minutes of search before Stage 3 ends.
- **multiSelect:** false
- **why:** P24. FlakeProof's Creativity was 3.5 because automated flaky-test repair was an established field. A Devpost-interviewed judge flagged rehashed ideas already on the market (secondary). His prevention rule: a search that finds nothing isn't proof nobody did it. No Recommended: this is a search result, not a choice.
- **red flag:** "It's new because it uses agents."
- **follow-up** (ask when he picks option 2 or 3): `Difference` · What will a judge notice, in one line, that's different?
  1. **A different person (Recommended)**: Weak evidence [inference]: the winners' originality came from the person and setting.
  2. **A different setting**: One county, one kind of business, one kind of letter.
  3. **A different call decided**: The one call from Q3.3 that nobody else makes in code.
- **fills:** section 3

### Q3.3 Critical call · CORE
- **question:** Where does "the AI proposes, rules decide, it refuses with evidence" matter here: which one call can the person not afford to get wrong?
- **header:** `Critical`
- **options:**
  1. **A date or deadline**: Instanter: the model never computes a date (video, 0:45).
  2. **Money or eligibility**: Tally: versioned rules decide reimbursement.
  3. **Safety or health**: Doorstep: a deterministic check catches the red flag in a call (video).
  4. **A policy exception**: OneDecision: repeated exceptions turned into tested policy.
- **multiSelect:** false
- **why:** P11. 8 of 10 winners say aloud that code, not the model, decides (video); agent 1 counted at least 4 of 10 from the project pages. FlakeProof had this and lost, so it's needed, not enough. No Recommended: the call comes from his chore, and every kind won.
- **winners:** On The Line: the model only proposes; a policy in plain Python decides (video).
- **red flag:** "Nothing critical, it's a convenience." Then the refusal has nothing to protect.
- **fills:** sections 3, 6

### Q3.4 Theme fit · CORE · [FACT]
- **question:** Reading the theme text again, which of its phrases does your idea match by what the demo actually does?
- **header:** `Theme fit`
- **options:**
  1. **[Theme phrase 1]**: Fill from the page. AfH asked for routine tasks handled in the background.
  2. **[Theme phrase 2]**: AfH asked that it surface only when there's a real decision.
  3. **[Theme phrase 3]**: The named audience or track.
- **multiSelect:** true
- **why:** P3, P4, P20 (Stage One theme fit, pass or fail). All 3 AfH Professional winners matched both halves of the theme: a run nobody started, plus a decision card or a short queue. No Recommended: tick every phrase the demo truly does.
- **winners:** OneDecision: a check-in starts the work, then one decision card reaches Dana.
- **red flag:** A phrase matched only by the write-up's wording.
- **fills:** section 3

### Q3.5 Dev tool test · OPTIONAL
- **ask when:** the person is a developer, or the job is tests, CI, code review or logs. Skip it if he's entering a developer-tools track.
- **question:** Take the sponsor tech out. Is what's left an existing developer tool pointed at a new user?
- **header:** `Dev tool?`
- **options:**
  1. **No, a person's chore (Recommended)**: Neither the user nor the job is a developer's. 0 of 10 AfH winners were developer tools.
  2. **Yes, a dev technique**: Keep the technique (gates, reruns, verification), point it at a non-developer's chore.
  3. **Yes, dev track exists**: Allowed only in that track.
- **multiSelect:** false
- **why:** P2. Across 7 AWS hackathons, none of 60 prizes of $1k or more went to a tests, CI or code-review tool (secondary). The Sep 22 simulation flagged developer-tool framing as a weakness.
- **red flag:** "It's for developers, but developers are humans too."
- **fills:** section 3

### Q3.6 Run ends in · OPTIONAL
- **ask when:** what a run produces sounds like a report, summary, dashboard or listing.
- **question:** What does a finished run leave in the world: the thing the person would otherwise do by hand?
- **header:** `Run ends in`
- **options:**
  1. **An action, after approval (Recommended)**: Weak evidence [inference]: Doorstep phones the neighbour; On The Line calls the provider and reads the change back.
  2. **A short decision queue**: The few items that need the person (Shopbell's 4).
  3. **A report or listing**: Red flag: Rack ended in a listing page and didn't place.
- **multiSelect:** false
- **why:** P26 [inference: drawn from the winners' one-line descriptions, not a counted pattern]. 2 of 10 let the judge hear the agent's own phone call (video). His Sep 3 post-mortem: the agent produces a real-world side effect, not a document (secondary).
- **fills:** section 3

---

## Stage 4: Sponsor tech

### Q4.1 Depth · CORE · [ARCH]
- **question:** In your design, which sponsor features do real work in the demo run, and what does each one do there?
- **header:** `Depth`
- **options:**
  1. **Runtime hosts the agent (Recommended)**: The sponsor's managed runtime, live while judges look (on AWS: AgentCore Runtime). 9 of 10 winners used AgentCore live.
  2. **Trigger starts each run**: The sponsor's scheduler or event trigger (on AWS: EventBridge). All 3 Professional winners.
  3. **Approval step pauses it**: The sponsor's human-approval feature (on AWS: Strands interrupts). 3 of 10 winners.
  4. **A hook enforces a rule**: The sponsor's hook or policy feature runs your rule (on AWS: a Strands hook, as in StepSpotter; Cedar policy, as in Doorstep). Hooks in 6 or 7 of 10.
- **multiSelect:** true
- **why:** P5, P14. Only 2 of 8 contrast entries used AgentCore. Swarm appears in 0 of 10 winners and Graph in 5; Shopbell won Professional Gold with one agent. FlakeProof used more Strands features than two of the three Professional winners, but only four agents acted. Memory and code tools count too (Tally: arithmetic in AgentCore Code Interpreter, not the model, video 2:45). The Recommended is the constraint the winners shared (where it lives), not a design.
- **winners:** Shopbell's architecture moment: AgentCore sessions, one Strands agent, EventBridge every morning through Lambda (video, 3:00 to 3:35).
- **red flag:** "I'll call the model API and mention the rest." A swarm where some agents never act.
- **note:** At the halfway check, each ticked feature needs a trace from a real run; anything without one comes out of the diagram and the copy (P14).
- **fills:** section 4

### Q4.2 Each required · CORE
- **skip if:** only one product is required.
- **question:** The rules require [products]. Does each one do a job a judge can see in the run?
- **header:** `Each one`
- **options:**
  1. **Each does real work (Recommended)**: Each has a visible job, in a file you can name. 10 of 10 winners named their sponsor tech aloud with its job (video).
  2. **One is decorative**: Stage One risk: entries must reasonably apply the required tools.
  3. **One is a build tool**: Used to build, not to run, such as an editor. Show how it was used [inference].
- **multiSelect:** false
- **why:** P5, P20. AfH Stage One asked whether the project reasonably applies the required tools, and the rules allowed AI-driven analysis. FlakeProof left dead AgentCore code pointing at files that weren't in the repo.
- **winners:** Tally: AgentCore live, plus Code Interpreter and Memory, each with a job.
- **red flag:** A required product called once on a hello-world path.
- **fills:** section 4

---

## Stage 5: What runs on its own, and when it surfaces

### Q5.1 Trigger · CORE · [ARCH]
- **question:** In your design, what starts each run without you?
- **header:** `Trigger`
- **options:**
  1. **A clock time (Recommended)**: Weak evidence: 3 of the top four run on a clock (Shopbell 6am, Instanter 7am weekdays, Tally closes the day at half past six). If Q1.3 said "when something arrives", put option 2 first instead.
  2. **An arriving event**: A new file, email, feed item or check-in (OneDecision: a check-in at the return dock).
  3. **A person clicks run**: Red flag on a background theme: FlakeProof had no trigger.
- **multiSelect:** false
- **why:** P3. All 3 Professional winners run without a person starting them, and both kinds of trigger won. The constraint that matters most: a trigger starts it, not a person.
- **winners:** Instanter: it runs itself at 7am every weekday whether or not anyone opens the page (video, 2:35).
- **red flag:** "I'll start it from the CLI for the demo."
- **fills:** section 5

### Q5.2 Decision moment · CORE
- **question:** When a run finishes, what reaches the person on the main screen, and which action waits for their yes?
- **header:** `Decision`
- **options:**
  1. **A short queue (Recommended)**: Weak evidence: fits a chore with many items (Q2.1 "hundreds"). Shopbell: of the items read, 4 needed a signature. If Q2.1 said dozens or a handful, put option 2 first.
  2. **One decision card**: OneDecision: one card, "Approve and Teach"; she approves from her phone between two jobs.
  3. **A capped question budget**: Tally: exactly two questions, a budget enforced in code.
  4. **Everything, ranked**: Red flag: a dashboard of everything isn't surfacing only for a decision.
- **multiSelect:** false
- **why:** P4, P15. 10 of 10 winners show the human decision explicitly in the video, and 10 of 10 have a real product UI. All three shapes won.
- **winners:** Instanter: nothing commits until a licensed attorney answers (video, 2:10).
- **red flag:** One notification per item. "They can always undo it later."
- **follow-up** (always): `On a no` · When the person says no, what changes?
  1. **The rule learns (Recommended)**: Weak evidence: 2 of the top four. OneDecision's "Teach", tested before it applies; Shopbell's overturn feeds the next decision (video, 1:55).
  2. **Only that item**: Simpler. The same case comes back next time.
  3. **It goes to someone else**: On The Line sends a closure to a human queue.
- **note:** This answer is also the main screen (P15): one screen the person would use for the chore, with the headline number (Q7.2) on it.
- **fills:** sections 5, 6, 9

---

## Stage 6: The deterministic core and the refusal

### Q6.1 Rule path · CORE · [ARCH]
- **question:** On [the one call he named at Q3.3, in his words], which code decides, and what stops the model routing around it?
- **header:** `Rule path`
- **options:**
  1. **Action only behind rule (Recommended)**: The action exists only behind the rule, so even the model's worst output can't trigger it. StepSpotter's gate, in a hook, held against a prompt written to defeat it.
  2. **Code checks the output**: Model output is checked in code before anything acts (Plainletter: two model turns checked against each other).
  3. **Plain code computes it**: The model never does the math (Instanter: deadlines in plain Python with a statute trace).
  4. **The prompt tells it**: Red flag: a prompt isn't a guardrail.
- **multiSelect:** false
- **why:** P11, P12. 8 of 10 winners say aloud that the model doesn't decide (video). His Haggle placed with a price floor enforced as a tool boundary (LeverageGate, secondary). Options 1 to 3 can combine; he types the mix in Other.
- **winners:** Doorstep: a Cedar policy refuses a phone number that isn't on the allow list (video).
- **red flag:** "The model decides, then we check its confidence score." The model doing anything beyond reading, extracting, proposing and explaining.
- **fills:** section 6

### Q6.2 Refuses · CORE
- **question:** Which inputs must it refuse, and what evidence does it show each time?
- **header:** `Refuses`
- **options:**
  1. **Built to trick it (Recommended)**: Hidden instructions, look-alikes, forged fields. Countable: Tally 13 of 13 adversarial cases; Doorstep 20 of 20 forbidden actions; StepSpotter 6 of 6 trick photos.
  2. **Can't prove it**: Missing evidence, so no claim (Tally won't guess a sugar limit a photo can't establish).
  3. **Outside the chore**: Not the job, said plainly (StepSpotter refuses a wrong photo).
  4. **Not real consent**: An unclear answer isn't a yes (Instanter refuses a typo as consent).
- **multiSelect:** true
- **why:** P12, P9. 10 of 10 winners show a refusal, a guard or their own miss on purpose (video). FlakeProof did too (121 of 200 refused; @Ignore refused), so refusing is needed, not the win. Counting refusals on a set is what turns it into a number (P7).
- **red flag:** "It never refuses; it always gives its best answer."
- **note:** On screen, a refusal is a result with its reason, never an error toast (P12). Each kind ticked goes into the case set (Q7.1), with a count.
- **fills:** sections 6, 7

---

## Stage 7: Proof and breadth

### Q7.1 Case set · CORE
- **question:** How many labelled cases will the demo run over, and which are held out before you tune?
- **header:** `Case set`
- **options:**
  1. **Dozens, held out first (Recommended)**: 20 to 50 cases. Labels, and what counts as "wrong", written first; a held-out set put aside before tuning. Instanter 46, OneDecision 24, Tally's unseen photos.
  2. **Dozens, no held-out set**: Weaker: the number is on cases you tuned.
  3. **Hundreds, sample labelled**: A feed-sized set; label a sample (Shopbell's 761 items).
  4. **One to five**: Red flag: FlakeProof showed one test in one library.
- **multiSelect:** false
- **why:** P8, P9. Breadth ranked second among FlakeProof's gaps. 8 of 10 winners used synthetic data. StepSpotter published its test set: 6 of 6 trick photos rejected, one real miss reported (video). OneDecision counted 13 escalations apart from errors. His StreetProof fixed its rules before its blind test (secondary).
- **winners:** OneDecision: 24 labelled cases replayed, 0 wrong.
- **red flag:** "I'll tweak until every case passes, then report."
- **fills:** section 7

### Q7.2 Headline · CORE
- **question:** Which one number will the app compute and show first?
- **header:** `Headline`
- **options:**
  1. **Filter ratio (Recommended)**: Weak evidence: the default when Q2.1 said hundreds (Shopbell: 761 read, 30 surfaced, 4 decisions). If Q2.1 said dozens, put option 2 first.
  2. **Zero-error count**: OneDecision: 24 cases, 11 automated, 13 escalated, 0 wrong.
  3. **Errors versus by hand**: Instanter: 4 of 46 deadlines hand-counting gets wrong.
  4. **Adversarial cases refused**: Tally: 13 of 13. Doorstep: 20 of 20 forbidden actions.
- **multiSelect:** false
- **why:** P7. 3 of 3 Professional winners lead with one of the first three, and 8 of 10 winners state measured results in the video (video). FlakeProof's numbers came from one test with a known answer (Impact 3).
- **red flag:** A market statistic, or "accuracy" from the model grading itself.
- **note:** The number sits on the main screen (Q5.2), computed by the app, not typed into a slide.
- **fills:** sections 7, 9

### Q7.3 Data source · OPTIONAL
- **ask when:** a public live feed exists for the chore, or he plans real data only.
- **question:** Will the cases be synthetic, real, or synthetic plus one live feed?
- **header:** `Data source`
- **options:**
  1. **Synthetic plus one feed (Recommended)**: Weak evidence [inference]: breadth from synthetic cases, one real catch from the feed. Shopbell caught a live recall at 6am on a Sunday (video, 0:45).
  2. **Synthetic, labelled**: 8 of 10 winners. Say "synthetic" where it's shown.
  3. **Real only**: Only if you can get dozens in time.
  4. **Feed behind a login**: Red flag: Rack's sold listings went behind an eBay login mid-build.
- **multiSelect:** false
- **why:** P8, P10. Shopbell was the only winner on live feeds, and it won Professional Gold. 4 of 8 contrast entries used real data, so real data alone didn't separate.
- **fills:** section 7

---

## Stage 8: Live deployment

### Q8.1 Judge link · CORE · [ARCH]
- **question:** When a judge opens the link in the submission, what happens?
- **header:** `Judge link`
- **options:**
  1. **Live agent, own input (Recommended)**: Calls the deployed agent right then. No login, a sample ready, and the judge's own input works too.
  2. **Live agent, sample only**: Real, but a judge can't test their own case.
  3. **Login or setup first**: A test account or a clone. Weaker; many judges won't.
  4. **A replay of runs**: Red flag: FlakeProof's GET-only replay behind "Launch Live Dashboard".
- **multiSelect:** false
- **why:** P6, P16. 9 of 10 winners had a public live URL; 5 of 8 contrast entries had one too, but only 2 ran the agent live. 5 of 10 winners invite the viewer to try it (video): Instanter, no account and no key; Tally, photograph your own lunch. FlakeProof needed the answer as input. Judges likely have about 10 minutes each [uncertain].
- **red flag:** Any code or config that changes per case (FlakeProof's docstring was rewritten for one target).
- **fills:** section 8

### Q8.2 Labels · CORE
- **question:** What in the demo will be synthetic, simulated, recorded or planted, and where will you say so?
- **header:** `Labels`
- **options:**
  1. **All three places (Recommended)**: On the screen where it shows (a LIVE or DEMO badge), once early in the video, and next to the claim in the write-up. OneDecision says "synthetic" at 0:25.
  2. **Write-up only**: A video-only judge never sees it.
  3. **Nothing to label**: Everything is real and live, like Shopbell's feeds. Only if that's literally true.
  4. **Not decided yet**: Red flag: decide before building the screen.
- **multiSelect:** false
- **why:** P18. FlakeProof's video said run 4 opened a PR (it didn't) and that the agents proposed three fixes (all three were planted); its landing page called a replay "Launch Live Dashboard". The AfH rules allowed automated AI analysis, which could compare the video with the README (secondary). 8 of 10 winners used synthetic data and still won.
- **winners:** Doorstep reports its results on 40 simulated residents, said aloud (video, 3:40). His Haggle disclosed its five simulated clinic desks and placed 2nd (secondary).
- **red flag:** "Live", "active" or "agents proposed" on a screen where it isn't literally true.
- **fills:** sections 8, 10, 11

### Q8.3 Uptime · OPTIONAL
- **ask when:** the live link needs paid cloud or model credits, or judging runs on after the deadline.
- **question:** Will the link and the trigger keep running through judging, and what does the page do if the model call fails?
- **header:** `Uptime`
- **options:**
  1. **Up, fails loudly (Recommended)**: Runs through judging; a failed call says so on screen.
  2. **Silent fallback**: Red flag: fixtures shown as live.
  3. **Up until submit**: Judges may open it days later.
- **multiSelect:** false
- **why:** P3, P6. Leave the schedule running through judging, and never show a silent fallback as live (secondary: his NextStep simulation). [uncertain] How long each winner stayed up isn't recorded.
- **red flag:** "I'll turn it off after submitting to save credits."
- **follow-up** (always): `Cost` · Is the cost per day covered through the judging window?
  1. **Covered (Recommended)**: Credits or a free tier last past the results date.
  2. **Not yet known**: Check the price page before building on it.
  3. **Not covered**: Plan a cheaper runtime or a capped schedule now.
- **fills:** section 8

---

## Stage 9: The product: audit trail, legibility, privacy

The main screen itself is settled in Q5.2.

### Q9.1 Audit trail · CORE · [ARCH]
- **question:** In your design, how does each decision get a record nobody can silently edit, and where does the person see it?
- **header:** `Audit trail`
- **options:**
  1. **Log they can overturn (Recommended)**: The person sees each decision and its reason, and can overturn it. Shopbell shows its own miss and the owner's overturn feeding the next decision (video, 1:55).
  2. **Hash-chained log**: OneDecision's hash-chained audit log.
  3. **Locked storage**: Instanter: S3 Object Lock, on the sponsor's own cloud.
  4. **Append-only, database enforced**: On The Line: a chain the database refuses to edit (video).
- **multiSelect:** true
- **why:** P13, P4. All 3 Professional winners show an audit trail as a feature. Option 1 is what the person sees; options 2 to 4 are ways to store it, and they combine.
- **red flag:** "It's in the server logs", or "only in the database".
- **note:** The quiet items (the ones that never reached the person) live in this trail too, so a wrong skip can be found.
- **fills:** sections 9, 5

### Q9.2 Legible · OPTIONAL
- **ask when:** before the first test recording, or when the main screen has dense text, charts or small fonts.
- **question:** Will the main screen read at video size: font, contrast, the main button above the fold, captions kept off the UI?
- **header:** `Legible`
- **options:**
  1. **Test-recorded early (Recommended)**: Record 10 seconds now, watch it small, fix it.
  2. **Check at the end**: Late fixes eat video time.
  3. **Not considered**: Red flag: FlakeProof's dashboard text was unreadable at watch size.
- **multiSelect:** false
- **why:** P15. FlakeProof's caption box covered about 25% of the frame (secondary). He caught tiny low-contrast text and a main button below the fold in StreetProof himself, then won (secondary). [uncertain] The winners' frames weren't analysed.
- **red flag:** Captions burned in over the UI.
- **fills:** section 9

### Q9.3 Privacy · OPTIONAL
- **ask when:** the chore handles personal data (health, family, money, letters, phone numbers).
- **question:** Which privacy or consent promise will the code keep, and where does the person see it?
- **header:** `Privacy`
- **options:**
  1. **Enforced in code (Recommended)**: The promise is kept by code, not the prompt. Shoulder's promises are checked in code; Plainletter pins its redaction in code.
  2. **Shares only approved**: Cahoots: the agent publishes one card the person approved.
  3. **Consent before acting**: Instanter: a typo is refused as consent.
  4. **Stated, not enforced**: Red flag: a promise that lives only in the copy.
- **multiSelect:** false
- **why:** 5 of 10 winners built privacy or consent in and showed it (video). Not one of agent 1's patterns; it moves Design and Impact through trust [inference].
- **winners:** Shoulder catches a staged attack on one agent's secret with a privacy check (video).
- **fills:** section 9

---

## Stage 10: The video

**Evidence:** the captions of all 10 AfH winners' videos and FlakeProof's, read Oct 14 (video). It covers what is said and when, not what is on screen; frames weren't analysed, so on-screen timing and legibility stay [uncertain]. FlakeProof matched the winners on refusals, the human deciding and a closing line, so those are needed, not the win. It missed: opening on a person and the stakes, a statistic in the first minute, a live run or a try-it line, the sponsor tech named aloud, measured results early, and a scripted, filler-free narration. Q10.1 to Q10.7 are kickoff decisions. Q10.8 to Q10.10 wait until a script exists.

### Q10.1 Opening · CORE
- **question:** What does a judge hear in the first 20 seconds?
- **header:** `Opening`
- **options:**
  1. **Scene with a person (Recommended)**: Tally: nine children before 8am, one adult, no free hands, then her name. 6 of 10 winners name the person or are the user.
  2. **The stakes in one line**: Instanter: a tenant has 7 days to answer, or loses by default.
  3. **The product doing it**: Doorstep: a real agent call to a neighbour who feels dizzy.
  4. **Me as the user**: StepSpotter's builder: he never owned a home.
- **multiSelect:** false
- **why:** P1, P17. 10 of 10 winners open on a person, a concrete scene or the stakes within about 20 seconds (video). Only Tally says the builder's name first, for two seconds. FlakeProof opened on the builder's name and "for Java developers". The first 30 seconds may set most of the score [uncertain: two secondary sources, not measured].
- **winners:** OneDecision at 0:10: Dana runs returns for a camera rental company and makes the same call 20 times a week.
- **red flag:** A title card, a team intro, or the builder first.
- **note:** Build it from the outsider detail he gave in Q1.4.
- **fills:** section 10

### Q10.2 Statistic · CORE
- **question:** Which real-world number, with a source, lands in the first minute?
- **header:** `Statistic`
- **options:**
  1. **A share or rate (Recommended)**: Weak evidence: the most common kind (3 of the 9). Shoulder 0:30: three in four families end up with one person carrying it.
  2. **Scale of the problem**: On The Line 0:20: 19 million referrals a year. Plainletter 0:30: 861 help desks.
  3. **A real past event**: Doorstep 0:50: the 2021 BC heat dome.
  4. **Your own run's number**: Instanter 0:10: 4 of 46 deadlines hand-counting gets wrong.
- **multiSelect:** false
- **why:** P7. 9 of 10 winners give a real-world number in the first minute; StepSpotter, told in first person, is the one exception (video). Most land by 0:30. FlakeProof's first number came at 2:15.
- **red flag:** No number before 1:00, or a number with no source.
- **note:** Check the source now (one search). It goes under "Context" in the write-up, never under "Measured".
- **fills:** sections 10, 11

### Q10.3 Real run · CORE
- **question:** Which real run does the judge see, and how is it called out as real?
- **header:** `Real run`
- **options:**
  1. **Live run, said aloud (Recommended)**: Instanter at 1:05 calls out a real run, happening now. 10 of 10 winners call out a live or real run in some form.
  2. **The run nobody watched**: Shopbell 0:45: at 6am on a Sunday it caught a live recall and emailed the owner.
  3. **Judge tries it**: Tally 3:20: photograph your own lunch. Instanter 2:45: no account, no key. 5 of 10 invite the viewer.
  4. **Recorded replay**: Red flag: FlakeProof showed "previous recorded runs".
- **multiSelect:** true
- **why:** P3, P6, P17. The top four show autonomy as a moment, not a description (Shopbell 0:45, OneDecision 0:30, Tally 2:20, Instanter 2:35); FlakeProof only described its runner, at 4:25 (video). In a model contest, let the judge see or hear the model's own output: Doorstep and On The Line play the agent's real phone call (2 of 10).
- **winners:** OneDecision at 2:35: the same agent, deployed on AgentCore Runtime, returns the same decision card.
- **red flag:** A "scheduled" run started by hand just before recording.
- **fills:** sections 10, 5

### Q10.4 Refusal · CORE
- **question:** Which refusal, guard or miss of its own will the video show on purpose?
- **header:** `Refusal`
- **options:**
  1. **A refusal with reason (Recommended)**: Say the rule first, then show it. Tally refuses to guess a sugar limit a photo can't establish (2:10).
  2. **An attack it stops**: StepSpotter: a prompt written to defeat the gate, refused. Shoulder: a staged attack, caught.
  3. **Its own real miss**: Shopbell shows a dog food recall it missed, and the owner's overturn (1:55).
  4. **None shown**: Red flag: 10 of 10 winners show one.
- **multiSelect:** true
- **why:** P12. 10 of 10 winners show a refusal, a guard or their own miss on purpose (video), and the top four do it at 1:55 to 2:15. FlakeProof did too, so it's needed, not the win. A judge who doesn't know refusal is the point can read it as broken (secondary: his NextStep simulation).
- **winners:** Instanter at 2:00: a typo instead of a decision is refused as consent.
- **red flag:** A refusal with no reason given, or one that looks like an error.
- **fills:** section 10

### Q10.5 Human decision · CORE
- **question:** How will the video frame the human decision (pick the shape, then type your one line in Other)?
- **header:** `Human call`
- **options:**
  1. **One line, mid-run (Recommended)**: 3 of the top four put it mid-run: OneDecision 1:05; Instanter 2:10, "The interrupt is the product"; Tally 2:30.
  2. **Up front, as the promise**: Shopbell 0:20: "Silence is the product."
  3. **A budget in code**: Tally: exactly two questions, a budget enforced in code.
  4. **Described, not shown**: Red flag: 10 of 10 winners show it.
- **multiSelect:** false
- **why:** P4. 10 of 10 winners show the human decision explicitly (video). Among the top four it's framed in one line, two of them as "X is the product". FlakeProof also showed the human deciding, so showing it is needed, not the win.
- **winners:** OneDecision: she approves from her phone between two jobs, and that's the whole interruption.
- **note:** The line can come back as the closing line (Q10.9).
- **fills:** section 10

### Q10.6 Tech aloud · CORE
- **question:** When will you name each sponsor product aloud, tied to what it does, and say what the model never decides?
- **header:** `Tech aloud`
- **options:**
  1. **One moment, last third (Recommended)**: Weak evidence: what most winners did (Shopbell 3:00 to 3:35, Doorstep 3:15, On The Line 3:55). In a big field, add a few words early too [inference].
  2. **Within the first 50 seconds**: Instanter 0:45: a Strands graph on Bedrock AgentCore, and the model never computes a date.
  3. **First line, again later**: OneDecision: Strands on Bedrock in its first sentence, AgentCore Runtime at 2:35.
  4. **Captions or logo only**: Red flag: FlakeProof never said Strands, Bedrock or Nova aloud.
- **multiSelect:** false
- **why:** P19, P11. 10 of 10 winners name the sponsor tech aloud, usually in one short moment in the last third (video), and 8 of 10 say aloud that code, not the model, decides. This settles P19's old [uncertain]: naming it aloud is what all the winners did and FlakeProof didn't.
- **winners:** Tally at 2:45: Strands agents by day, a graph at night, and the arithmetic in AgentCore Code Interpreter, not the model.
- **red flag:** A list of product names with no job attached.
- **note:** The sentence shape, which he writes: "[product] does [job]; the model never [the call from Q3.3]."
- **fills:** sections 10, 11

### Q10.7 Length · CORE
- **question:** The cap is [max]. How long will the video run, and is it scripted word for word?
- **header:** `Length`
- **options:**
  1. **Most of the cap (Recommended)**: About 60 to 90% of the max, scripted, no filler. On AfH's 5:00 cap, 7 of 10 winners ran 3:00 to 4:30.
  2. **Well under, scripted**: StepSpotter 2:39 and Instanter 2:55 both placed.
  3. **Right at the cap**: Cahoots ran 4:55 and placed, but there's no margin for one more edit.
  4. **Unscripted take**: Red flag: FlakeProof ran 4:58 with many filler words.
- **multiSelect:** false
- **why:** P17. Winners ran 2:39 to 4:55, median about 4 minutes, all under the 5:00 cap, and no winner's captions show filler words (video). OneDecision used synthetic narration. The band for other caps is scaled from one event [inference].
- **red flag:** "I'll talk over a screen recording."
- **note:** Q10.8 to Q10.10 wait until a script exists, after the halfway check (Q13.1). The full beat order is the Video script skeleton in IDEA-TEMPLATE.md, section 10.
- **fills:** section 10

### Q10.8 Result at · OPTIONAL
- **ask when:** at script time.
- **question:** Where in the video does your measured result appear?
- **header:** `Result at`
- **options:**
  1. **After the run (Recommended)**: Weak evidence: 3 of the 5 timed examples (OneDecision 1:30 and 3:10, Doorstep 3:40, Tally 4:00). The number then has a run behind it.
  2. **Early, maybe in title**: Instanter: 0:10, and in its title. Shopbell 0:30.
  3. **Not stated**: 2 of 10 winners didn't (Shoulder, On The Line). Impact is judged on what's demonstrated.
- **multiSelect:** false
- **why:** P7. 8 of 10 winners state measured results, anywhere from 0:10 to 4:00 (video). FlakeProof's came at 2:15, so timing alone didn't separate.
- **fills:** section 10

### Q10.9 Closing · OPTIONAL
- **ask when:** at script time.
- **question:** What does the closing line restate?
- **header:** `Closing`
- **options:**
  1. **The promise (Recommended)**: 10 of 10 winners close on the promise. OneDecision: teach the agent once, it handles the next hundred.
  2. **Who decides**: Instanter: the agent stops, a licensed attorney decides.
  3. **The name, then try it**: Doorstep ends "Try it today." Shoulder and On The Line close on the name.
- **multiSelect:** false
- **why:** 10 of 10 winners close on a line that restates the promise (video). FlakeProof had one too, so it's needed, not the win.
- **fills:** section 10

### Q10.10 Limitation · OPTIONAL
- **ask when:** a known gap exists (synthetic only, one region, a simulated part).
- **question:** How will the video say its one honest limitation?
- **header:** `Limitation`
- **options:**
  1. **One line, early (Recommended)**: OneDecision 0:25: everything runs on synthetic data.
  2. **Shown as a miss**: Shopbell 1:50: it also gets things wrong, and here's what that looks like.
  3. **A long disclaimer**: Red flag: FlakeProof's 33 seconds read as an apology (secondary), and its 3:55 disclaimer landed on its main result.
- **multiSelect:** false
- **why:** P18. Winners state honesty early and keep it small (video).
- **fills:** section 10

---

## Stage 11: The write-up

### Q11.1 First screen · CORE
- **question:** What does the first screen of the write-up show, in order?
- **header:** `First screen`
- **options:**
  1. **Logline, then measured number (Recommended)**: Then the context statistic with its source, under its own heading, and one heading per criterion using the sheet's names. 3 of 3 Professional winners lead with a number.
  2. **Logline, then the scene**: The person's story first, the number lower down.
  3. **Background first**: Red flag: "Inspiration" first, the product in paragraph four.
- **multiSelect:** false
- **why:** P1, P7, P19. Judges may score from text alone, in about 10 minutes [uncertain]. Keep "Measured" (with how the set was made, and that it wasn't used for tuning) apart from "Context" (CHECKLIST). The sponsor products go in the first paragraph by name. Headings per criterion are CHECKLIST advice; the winners' headings aren't recorded [uncertain].
- **red flag:** A cited statistic that reads like your own result.
- **fills:** section 11

### Q11.2 Tech section · CORE
- **question:** Will the write-up list each sponsor product with its file and the run where it fired, and say which part is the model and which is code?
- **header:** `Tech section`
- **options:**
  1. **Product, file, run (Recommended)**: Plus one line: "the model extracts; [file] decides". A screener can check each claim.
  2. **Product list only**: A screener can't check it.
  3. **Not written yet**: Write it from Q4.1 and Q6.1.
- **multiSelect:** false
- **why:** P5, P11, P14. AfH's rules allowed AI-driven screening, and a video-only judge has to take the write-up's word for depth (secondary). FlakeProof's copy said "4-agent swarm" when four agents acted and none handed off, and it left dead AgentCore code. [uncertain] The winners' sections aren't recorded.
- **red flag:** A feature listed that never ran, or code that calls nothing.
- **fills:** sections 11, 4

---

## Stage 12: Checks, bonus and submission hygiene

### Q12.1 Checks · CORE
- **question:** When will you check the script against the recorded run, the write-up against the README, and every link from a logged-out browser?
- **header:** `Checks`
- **options:**
  1. **Each before its step (Recommended)**: The script before recording, the write-up before submitting, every link a day early, logged out.
  2. **All at submit time**: Little margin to fix what's found.
  3. **Not planned**: Red flag: FlakeProof's video claimed a PR the run never opened.
- **multiSelect:** false
- **why:** P18, P20. FlakeProof's video said run 4 opened a PR (it didn't), its README said the PR was by the maintainers (a research account opened it), and its video went public 1h51m late. A transcript-versus-README diff would catch the first two (secondary).
- **red flag:** "I'll make it public after I submit." A commit after the deadline "just to fix the README".
- **fills:** sections 12, 10

### Q12.2 Bonus · OPTIONAL
- **ask when:** the sheet lists a bonus.
- **question:** When will the bonus items be done, with their exact conditions from the rules summary met?
- **header:** `Bonus`
- **options:**
  1. **After the core works (Recommended)**: Margin points, never instead of the core. Exact title words, public before the deadline, found logged out.
  2. **During the build**: Only if the core is on track at halfway.
  3. **Skip them**: Free points lost; only if the core needs the time.
- **multiSelect:** false
- **why:** P21. AfH posts were worth 0.2 each, up to 0.6, only if public before the deadline with "Agents for Humans" in the title. Only 4 of 10 winners had posts and the top three had none; 7 of 8 contrast entries had them. FlakeProof's audit: about 3.1, or about 3.7 if all three posts had counted.
- **red flag:** A post started before the end-to-end path runs, or drafted but not published.
- **fills:** section 12

---

## Stage 13: Scope and time

### Q13.1 Halfway · CORE
- **question:** What has to run end to end by halfway?
- **header:** `Halfway`
- **options:**
  1. **Thin full path (Recommended)**: Every piece once, crudely, on the sponsor's runtime. The freeze leaves about 15% of the time for the screen and the video.
  2. **Core logic first**: Deployment and the screen left for the end. Weaker.
  3. **One piece polished**: Red flag: Dialdown's deadline passed mid-build.
- **multiSelect:** false
- **why:** P25, P15, P17. CHECKLIST: one end-to-end path by halfway, then improve it rather than extend it, and about 15% of the time for one clean screen and the video. A scripted, filler-free video takes real time (video: no winner's captions show filler). Dialdown (Hack-Nation 6) wasn't submitted (secondary).
- **winners:** OneDecision: one chore, deployed and triggered, with 199 tests.
- **red flag:** "Deployment is the last step." "I'll record the video the night before."
- **follow-up** (always): `Clock times` · The halfway check and the feature freeze: these times?
  1. **[Halfway], [freeze] (Recommended)**: Computed from now and the Q0.2 target; the freeze leaves about 15% of the time before it. Each gets a silent visual timer.
  2. **Earlier, more margin**: Pull both forward a few hours.
  3. **Later, more build time**: Red flag if the freeze leaves under 15% for the screen and the video.
- **note:** If the path isn't running at halfway, ask Q13.3.
- **fills:** section 13

### Q13.2 One chore · OPTIONAL
- **ask when:** the logline or Q1.2 holds two jobs, or he says "it also does...".
- **question:** Is there a second chore hiding in the idea, and which one gets cut?
- **header:** `One chore`
- **options:**
  1. **One chore only (Recommended)**: Every AfH winner did one chore end to end.
  2. **Keep the decision one**: Two related chores: keep the one with the human decision, cut the other.
  3. **A platform for many**: Red flag: breadth goes in the cases, not the features.
- **multiSelect:** false
- **why:** P25, P8. FlakeProof's narrowness that hurt was narrow proof, not a narrow function.
- **fills:** section 13

### Q13.3 Cut list · OPTIONAL
- **ask when:** the halfway check fails, or the plan looks tight at kickoff.
- **question:** If you're behind, what is safe to cut first?
- **header:** `Cut list`
- **options:**
  1. **Extra agents or tools (Recommended)**: Anything that doesn't act in the demo run. Shopbell won with one agent.
  2. **Bonus items**: Margin points only (P21).
  3. **The live feed**: Keep the synthetic breadth; Q7.3 rests on an inference.
  4. **Any second screen**: Keep the one main screen.
- **multiSelect:** true
- **why:** P25, P14, P21, P10. Before cutting a component, check what the later pieces need from it (his prevention rule). Never cut the deployment, the trigger, the held-out set or the video time.
- **fills:** section 13

---

## Stage 14: Team split

### Q14.1 Team · CORE
- **skip if:** he's already said he's solo.
- **question:** Solo or a pair this time, and if a pair, what does the teammate own end to end?
- **header:** `Team`
- **options:**
  1. **Teammate owns visuals (Recommended)**: Weak evidence [inference]: the video and the screen, what judges see most.
  2. **Teammate owns evals**: The case set, the held-out split and the number.
  3. **Teammate owns deploy**: The runtime, the trigger and the judge link.
  4. **Solo this time**: 6 of 10 AfH winners were solo, including Shopbell and Instanter.
- **multiSelect:** false
- **why:** P22. Tally (Grand Prize) was a pair, and no winning team was larger than 3. Use a teammate for what judges see most, not for more features [inference]. The architecture stays his (Gate A) either way.
- **red flag:** Both people coding features until the last night.
- **fills:** section 14

---

## Loop questions

### R1 Re-check · LOOP
- **when:** How to use, items 9 to 12. At most 2 rounds per section, plus one more at W1.
- **question:** [The topic, in plain words]: [the gap, in one line]. What the winners did: [the bar, with its count]. What do you want to do?
- **header:** `Re-check`
- **options:**
  1. **Take the suggestion (Recommended)**: [The recommended alternative, taken from the question's first option or its red flag line, with its count.]
  2. **Keep mine, log risk**: It goes in Known risks with the bar it misses.
  3. **Cut this section**: R2 asks why. The spec says "cut, because ...".
- **multiSelect:** false
- **why:** He asked for the plan to be pushed until it gives him the best chance of winning. Other carries his own revision, which is then checked again. Stage One form: for a Stage One line, option 2 becomes **Rethink the idea** (back to Stage 1 or 3), because a Stage One fail can't be kept as a risk. Gate A form: for one of his three lines, say the clash in plain words, with no winners' bar and no other project named; option 1 becomes **Change my line**, with no Recommended mark, option 3 becomes **Change that answer**, and option 2 stays.

### R2 Cut reason · LOOP
- **when:** he picks "Cut this section" in R1.
- **question:** Why cut [section]?
- **header:** `Cut reason`
- **options:**
  1. **Low weight here**: The sheet's weights put little on it.
  2. **Not in these rules**: The event doesn't ask for it.
  3. **No time left**: The halfway check failed, and it's on the cut list.
  4. **Doesn't fit the chore**: It would have to be faked to fit.
- **multiSelect:** false
- **why:** No Recommended: the reason is his. The spec records "cut, because [reason]".

### P1 Pause · LOOP
- **when:** after 9pm before Gate A (right after Stage 0) and again before Stage 4, or when the hour's time box runs out.
- **question:** It's [time]. Stop here and pick up at [the next step, in plain words] tomorrow morning?
- **header:** `Pause`
- **options:**
  1. **Stop, resume tomorrow (Recommended)**: Architecture is hard thinking, and his rule is no hard cognitive work at night. The spec notes where it stopped.
  2. **Finish this stage only**: Close the open stage's loop, then stop. Offer it only when a stage is open.
  3. **Keep going, 20 minutes**: Daytime overrun only. Don't offer this option after 9pm.
- **multiSelect:** false
- **why:** CLAUDE.md: don't let him start hard cognitive work at night; bounded chunks with clear stop signals.

### W1 Win check · LOOP
- **when:** after the last stage, before writing the spec (How to use, item 12).
- **question:** Win check: [n] lines fail ([each line, with its stage]). What next?
- **header:** `Win check`
- **options:**
  1. **Fix them now (Recommended)**: Back to each stage for one more R1 round, heaviest-weighted criterion first.
  2. **Log them as risks**: Each goes in Known risks. Not allowed for a Stage One line.
  3. **Pause, finish later**: The spec notes what's still open.
- **multiSelect:** false
- **why:** How to use, items 12 and 13. The lines are the Judge's 60-second test, the Scorecard verdict for each of the five criteria, and every Stage One line, all in IDEA-TEMPLATE.md. When nothing fails, ask instead: "Every line passes or is a deliberate cut. Write the spec now?" with **Write it now (Recommended)** and **Review weakest section** (the heaviest-weighted criterion whose sections have the weakest answers).
