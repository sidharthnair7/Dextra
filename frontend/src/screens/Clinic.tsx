import { useEffect, useMemo, useRef, useState } from 'react'
import { getPeople, getProgress, type AttemptRecord, type MetricTrend, type ProgressView } from '../api'
import Reveal from '../components/Reveal'
import SiteNav from '../components/SiteNav'
import { navItems } from '../nav'

const UNIT: Record<MetricTrend['key'], string> = { accuracy: '', smoothness: '', speed: 's', coverage: '%' }
const NOTE: Record<MetricTrend['key'], string> = {
  accuracy: 'score out of 100', smoothness: 'score out of 100', speed: 'seconds per target, lower is better', coverage: 'share of frames tracked',
}
const fmt = (v: number | null, key: MetricTrend['key']) => (v == null ? '-' : key === 'speed' ? v.toFixed(1) : String(Math.round(v)))
const when = (iso: string) => { const d = new Date(iso); return isNaN(+d) ? iso : d.toLocaleString([], { month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' }) }
const cap = (s: string) => s.charAt(0).toUpperCase() + s.slice(1)

/** One series: the line in the quiet colour, the latest attempt in the accent; hover snaps to the nearest attempt. */
function Sparkline({ m, rows }: { m: MetricTrend; rows: AttemptRecord[] }) {
  const [hover, setHover] = useState<number | null>(null)
  const svgRef = useRef<SVGSVGElement>(null)
  const W = 200, H = 48, P = 6, vals = m.values
  const lo = Math.min(...vals), hi = Math.max(...vals), span = hi - lo || 1
  const x = (i: number) => (vals.length === 1 ? W / 2 : P + (i * (W - 2 * P)) / (vals.length - 1))
  const y = (v: number) => (hi === lo ? H / 2 : H - P - ((v - lo) / span) * (H - 2 * P))
  const onMove = (e: React.PointerEvent) => {
    const box = svgRef.current!.getBoundingClientRect()
    const px = ((e.clientX - box.left) / box.width) * W
    let best = 0
    vals.forEach((_, k) => { if (Math.abs(x(k) - px) < Math.abs(x(best) - px)) best = k })
    setHover(best)
  }
  return (
    <div className="relative mt-3">
      <svg ref={svgRef} viewBox={`0 0 ${W} ${H}`} className="block h-auto w-full overflow-visible" role="img"
        aria-label={`${m.label} over ${vals.length} scored attempts, oldest first: ${vals.map(v => fmt(v, m.key) + UNIT[m.key]).join(', ')}`}>
        <line x1={0} x2={W} y1={H - 0.5} y2={H - 0.5} stroke="rgb(22 24 29 / .1)" strokeWidth={1} />
        {vals.length > 1 && (
          <polyline points={vals.map((v, i) => `${x(i)},${y(v)}`).join(' ')} fill="none" stroke="#8a909b" strokeWidth={2} strokeLinejoin="round" strokeLinecap="round" />
        )}
        {hover != null && <line x1={x(hover)} x2={x(hover)} y1={0} y2={H} stroke="rgb(22 24 29 / .3)" strokeWidth={1} />}
        {vals.map((v, i) => {
          const last = i === vals.length - 1
          return <circle key={i} cx={x(i)} cy={y(v)} r={last ? 5 : 4} fill={last ? '#ff6a2b' : '#8a909b'} stroke="#ffffff" strokeWidth={2} />
        })}
        <rect x={-8} y={-8} width={W + 16} height={H + 16} fill="transparent" onPointerMove={onMove} onPointerLeave={() => setHover(null)} />
      </svg>
      {hover != null && (
        <div className="pointer-events-none absolute -top-14 rounded-lg bg-ink px-2.5 py-1.5 text-xs whitespace-nowrap text-white/70 shadow-lg"
          style={{ left: `clamp(0px, calc(${(x(hover) / W) * 100}% - 60px), calc(100% - 140px))` }}>
          <strong className="block text-sm text-white tabular-nums">{fmt(vals[hover], m.key)}{UNIT[m.key]}</strong>
          Scored attempt {hover + 1} of {vals.length}{rows[hover] ? ` · ${when(rows[hover].startedAt)}` : ''}
        </div>
      )}
    </div>
  )
}

function Tile({ m, history }: { m: MetricTrend; history: AttemptRecord[] }) {
  const rows = history.filter(r => r.scored && r[m.key] != null)
  let delta: { text: string; cls: string }
  if (m.change == null) delta = { text: m.values.length ? 'One scored attempt so far' : 'No scored attempts yet', cls: 'text-steel font-medium' }
  else {
    const verdict = m.improved === true ? 'better' : m.improved === false ? 'worse' : 'no change'
    const arrow = m.change > 0 ? '▲' : m.change < 0 ? '▼' : '■'
    delta = {
      text: `${arrow} ${m.change > 0 ? '+' : ''}${fmt(m.change, m.key)}${UNIT[m.key]} since the first attempt · ${verdict}`,
      cls: m.improved === true ? 'text-mint' : m.improved === false ? 'text-alert' : 'text-steel',
    }
  }
  return (
    <div className="rounded-3xl bg-white p-5 ring-1 ring-ink/10">
      <p className="text-xs text-steel">{m.label} · {NOTE[m.key]}</p>
      <p className="mt-1 font-display text-4xl font-[800] tabular-nums">{m.latest == null ? '-' : fmt(m.latest, m.key) + UNIT[m.key]}</p>
      <p className={`mt-1 text-xs font-semibold tabular-nums ${delta.cls}`}>{delta.text}</p>
      {m.values.length > 0 && <Sparkline m={m} rows={rows} />}
    </div>
  )
}

export default function Clinic() {
  const [names, setNames] = useState<string[]>([])
  const [person, setPerson] = useState('')
  const [view, setView] = useState<ProgressView | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    getPeople()
      .then(p => {
        const list = p.map(x => x.person)
        let saved = ''
        try { saved = localStorage.getItem('dextra.who') || '' } catch { /* storage blocked */ }
        const pick = list.find(n => n.toLowerCase() === saved.toLowerCase()) || list[0] || 'Guest'
        setNames(list.length ? list : ['Guest'])
        setPerson(pick)
      })
      .catch(() => setError('The Java app isn’t running. Start it with mvn spring-boot:run, then reload.'))
  }, [])

  useEffect(() => {
    if (!person) return
    getProgress(person).then(setView).catch(() => setError('Could not load progress.'))
  }, [person])

  const newestFirst = useMemo(() => (view ? [...view.history].reverse() : []), [view])

  return (
    <>
      <SiteNav items={navItems} activeHref="/clinic" />
      <main className="mx-auto max-w-6xl px-5 pb-24 pt-32 text-ink sm:px-8">
        <Reveal>
          <h1 className="font-display text-4xl font-[780] leading-none sm:text-6xl">Clinic mode</h1>
          <p className="mt-4 max-w-2xl text-lg text-steel">One person's practice over time, saved on this laptop. Practice evidence, not a diagnosis.</p>
        </Reveal>

        {error ? (
          <p className="mt-10 rounded-2xl bg-alert/10 p-5 text-alert">{error}</p>
        ) : (
          <>
            <div className="mt-10 flex flex-wrap items-center gap-3">
              <label htmlFor="person" className="text-sm text-steel">Person</label>
              <select id="person" value={person} onChange={e => setPerson(e.target.value)}
                className="rounded-full bg-white px-4 py-2 text-sm font-semibold ring-1 ring-ink/15 outline-none focus-visible:ring-2 focus-visible:ring-tape">
                {names.map(n => <option key={n} value={n}>{n}</option>)}
              </select>
              {view && <span className="text-sm text-steel">{view.attempts} attempt{view.attempts === 1 ? '' : 's'} · {view.scored} scored · {view.refused} not scored</span>}
            </div>

            {view && view.attempts === 0 && (
              <p className="mt-8 rounded-2xl bg-white p-6 text-steel ring-1 ring-ink/10">No attempts saved for {view.person} yet. Do a drill in the trainer and it shows up here.</p>
            )}

            {view && view.attempts > 0 && (
              <>
                <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                  {view.metrics.map(m => <Tile key={m.key} m={m} history={view.history} />)}
                </div>
                <div className="mt-8 overflow-x-auto rounded-3xl bg-white ring-1 ring-ink/10">
                  <table className="w-full text-left text-sm tabular-nums">
                    <caption className="sr-only">Every attempt, newest first</caption>
                    <thead className="text-steel">
                      <tr>{['When', 'Result', 'Accuracy', 'Smoothness', 'Speed', 'Tracked', 'Fingers'].map(h => <th key={h} className="px-4 py-3 font-semibold">{h}</th>)}</tr>
                    </thead>
                    <tbody>
                      {newestFirst.map(r => (
                        <tr key={r.id} className="border-t border-ink/10">
                          <td className="px-4 py-3 whitespace-nowrap">{when(r.startedAt)}</td>
                          <td className={`px-4 py-3 ${r.scored ? '' : 'text-steel'}`}>{r.scored ? 'Scored' : `Not scored: ${r.refusedReason || r.state}`}</td>
                          <td className="px-4 py-3">{fmt(r.accuracy, 'accuracy')}</td>
                          <td className="px-4 py-3">{fmt(r.smoothness, 'smoothness')}</td>
                          <td className="px-4 py-3">{r.speed == null ? '-' : fmt(r.speed, 'speed') + 's'}</td>
                          <td className="px-4 py-3">{r.coverage == null ? '-' : fmt(r.coverage, 'coverage') + '%'}</td>
                          <td className="px-4 py-3 whitespace-nowrap">{(r.fingers || []).map(cap).join(' + ')}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </>
            )}
          </>
        )}
      </main>
    </>
  )
}
