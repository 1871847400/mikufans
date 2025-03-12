type Listener = (e: KeyboardEvent)=>void
type Options = AddEventListenerOptions & {
  oncePerPress?: boolean //每次按下最多触发一次
}

/**
 * 当按下某个键时触发(会持续触发)
 */
export function useKeyDownEvent(listener: Listener, options?: Options) {
  const keyPress = new Map
  function keydown(e: KeyboardEvent) {
    if (options?.oncePerPress && keyPress.get(e.code)) {
      return
    }
    keyPress.set(e.code, true)
    listener(e)
  }
  function keyup(e: KeyboardEvent) {
    keyPress.set(e.code, false)
  }
  onMounted(()=>{
    // keydown优先于keypress执行
    window.addEventListener('keydown', keydown, options)
    if (options?.oncePerPress) {
      window.addEventListener('keyup', keyup, true)
    }
  })
  onUnmounted(()=>{
    window.removeEventListener('keydown', keydown, options)
    window.removeEventListener('keyup', keyup, true)
  })
}