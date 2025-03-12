import { WatchSource, watch } from 'vue';

type Callback<T, R> = (value: T, oldValue: T)=>Promise<R>
export function useWatch<T, R>(source: WatchSource<T>, callback: Callback<T, R>) {
  const data = shallowRef<R>(null)
  const loading = ref(true)
  const error = ref(false)
  const history = shallowRef({})
  const stop = watch(source, async (value, oldValue)=>{
    try {
      error.value = false
      if (history.value[value+'']) {
        data.value = history.value[value+'']
        return
      }
      loading.value = true
      const res = await callback(value, oldValue)
      history.value[value+''] = res
      data.value = res
    } catch {
      error.value = true
    } finally {
      loading.value = false
    }
  }, { immediate: true })
  return { data, loading, error, history, stop }
}