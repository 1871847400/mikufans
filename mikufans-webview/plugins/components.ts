import path from 'path'
import Components from 'unplugin-vue-components/vite'

/**
 * 自动全局导入VUE组件，并生成类型声明文件
 */
export default Components({
  // relative paths to the directory to search for components.
  dirs: [
    path.resolve(process.cwd(), 'src/components'),
  ],

  // valid file extensions for components.
  extensions: ['vue'],

  // Glob patterns to match file names to be detected as components.
  // When specified, the `dirs`, `extensions`, and `directoryAsNamespace` options will be ignored.
  // If you want to exclude components being registered, use negative globs with leading `!`.
  // globs: ['src/components/*.{vue}'],
  globs: ['src/components/**/index.vue'], //只导入index.vue,其它属于配件无需全局导入

  // search for subdirectories
  deep: true,

  // resolvers for custom components
  // resolvers: [],

  // generate `components.d.ts` global declarations,
  // also accepts a path for custom filename
  // default: `true` if package typescript is installed
  // dts: path.resolve(process.cwd(), '/src/types/components.d.ts'),
  dts: path.resolve(process.cwd(), 'src/types/components.d.ts'),

  // Allow subdirectories as namespace prefix for components.
  directoryAsNamespace: false,

  // Collapse same prefixes (camel-sensitive) of folders and components
  // to prevent duplication inside namespaced component name.
  // works when `directoryAsNamespace: true`
  collapseSamePrefixes: false,

  // Subdirectory paths for ignoring namespace prefixes.
  // works when `directoryAsNamespace: true`
  globalNamespaces: [],

  // auto import for directives
  // default: `true` for Vue 3, `false` for Vue 2
  directives: true,

  // Transform path before resolving
  importPathTransform: v => v,

  // Allow for components to override other components with the same name
  allowOverrides: false,
  
  include: [/\.vue$/, /\.vue\?vue/],

  exclude: [/[\\/]node_modules[\\/]/, /[\\/]\.git[\\/]/, /[\\/]\.nuxt[\\/]/],
})