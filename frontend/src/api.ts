// The Java app's API (proxied by Vite in dev). Shapes match src/main/java/com/dextra.

export interface Status { status: string; fps: number; handFound: boolean; gemini: 'online' | 'offline' }

export interface AttemptRecord {
  id: string; person: string; startedAt: string; state: string; scored: boolean; refusedReason: string | null
  accuracy: number | null; smoothness: number | null; speed: number | null; coverage: number | null
  hits: number; refusals: number; fingers: string[]
}

export interface MetricTrend {
  key: 'accuracy' | 'smoothness' | 'speed' | 'coverage'; label: string; unit: string; higherIsBetter: boolean
  values: number[]; first: number | null; latest: number | null; best: number | null; change: number | null; improved: boolean | null
}

export interface Day { day: string; attempts: number; scored: number; accuracy: number | null; smoothness: number | null; speed: number | null; coverage: number | null }

export interface ProgressView { person: string; attempts: number; scored: number; refused: number; metrics: MetricTrend[]; history: AttemptRecord[]; daily: Day[]; storage: string | null }

export interface Person { person: string; attempts: number; last: string }

async function json<T>(res: Response): Promise<T> {
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return res.json() as Promise<T>
}

export const getStatus = () => fetch('/api/status').then(r => json<Status>(r))
export const getPeople = () => fetch('/api/people').then(r => json<Person[]>(r))
export const getProgress = (person: string) =>
  fetch('/api/progress', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ person }) })
    .then(r => json<ProgressView>(r))
