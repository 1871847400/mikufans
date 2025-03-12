export function useAsyncFn<P extends any[], R>(fn: (...args: P)=>Promise<R | undefined>) {
  const loading = ref(false)
  return function(...args: P) {
    if (loading.value) {
      return
    }
    try {
      loading.value = true
      return fn(...args)
    } finally {
      loading.value = false
    }
  }
}