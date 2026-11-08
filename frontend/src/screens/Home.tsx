import { useEffect, useState } from 'react'
import { Activity, Camera, Hand, LineChart, ShieldCheck, Sparkles, Target } from 'lucide-react'
import { getStatus, type Status } from '../api'
import BlurText from '../components/BlurText'
import ClickSpark from '../components/ClickSpark'
import CountUp from '../components/CountUp'
import Magnet from '../components/Magnet'
import Reveal from '../components/Reveal'
import RotatingText from '../components/RotatingText'
import ShinyText from '../components/ShinyText'
import SiteNav from '../components/SiteNav'
import Threads from '../components/Threads'
import { navItems } from '../nav'

const steps = [
  { icon: Camera, title: 'Setup check', text: 'Hold still for 3 seconds. Java measures tracking, frame rate and shake, and decides. Gemini looks at one frame and tells you what to fix.' },
  { icon: Hand, title: 'Pick your two fingers', text: 'Pinch them together three times, or tap them. Any two work, so it fits hands that are missing a finger.' },
  { icon: Target, title: 'Run the drill', text: 'The 3D instrument follows the point between your fingers and grips when you pinch. Hit five rings in order.' },
  { icon: LineChart, title: 'See your progress', text: 'Four numbers with their formulas, a coach that explains them, and clinic mode for progress over time.' },
]

const measures = [
  { name: 'Accuracy', formula: '100 x (1 - mean distance off the straight path / 0.10 screen widths)', better: 'Higher is better' },
  { name: 'Smoothness', formula: 'One smooth move scores 100. Every stop and correction lowers it.', better: 'Higher is better' },
  { name: 'Speed', formula: 'Seconds from the first ring to the last, per move.', better: 'Lower is better' },
  { name: 'Tracked', formula: 'Share of frames where your hand was found with confidence.', better: 'Under 80% and it refuses to score' },
]

function LiveCard() {
  const [status, setStatus] = useState<Status | null>(null)
  const [down, setDown] = useState(false)

  useEffect(() => {
    let alive = true
    const tick = () => getStatus().then(s => { if (alive) { setStatus(s); setDown(false) } }).catch(() => alive && setDown(true))
    tick()
    const id = window.setInterval(tick, 2000)
    return () => { alive = false; window.clearInterval(id) }
  }, [])

  const rows = status && !down
    ? [
        { label: 'Camera', value: status.status, ok: status.status.includes('open') },
        { label: 'Frames per second', value: status.fps.toFixed(0), ok: status.fps >= 20 },
        { label: 'Hand', value: status.handFound ? 'tracking' : 'not in view', ok: status.handFound },
        { label: 'Gemini', value: status.gemini, ok: status.gemini === 'online' },
      ]
    : []

  return (
    <div className="liquid-glass animate-float rounded-[26px] p-6 ring-1 ring-ink/10">
      <div className="flex items-center gap-2 text-sm font-semibold text-steel">
        <span className={`h-2.5 w-2.5 rounded-full ${down ? 'bg-alert' : 'bg-mint animate-pulse-dot'}`} />
        {down ? 'Java app not running' : 'Live from this laptop'}
      </div>
      {down ? (
        <p className="mt-4 text-sm leading-6 text-steel">Start it with <code className="rounded bg-ink/5 px-1.5 py-0.5">mvn spring-boot:run</code> in the project folder, then this card fills in.</p>
      ) : (
        <dl className="mt-4 divide-y divide-ink/10">
          {rows.map(r => (
            <div key={r.label} className="flex items-center justify-between gap-4 py-2.5">
              <dt className="text-sm text-steel">{r.label}</dt>
              <dd className="flex items-center gap-2 text-sm font-semibold tabular-nums text-ink">
                <span className={`h-2 w-2 rounded-full ${r.ok ? 'bg-mint' : 'bg-tape'}`} aria-hidden="true" />
                {r.value}
              </dd>
            </div>
          ))}
          {!status && <p className="py-3 text-sm text-steel">Connecting…</p>}
        </dl>
      )}
      <a href="/train" className="mt-5 inline-flex w-full items-center justify-center rounded-full bg-ink px-5 py-3 text-sm font-semibold text-white transition-[scale] duration-150 active:scale-[0.97]">
        Open the 3D trainer
      </a>
    </div>
  )
}

