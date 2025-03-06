import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  server: {
    cors: true, // 추가
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 백엔드 주소
        changeOrigin: true,
        secure: false,
        rewrite: (path) => {return path.replace(/^\/api/, '')} // 추가
      }
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
