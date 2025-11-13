import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // 載入 .env 檔案
  const env = loadEnv(mode, process.cwd(), '');

  return {
    plugins: [react()],
    base: env.VITE_APP_BASE_PATH || '/',
    server: {
      port: 5173, // 確保與 Nginx proxy_pass 的 5173 埠一致
      allowedHosts: ['winmon.co'],
      host: '0.0.0.0', // 或者 host: true

      // 4. 設定開發環境的 API 代理 (模擬 Nginx)
      proxy: {
        [`^${env.VITE_APP_BASE_PATH}/api`]: {
          target: 'http://192.168.0.15:8080', // 您的 Spring Boot 後端
          changeOrigin: true,
          rewrite: (path) => path.replace(/\/api/, ''),
        },
      },
      hmr: {
        host: 'winmon.co',
        protocol: 'wss',
      },
    },
  };
});