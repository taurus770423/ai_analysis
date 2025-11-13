// src/constants/constants.ts

// 您的 LIFF ID (用於 LIFF 環境)
export const LIFF_ID = '2008445940-BVmnKq78';

// 您的 Line Login Channel ID (用於電腦版網頁登入)
export const LINE_LOGIN_CLIENT_ID = '2008445940';

// 您的應用程式 URL (Nginx 轉發前的)
export const APP_BASE_URL = 'https://winmon.co/group-buy-dev';

// 您的後端 API URL
export const API_BASE_URL = 'https://winmon.co/group-buy-dev/api';

// 電腦版登入的 Callback URL (必須與 Line Developers 後台設定一致)
export const LINE_LOGIN_CALLBACK_URL = `${APP_BASE_URL}/auth/callback`;