import Home from './screens/Home'
import Train from './screens/Train'
import Clinic from './screens/Clinic'

/** Three pages, picked by path. Each page renders the shared shrinking nav itself. */
export default function App() {
  const path = window.location.pathname.replace(/\/+$/, '') || '/'
  if (path === '/train') return <Train />
  if (path === '/clinic') return <Clinic />
  return <Home />
}
