// src/config/index.ts

const config = {
    liffId: import.meta.env.VITE_LIFF_ID as string,
    basePath: import.meta.env.VITE_APP_BASE_PATH as string,
    apiBasePath: `${import.meta.env.VITE_APP_BASE_PATH}/api`, // API 基礎路徑
};

if (!config.liffId) {
    console.error('VITE_LIFF_ID is not set in .env file');
}

export default config;