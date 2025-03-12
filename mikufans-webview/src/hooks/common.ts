/**
 * 返回一个不会出现重复元素的Ref数组
 */
export function useUniqueArray<T>(initialState: T[], compare: (a: T, b: T)=>boolean) {
  const result = ref(initialState)
  watchEffect(()=>{
    let length = result.value.length
    for (let i = 0; i < length - 1; i++) {
      for (let j = i + 1; j < length; j++) {
        if (compare(result.value[i] as T, result.value[j] as T)) {
          result.value.splice(j, 1)
          length--
          j--
        }
      }
    }
  }, { 
    flush: 'pre' //DOM更新前执行(默认)
  })
  return result
}