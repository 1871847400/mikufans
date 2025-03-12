import { Directive } from "vue"

/**
 * 控制元素是否显示 visiblity
 */
export default {
  mounted(el: HTMLElement, binding) {
    el.style.visibility = binding.value ? 'visible' : 'hidden'
  },
  updated(el, binding) {
    el.style.visibility = binding.value ? 'visible' : 'hidden'
  }
} as Directive