export default function Home() {
  return (
    <>
      <SiteNav items={navItems} activeHref="/" />
      <main id="top" className="relative overflow-x-clip text-ink">
        <section className="relative isolate">
          <div aria-hidden="true" className="pointer-events-none absolute inset-0 -z-10 [mask-image:linear-gradient(to_bottom,black_55%,transparent)]">
            <Threads color="#16181d" accentColor="#ff6a2b" amplitude={1.3} distance={0.3} enableMouseInteraction lineCount={44}
              thickness={0.7} softness={1.2} speed={0.45} brightness={1} opacity={0.36} angle={-14} />
          </div>
          <div className="mx-auto grid min-h-[100svh] max-w-7xl items-center gap-12 px-5 pb-16 pt-28 sm:px-8 lg:grid-cols-[minmax(0,1.2fr)_minmax(0,0.8fr)]">
            <div className="max-w-2xl">
              <p className="mb-5 inline-flex items-center gap-2 rounded-full bg-white/70 px-3 py-1.5 text-xs font-semibold uppercase tracking-[0.14em] text-steel ring-1 ring-ink/10 backdrop-blur">
                <Sparkles size={14} className="text-tape" /> HackTrent 2026
              </p>
              <h1 className="font-display text-[clamp(2.7rem,6.1vw,5.2rem)] font-[780] leading-[0.92]">
                <BlurText text="Control with precision." animateBy="words" direction="top" delay={90} className="block" />
                <ShinyText text="Improve with evidence." color="#16181d" shineColor="#ff6a2b" speed={3.2} className="block" />
              </h1>
              <Reveal delay={0.45}>
                <p className="mt-7 max-w-[34rem] text-lg leading-8 text-steel sm:text-xl sm:leading-9">
                  A laptop webcam becomes a hand precision trainer. Move two fingers and a 3D robotic instrument copies them, so you can{' '}
                  <RotatingText
                    texts={['hit every ring', 'pinch to grip', 'see your numbers', 'track your progress']}
                    highlight highlightColor="#ff6a2b" effect="slide" direction="up" rotationInterval={2600} staggerDuration={0.012}
                    mainClassName="inline-flex overflow-hidden rounded-[4px] px-1.5 align-baseline font-semibold text-ink"
                  />.
                </p>
              </Reveal>
              <Reveal delay={0.6}>
                <div className="mt-10 flex flex-wrap items-center gap-x-6 gap-y-4">
                  <Magnet padding={70} magnetStrength={3} maxOffset={10}>
                    <ClickSpark sparkColor={['#16181d', '#ff6a2b']} sparkCount={14} sparkRadius={52} sparkSize={10} sparkWidth={2} duration={550} shape="line" className="h-auto! w-auto!">
                      <a href="/train" className="inline-flex min-h-13 items-center rounded-full bg-tape px-7 text-base font-semibold text-ink shadow-[inset_0_-2px_0_rgb(22_24_29/0.16)] transition-[scale] duration-150 ease-out active:scale-[0.96]">
                        Start training
                      </a>
                    </ClickSpark>
                  </Magnet>
                  <a href="/clinic" className="inline-flex min-h-13 items-center rounded-full bg-white/70 px-6 text-base font-semibold text-ink ring-1 ring-ink/20 backdrop-blur transition-[box-shadow] duration-200 hover:ring-ink/60">
                    Open clinic mode
                  </a>
                </div>
              </Reveal>
            </div>
            <Reveal delay={0.25} className="mx-auto w-full max-w-md"><LiveCard /></Reveal>
          </div>
        </section>

        <section id="how" className="mx-auto max-w-7xl px-5 py-24 sm:px-8">
          <Reveal><h2 className="font-display text-3xl font-[760] leading-none sm:text-5xl">How it works</h2></Reveal>
          <Reveal delay={0.1}><p className="mt-4 max-w-2xl text-lg text-steel">Four steps. Java measures and decides everything; Gemini only explains.</p></Reveal>
          <div className="mt-12 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {steps.map((s, i) => (
              <Reveal key={s.title} delay={0.1 + i * 0.1}>
                <div className="h-full rounded-3xl bg-white p-6 ring-1 ring-ink/10 transition-[box-shadow,translate] duration-300 hover:-translate-y-1 hover:shadow-[0_24px_50px_-28px_rgb(22_24_29/0.45)]">
                  <div className="flex items-center justify-between">
                    <span className="grid h-11 w-11 place-items-center rounded-2xl bg-tape/15 text-tape"><s.icon size={22} strokeWidth={2.4} /></span>
                    <span className="font-display text-4xl font-[800] text-ink/10">{i + 1}</span>
                  </div>
                  <h3 className="mt-5 text-lg font-bold">{s.title}</h3>
                  <p className="mt-2 text-[15px] leading-7 text-steel">{s.text}</p>
                </div>
              </Reveal>
            ))}
          </div>
        </section>

        <section className="border-y border-ink/10 bg-white/60">
          <div className="mx-auto grid max-w-7xl grid-cols-2 gap-8 px-5 py-14 sm:px-8 lg:grid-cols-4">
            {[
              { to: 30, suffix: '', label: 'frames a second, tracked in Java' },
              { to: 55, suffix: '', label: 'automated tests passing' },
              { to: 4, suffix: '', label: 'numbers, each with its formula' },
              { to: 80, suffix: '%', label: 'tracked, or it refuses to score' },
            ].map(n => (
              <div key={n.label}>
                <div className="font-display text-5xl font-[800] tabular-nums text-ink">
                  <CountUp to={n.to} duration={1.6} suffix={n.suffix} trigger="view" />
                </div>
                <p className="mt-2 text-sm text-steel">{n.label}</p>
              </div>
            ))}
          </div>
        </section>

        <section id="measures" className="mx-auto max-w-7xl px-5 py-24 sm:px-8">
          <Reveal><h2 className="font-display text-3xl font-[760] leading-none sm:text-5xl">What it measures</h2></Reveal>
          <div className="mt-12 grid gap-5 md:grid-cols-2">
            {measures.map((m, i) => (
              <Reveal key={m.name} delay={i * 0.08}>
                <div className="flex h-full gap-5 rounded-3xl bg-white p-6 ring-1 ring-ink/10">
                  <Activity className="mt-1 shrink-0 text-tape" size={22} />
                  <div>
                    <h3 className="text-lg font-bold">{m.name}</h3>
                    <p className="mt-1 text-[15px] leading-7 text-steel">{m.formula}</p>
                    <p className="mt-2 text-sm font-semibold text-ink">{m.better}</p>
                  </div>
                </div>
              </Reveal>
            ))}
          </div>
        </section>

        <section id="honest" className="mx-auto max-w-7xl px-5 pb-24 sm:px-8">
          <div className="grid gap-8 rounded-[32px] bg-ink p-8 text-white sm:p-12 lg:grid-cols-2">
            <div>
              <ShieldCheck className="text-tape" size={30} />
              <h2 className="mt-4 font-display text-3xl font-[760] leading-tight sm:text-4xl">It won't give you a number it can't back up.</h2>
              <p className="mt-4 text-white/70">If tracking drops or you don't finish, the attempt isn't scored, and you see why. Practice plus measured evidence: not surgical training, not a grade, not a diagnosis.</p>
            </div>
            <ul className="space-y-4 text-white/85">
              <li><b className="text-white">Gemini explains, Java decides.</b> The model never sets a score or a pass.</li>
              <li><b className="text-white">Any two fingers.</b> The model invents points for missing fingers, so you choose yours.</li>
              <li><b className="text-white">Stays on the laptop.</b> History is one local file. Gemini gets numbers, plus one frame for the setup check.</li>
              <li><b className="text-white">Honest limits.</b> A webcam sees in 2D, and it isn't validated with medical students yet.</li>
            </ul>
          </div>
        </section>

        <footer className="border-t border-ink/10 py-10 text-center text-sm text-steel">
          DEXTRA · built at HackTrent 2026 by Sid and Trini · <a href="https://github.com/sidharthnair7/Dextra" className="underline underline-offset-4">GitHub</a>
        </footer>
      </main>
    </>
  )
}
