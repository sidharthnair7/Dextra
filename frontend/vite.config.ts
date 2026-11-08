import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { fileURLToPath, URL } from 'node:url'

declare const process: { env: Record<string, string | undefined> }

// The Java app (camera, scoring, Gemini) runs on 8080. The dev server forwards the API, the live socket,
// the camera preview and the 3D trainer page to it, so this site and the trainer share one origin.
const backend = process.env.DEXTRA_BACKEND || 'http://localhost:8080'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': { target: backend, changeOrigin: true },
      '/ws': { target: backend.replace(/^http/, 'ws'), ws: true, changeOrigin: true },
      '/trainer': { target: backend, changeOrigin: true, rewrite: () => '/' },
    },
  },
})
