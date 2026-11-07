# Accessible Website Challenge: cheat sheet

Sat Nov 7, Rita Chiu Classroom @ TSC, 2:00 to 4:30pm.
The schedule lists 2:00 "Workshop + Boba", then workshop blocks to 4:30. My read: the talk comes first, then the challenge. Confirm in the room.
The task (handbook): every team gets the same website and adds accessible features, "using the presentation tips". Team of 1 to 4.

## Ask in the first 2 minutes
- Are AI tools allowed? Browser extensions?
- How do we hand it in: a repo link, a zip, or a live demo to the judges?
- What's the time limit?

## The score (out of 100)
- Keyboard navigation and focus: 25
- Visual accessibility: 20
- Semantic HTML and screen readers: 20
- Testing and technical quality: 20
- Forms and interaction: 15

## Plan for about 90 minutes
1. **0 to 10 min, baseline.** Run Lighthouse (Chrome DevTools > Lighthouse > Accessibility) and axe DevTools. Screenshot both scores. Tab through the whole page once and write down what breaks.
2. **10 to 70, fix in score order.** Suggested split: Sid on keyboard and forms, Trini on visual and semantic. Both test.
3. **70 to 85, re-test.** Lighthouse and axe again, screenshot the after scores. One keyboard-only walk. One minute with a screen reader.
4. **85 to 90, the fix list** for the judges (template at the bottom).

## 1. Keyboard and focus (25)
- [ ] Tab reaches every link, button, input and menu, in reading order. (2.1.1, 2.4.3)
- [ ] Clickable `div`/`span` becomes a real `<button>` or `<a href>`. Keyboard support comes free.
- [ ] Visible focus on everything. Never `outline: none` alone. (2.4.7)
  `:focus-visible { outline: 3px solid #1a5fff; outline-offset: 2px; }`
  The ring needs 3:1 against the background.
- [ ] Skip link as the first Tab stop, visible when focused. (2.4.1)
  `<a class="skip" href="#main">Skip to main content</a>`
- [ ] No `tabindex` above 0. Only 0 or -1.
- [ ] Menus, modals, accordions: Enter or Space opens, Esc closes. Focus moves into a modal, and back to its button after.
- [ ] No keyboard trap. (2.1.2)
- [ ] A sticky header never hides the focused item. (2.4.11, new in 2.2)
  `html { scroll-padding-top: 80px; }` (use the header's height)

## 2. Visual (20)
- [ ] Text contrast at least 4.5:1. Large text (18pt, or 14pt bold) at least 3:1. (1.4.3)
- [ ] Icons, input borders and focus rings at least 3:1. (1.4.11)
  Check: DevTools colour picker shows the ratio, or WebAIM's contrast checker.
- [ ] Colour is never the only signal: links in text are underlined; errors have words or an icon, not just red. (1.4.1)
- [ ] At 200% zoom nothing is cut off. At 320px wide, no sideways scroll. (1.4.4, 1.4.10)
- [ ] Sizes in `rem`, line-height about 1.5.
- [ ] Reduced motion respected:
  `@media (prefers-reduced-motion: reduce) { * { animation: none !important; transition: none !important; scroll-behavior: auto !important; } }`
- [ ] Anything that moves on its own gets a pause button. (2.2.2)
- [ ] Click targets at least 24 by 24 CSS px. (2.5.8, new in 2.2)

## 3. Semantic HTML and screen readers (20)
- [ ] `<html lang="en">` and a real `<title>`. (3.1.1, 2.4.2)
- [ ] Landmarks: `<header>`, `<nav>`, one `<main id="main">`, `<footer>`. (1.3.1)
- [ ] One `<h1>`, then h2 and h3 in order, no skipped levels. Headings are real `<h2>`, not bold divs.
- [ ] Images: alt text that says what matters. Decorative ones get `alt=""`. (1.1.1)
- [ ] Icon-only buttons: `aria-label="Close menu"`, and the icon gets `aria-hidden="true"`. (4.1.2)
- [ ] Lists are `ul`/`ol`. Tables have `<th scope="col">` and a `<caption>`.
- [ ] Current page in the nav: `aria-current="page"`. Toggles: `aria-expanded="true"` or `"false"`.
- [ ] Link text says where it goes, never "click here". (2.4.4)
- [ ] Native element first. ARIA only when no element does the job.

## 4. Forms and interaction (15)
- [ ] Every input has a visible `<label for="id">`. A placeholder is not a label. (1.3.1, 3.3.2)
- [ ] Radio and checkbox groups sit in `<fieldset>` with a `<legend>`.
- [ ] Right types and autocomplete: `type="email" autocomplete="email"`, `autocomplete="name"`, `type="tel"`. (1.3.5)
- [ ] Required fields: `required`, plus "(required)" in the label text.
- [ ] Errors: words next to the field, linked with `aria-describedby`, plus `aria-invalid="true"`. On submit, focus goes to the first error. (3.3.1)
- [ ] Success and status messages in `<div role="status" aria-live="polite">`. (4.1.3)
- [ ] An error never wipes what they typed.

## 5. Testing and technical quality (20): show the proof
- [ ] Before and after: Lighthouse accessibility score and axe issue count, a screenshot of each.
- [ ] One keyboard-only walk, start to finish.
- [ ] Screen reader: Windows Narrator (Ctrl+Win+Enter) or NVDA; Mac VoiceOver (Cmd+F5). Move through the page by headings once.
- [ ] HTML validates (validator.w3.org), or at least no new errors.
- [ ] The fix list below, so judges can check each claim.

## Fix list for the judges (fill in as you go)
| Fix | Category | WCAG |
|---|---|---|
| Added a skip link | Keyboard | 2.4.1 |
| Visible focus ring on every control | Keyboard | 2.4.7 |
| Labels on every form field | Forms | 1.3.1, 3.3.2 |
| Body text contrast from [before] to [after] | Visual | 1.4.3 |
| | | |

## Install before 2pm (both laptops)
- axe DevTools (Chrome extension, free)
- WAVE (Chrome extension, free)
- Lighthouse is already in Chrome DevTools
- Optional: NVDA (free, Windows). Narrator is built in.

Contrast numbers and the WCAG 2.2 items were checked against w3.org/TR/WCAG22 on Nov 7.
