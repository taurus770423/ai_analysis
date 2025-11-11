import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // 載入 .env 檔案
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react()],

    // 1. 設定基礎路徑 (對應 Nginx location /group-buy-dev/)
    base: env.VITE_APP_BASE_PATH || '/',

    server: {
      port: 5173, // 確保與 Nginx proxy_pass 的 5173 埠一致

      // --- 修正開始 ---

      // 2. 允許來自 Nginx 代理的主機名稱
      // 告訴 Vite "winmon.co" 是一個合法的主機
      allowedHosts: ['winmon.co'],

      // 3. 監聽所有 IP 位址 (包括 192.168.0.15)
      // 這樣 Nginx 才能成功 proxy_pass 到它
      host: '0.0.0.0', // 或者 host: true

      // --- 修正結束 ---

      // 4. 設定開發環境的 API 代理 (模擬 Nginx)
      proxy: {
        [`^${env.VITE_APP_BASE_PATH}/api`]: {
          target: 'http://192.168.0.15:8080', // 您的 Spring Boot 後端
          changeOrigin: true,
          rewrite: (path) => path.replace(/\/api/, ''),
        },
      },
      hmr: {
        host: 'winmon.co',   // 告訴 HMR client 連接到 Nginx 的主機
        protocol: 'wss',     // 使用 WSS (WebSocket Secure)
        clientPort: 443,     // Nginx 正在監聽的 HTTPS 埠 (預設 443)
      }
    },
  };
});