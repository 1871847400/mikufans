import mitt from 'mitt'

type Events = {
  userFollow: { userId: string, follow: UserFollowStatus },
}

//全局事件处理器
const globalEventEmitter = mitt<Events>()

export function useGlobalEvent<K extends keyof Events>(ev: K, listener: (event: Events[K])=>void) {
  tryOnMounted(()=>{
    globalEventEmitter.on(ev, listener)
  })
  tryOnBeforeUnmount(()=>{
    globalEventEmitter.off(ev, listener)
  })
}

export function emitGlobalEvent<K extends keyof Events>(ev: K, data: Events[K]) {
  globalEventEmitter.emit(ev, data)
}