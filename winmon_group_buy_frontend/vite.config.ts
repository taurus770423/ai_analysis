import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  base: '/group-buy-dev',
  plugins: [react()],
  server: {
    host: true,
    port: 5173,
    allowedHosts: [
      'winmon.co'
    ],
    hmr: {
      port: 443,         // Nginx 正在監聽的 SSL port
      protocol: 'wss'    // 使用 Secure WebSocket
    }
  }
})
