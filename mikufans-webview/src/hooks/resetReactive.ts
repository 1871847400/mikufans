import { cloneDeep } from "lodash"
import { Reactive } from "vue"

/**
 * 可以重置回默认对象，多余的属性会被删除
 * @param target 默认对象
 */
export function resetReactive<T extends object>(target: T) : [Reactive<T>, ()=>void] {
  const initialObj = cloneDeep(target)
  const data = reactive(target)
  function reset() {
    for (const key in data) {
      delete data[key]
    }
    Object.assign(data, cloneDeep(initialObj))
  }
  return [ data, reset ]
}