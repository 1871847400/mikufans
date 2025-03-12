import legacy from '@vitejs/plugin-legacy'

//兼容低版本手机浏览器
export default legacy({
  targets: ['defaults', 'not IE 11'],
})