import { Directive, DirectiveBinding } from "vue"

/**
 * 监听元素的子树变化
 */
const map = new WeakMap()
const ob = new MutationObserver((entries, observer)=>{
  for (const entry of entries) {
    const handler = map.get(entry)
    if (handler) {
      handler(entry as MutationRecord)
    }
  }
})
export default {
  mounted(el, binding: DirectiveBinding) {
    map.set(el, binding.value)
    ob.observe(el, {
      childList: true, //观察目标子节点的变化，是否有添加或者删除
      attributes: true, // 观察属性变动
      subtree: true, // 观察后代节点，默认为 false
      characterData: true,
    })
  },
  unmounted(el) {
    map.delete(el)
  }
} as Directive