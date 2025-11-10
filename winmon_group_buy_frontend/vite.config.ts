import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  base: '/group-buy-dev',
  plugins: [react()],
  server: {
    host: true,
    allowedHosts: [
      'winmon.co'
    ]
  }
})
