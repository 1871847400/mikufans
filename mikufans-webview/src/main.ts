import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'virtual:svg-icons-register' //引入svg,需要先在vite里安装插件
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue' //图标库
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/display.css' //element布局响应式
import 'element-plus/theme-chalk/dark/css-vars.css' //导入element的css变量
import axios from 'axios'
import VueAxios from 'vue-axios'
import { createPinia } from 'pinia'
import VConsole from 'vconsole' //手机浏览器上打开控制台
import { isMobile } from './utils/common'
import logger from './utils/logger'
import '@/assets/iconfont/iconfont.css'
import "tailwindcss/tailwind.css"
import _ from 'lodash'
import '@/utils/dayjs' //初始化dayjs
import { installDirectives } from './directives'
import 'nprogress/nprogress.css' //加载进度条样式
import '@/assets/styles/index.scss' //加载主样式
import '@imengyu/vue3-context-menu/lib/vue3-context-menu.css'

const app = createApp(App)
const pinia = createPinia()

//挂载日志输出方法到全局
window['logger'] = logger

if (import.meta.env.DEV) {
  if (isMobile()) {
    //手机端控制台
    new VConsole()
  }
  //将lodash方法挂载到全局
  window._ = _
}

//注册插件
app.use(ElementPlus)
app.use(router)
app.use(pinia)
//注册图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(VueAxios, axios)

//全局注册组件(现在由vite插件代替)
// app.component() 

//安装指令
installDirectives(app)

//挂载APP到容器
app.mount('#app')

export default app