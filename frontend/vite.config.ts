import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    proxy: {
      '^/(api|auth|unit|sys)': {
        target: 'http://localhost:8080', // 后端 Spring Boot 地址
        changeOrigin: true,
        rewrite: (path) => path // 保持前缀
      }
    }
  }
})
