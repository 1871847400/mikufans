import { isNumber } from "lodash";
import { MaybeRef } from "vue";

/**
 * @param el 目标元素,不能预先设置maxHeight和overflow
 * @param height 最大高度,超过后隐藏
 * @returns 
 */
export function useOverflow(el: MaybeRef<HTMLElement>, height: number | string) {
  //目标元素是否有溢出
  const overflow = ref(false)
  //是否展开
  const expand = ref(false)
  let maxHeight = 0
  function toggle(value: boolean) {
    const _el = unref(el)
    if (value) {
      _el.style.maxHeight = ''
      _el.style.overflow = ''
    } else {
      _el.style.maxHeight = isNumber(height) ? height + 'px' : height
      _el.style.overflow = 'hidden'
      requestAnimationFrame(()=>{
        const str = getComputedStyle(_el).maxHeight
          .replace('px', '')
        if (str) {
          maxHeight = Number.parseFloat(str)
        }
      })
    }
  }
  watch(expand,toggle)
  function calcOverflow() {
    const _el = unref(el)
    // console.log(_el.scrollHeight, maxHeight);
    overflow.value = _el && _el.scrollHeight - maxHeight > 0.1
  }
  //组件挂载后计算一次
  tryOnMounted(()=>{
    toggle(false)
    calcOverflow()
  })
  //元素大小变化后重新计算
  useResizeObserver(el, calcOverflow)
  return { overflow: readonly(overflow), expand }
}