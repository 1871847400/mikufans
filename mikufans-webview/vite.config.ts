import { defineConfig, loadEnv, UserConfig } from 'vite'
import plugins from './plugins'
import tailwindcss from  'tailwindcss'
import autoprefixer from 'autoprefixer'
import dts from "vite-plugin-dts";
import { nodeResolve } from '@rollup/plugin-node-resolve';
import path from 'path';

console.log('当前文件目录:', __dirname);
console.log('当前进程目录:', process.cwd())

// https://vitejs.dev/config/
export default defineConfig((config)=>{
  console.log('config:', config);
  const env = loadEnv(config.mode, './env')
  console.log('env:', env)
  const options : UserConfig = {
    // root: path.resolve(__dirname, 'src'),
    plugins,
    envDir: './env',
    resolve: {
      alias: {
        '~': path.resolve(process.cwd(), ''),
        '@': path.resolve(process.cwd(), 'src'),
      },
      extensions: ['.mjs', '.js', '.mts', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    css: {
      //在开发过程中是否启用 sourcemap。
      devSourcemap: true,
      preprocessorOptions: {
        scss: {
          //全局导入scss变量
          //注意不要导入定义class的文件会无法生效
          additionalData: '@import "@/assets/styles/variables.scss";',
        },
      },
      postcss: {
        plugins: [
          tailwindcss,
          autoprefixer({
            overrideBrowserslist: [
              'Android 4.1',
              'iOS 7.1',
              'Chrome > 31',
              'ff > 31',
              'ie >= 8',
              '> 1%',
            ],
          }),
        ],
      }
    },
    server: {
      host: '0.0.0.0',//开放局域网内访问
      proxy: {
        '/dev-api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/dev-api/, ''),
        },
        '/dev-socket': {
          target: 'http://localhost:8188',
          rewrite: (path) => path.replace(/^\/dev-socket/, '/socket.io'),
          changeOrigin: true,
          secure: false,
          ws: true,
        },
        '/api': {
          target: 'http://127.0.0.1',
          changeOrigin: true,
        },
        '/socket.io': {
          target: 'http://127.0.0.1',
          changeOrigin: true,
          secure: false,
          ws: true,
        },
      }
    }
  }
  if (config.mode === 'lib') {
    fillLibConfig(options)
  }
  return options
})

//库模式下打包添加以下配置
function fillLibConfig(options: UserConfig) {
  options.plugins.push(
    dts({
      entryRoot: "./src",
      outDir: ['dist-lib'],
      insertTypesEntry: true, //生成index.d.ts
      tsconfigPath: "tsconfig.json",
    }),
  )
  options.build = {
    minify: false, //代码压缩
    // sourcemap: true, //打包源代码
    target: 'esnext',
    lib: {
      entry: [
        './lib/main.ts',
        './lib/utils.ts',
        './lib/apis.ts'
      ],
      name: 'mikufans',
      formats: ['es'], //只打包ESmodule
    },
    rollupOptions: {
      plugins: [
        nodeResolve, 
      ],
      // 确保外部化处理那些你不想打包进库的依赖
      external: ['vue', 'element-plus', /node_modules/],
      // input: ['/src/utils/*'],
      output: {
        //所有生成的 chunk 被放置在哪个目录中
        dir: 'dist-lib',
        //生成的 bundle 的格式
        format: 'es',
        //自定义分包名称
        manualChunks(id, meta) {
          // console.log(id, meta);
          if (id.includes('/node_modules/')) {
            return 'vendor'
          }
        },
      },
    },
  }
}