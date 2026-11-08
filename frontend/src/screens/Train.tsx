import SiteNav from '../components/SiteNav'
import { navItems } from '../nav'

/** The 3D trainer (served by the Java app), full screen under the compact nav. */
export default function Train() {
  return (
    <>
      <SiteNav items={navItems} activeHref="/train" compact />
      <main className="h-[100svh] w-full bg-paper pt-[4.5rem]">
        <iframe
          src="/trainer"
          title="DEXTRA 3D trainer"
          allow="fullscreen"
          className="block h-full w-full border-0"
        />
      </main>
    </>
  )
}
