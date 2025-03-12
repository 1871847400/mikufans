/**
 * 用于监听元素大小尺寸变化
 */

import { Directive } from "vue"

//weakMap防止元素被删除后内存泄漏
const map = new WeakMap()
const ob = new ResizeObserver((entries)=>{
  for (const entry of entries) {
    const handler = map.get(entry.target)
    if (typeof handler === 'function') {
      handler({
        width: entry.borderBoxSize[0].inlineSize,
        height: entry.borderBoxSize[0].blockSize,
        el: entry.target,
      })
    }
  }
})
export default {
  mounted(el, binding) {
    map.set(el, binding.value)
    ob.observe(el)
  },
  unmounted(el) {
    ob.unobserve(el)
  }
} as Directive