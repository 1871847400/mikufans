import { onBeforeUnmount, onMounted } from 'vue'

export function useDocumentEvent(type: keyof DocumentEventMap, listener: (this: Document, ev: Event | any) => any) {
  onMounted(() => {
    document.addEventListener(type, listener)
  })
  onBeforeUnmount(() => {
    document.removeEventListener(type, listener)
  })
}

export function useWindowEvent<K extends keyof WindowEventMap>(type: K, listener: (this: Window, ev: WindowEventMap[K]) => any) {
  onMounted(() => {
    window.addEventListener(type, listener)
  })
  onBeforeUnmount(() => {
    window.removeEventListener(type, listener)
  })
}

export function useIntervalTask(interval: number, handler: Function) {
  var id : number
  onMounted(() => {
    id = setInterval(handler, interval)
  })
  onBeforeUnmount(() => {
    clearInterval(id)
  })
}

/**
 * 当""页面文档元素""滚动到底部时触发,注意会重复触发
 */
export function useDocumentScrollEndEvent(handler: ()=>void) {
  useDocumentEvent('scroll', (_) => {
    // 滚动的距离
    let scrollTop = document.documentElement.scrollTop
    // 视窗的高度
    let clientHeight = document.documentElement.clientHeight
    // 可滚动的最大高度
    let scrollHeight = document.documentElement.scrollHeight
    // console.log(scrollTop,clientHeight,scrollHeight);
    // 滚动到底部时触发
    if (scrollTop + clientHeight + 1 >= scrollHeight) {
      handler()
    }
  })
}

export function useDocumentKeyupEvent(listener: (ev: KeyboardEvent) => void) {
  onMounted(() => {
    document.addEventListener('keyup', listener)
  })
  onBeforeUnmount(() => {
    document.removeEventListener('keyup', listener)
  })
}

/**
 * 当元素滚动到底部时触发
 * 注意设置loading防止无限触发
 */
export function addScrollEndEvent(ele: Element, handler: ()=>void) {
  ele.addEventListener('scroll', ()=>{
    let scrollTop = ele.scrollTop
    let clientHeight = ele.clientHeight
    let scrollHeight = ele.scrollHeight
    if (scrollTop + clientHeight + 1 >= scrollHeight) {
      handler()
    }
  })
}