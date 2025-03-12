import { cloneDeep, isEqual } from "lodash"

type Target<T> = T | Promise<T> | (()=>Promise<T>)
/**
 * 扩展reactive功能
 */
export function useReactive<T extends object>(target: Target<T>) {
  let _promise : Promise<T> = null
  if (target instanceof Function) {
    _promise = target()
  } else if (target instanceof Promise) {
    _promise = target
  } else {
    _promise = Promise.resolve(target)
  }
  const ready = ref(false)
  //初始值
  const initialObj = {}
  //响应式
  const data = reactive({} as T)
  _promise.then(value=>{
    if (value) {
      Object.assign(initialObj, cloneDeep(value))
      Object.assign(data, value)
      ready.value = true
    }
  })
  const hasChanged = computed(()=>{
    return !isEqual(data, initialObj)
  })
  function reset() {
    for (const key in data) {
      delete data[key]
    }
    Object.assign(data, cloneDeep(initialObj))
  }
  return {
    data,
    reset,
    hasChanged,
    ready,
  }
}