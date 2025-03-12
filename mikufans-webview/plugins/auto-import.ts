import path from 'path'
import AutoImport from 'unplugin-auto-import/vite'

const vueTypes = [
  'any',
  'integer',
  'number',
  'func',
  'array',
  'bool',
  'string',
  'object',
  'oneOf',
  'oneOfType',
]
/**
 * 全局自动导入第三方的函数库和类型
 */
export default AutoImport({
  //生效的文件类型
  include: [
    /\.[tj]sx?$/, // .ts, .tsx, .js, .jsx
    /\.vue$/,
    /\.vue\?vue/, // .vue
    /\.md$/, // .md
  ],
  //自动导入的模块
  imports: [
    'vue',
    'vue-router',
    '@vueuse/core',
    'pinia',
    {
      'vue-types': vueTypes,
    },
    {
      from: 'vue-types',
      imports: vueTypes,
      type: true,
    },
    {
      '@vueuse/router': ['useRouteQuery', 'useRouteParams', 'useRouteHash'],
    }
  ],
  //自动导入的目录列表
  dirs: [
    // path.resolve(process.cwd(), 'src')
    path.resolve(process.cwd(), 'src/stores/**'),
    path.resolve(process.cwd(), 'src/utils/message'),
    path.resolve(process.cwd(), 'src/directives/**'),
  ],
  //自动生成类型声明文件
  dts: path.resolve(process.cwd(), 'src/types/auto-imports.d.ts'),
  vueTemplate: true,
  viteOptimizeDeps: true,
  vueDirectives: {
    isDirective(from, importEntry) {
      // from为文件的绝对路径,最后再排除index
      return from.includes('/directives/') && !from.endsWith('index.ts')
    },
  },
  //导入语句放末尾
  injectAtEnd: true,
  //默认导出,用文件名作为导出名
  //如果为false,将不会自动导入export default
  defaultExportByFilename: false
})