/**
 * eventBus.on如果在组件中直接使用,会造成重复注册
 * 该方法会在组件销毁时取消注册
 */
export function createEventEmitter<Events extends Record<string | symbol, unknown>>() {
  const eventBus = useEventBus(Symbol())
  function useEvent<Key extends keyof Events>(name: Key, callback: (payload: Events[Key])=>void) {
    const off = eventBus.on((event, payload)=>{
      if (event === name) {
        callback(payload)
      }
    })
    tryOnBeforeUnmount(off)
  }
  function emitEvent<Key extends keyof Events>(name: Key, payload: Events[Key]) {
    eventBus.emit(name, payload)
  }
  return {
    useEvent,
    emitEvent,
  }
}