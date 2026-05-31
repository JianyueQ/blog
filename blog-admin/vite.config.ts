import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { ConfigEnv, UserConfig, loadEnv } from 'vite'
import AutoImport from 'unplugin-auto-import/vite'
import { compression } from 'vite-plugin-compression2'
import { svgBuilder } from './src/plugins/svgBuilder'

export default defineConfig(({ command, mode }: ConfigEnv): UserConfig => {
  // 获取环境变量
  const env = loadEnv(mode, process.cwd())
  const isBuild = command === 'build'
  
  return {
    base: '/admin/',
    css: {
      preprocessorOptions: {
        scss: {
          charset: false
        },
      },
    },
    plugins: [
      vue(),
      svgBuilder('./src/icons/svg/'),
      AutoImport({
        imports: [
          'vue',
          'vue-router',
          'pinia'
        ],
        dts: 'src/auto-imports.d.ts',
        // 可以选择是否自动导入 Vue 的组合式 API
        vueTemplate: true,
        // 自动导入目录下的模块
        dirs: [
          './src/composables',
          './src/stores'
        ],
      }),
      // 生产环境开启 gzip 压缩
      isBuild && compression({
        include: /\.(js|css|html|json|svg)$/i,
        threshold: 1024,
        deleteOriginalAssets: false,
      }),
    ],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    // 构建优化
    build: {
      // 生产环境关闭 sourcemap
      sourcemap: false,
      // 使用 terser 压缩，去除 console 和 debugger
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: true,
          drop_debugger: true,
        },
      },
      // 代码分割策略
      rollupOptions: {
        output: {
          manualChunks: {
            'vue-vendor': ['vue', 'vue-router', 'pinia'],
            'element-plus': ['element-plus', '@element-plus/icons-vue'],
            'echarts': ['echarts'],
            'editor': ['@wangeditor/editor-for-vue'],
          },
        },
      },
      // chunk 大小警告
      chunkSizeWarningLimit: 1000,
    },
    server: {
      host: '0.0.0.0',
      port: Number(env.VITE_APP_PORT) || 3000,
      open: false,
      proxy: {
        '/api': {
          target: env.VITE_APP_API_URL,
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/api/, ''),
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              console.log('代理请求:', {
                target: options.target,
                path: req.url
              })
            })
          }
        }
      }
    }
  }
})
