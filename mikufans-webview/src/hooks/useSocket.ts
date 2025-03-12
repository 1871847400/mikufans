import { getRefreshToken } from '@/utils/token'
import SocketWorker from '@/workers/socket?sharedworker'

export type SocketListener = (...args: any[])=>void

//自定义发送给worker的消息体
export interface CustomSendMessage {
  type: 'connect'|'disconnect'
  namespace: string //socket.io的命名空间
  token?: string //验证身份
}

//自定义worker发给主线程的消息体
export interface CustomReceiveMessage {
  type: 'worker'|'socket' //消息类型
  event: string //事件名称
  data: any[] //传递的数据
}

/**
 * 使用SharedWorker能让多个同源页面共享内存
 *  socket.io在多页面创建连接时,新页面创建连接会让旧页面的连接失效
 *  如果使用document.visible事件动态开关,会在两个及以上的活动窗口时失效
 */
export function useSharedSocket(namespace: string) {
  // 事件名 : 监听器[]
  const handlers = new Map<string, SocketListener[]>()
  const workerName = 'worker-' + namespace.replace('/', '')
  const worker = new SocketWorker({ name: workerName })
  worker.port.start()
  worker.port.addEventListener('message', e=>{
    // logger.debug('收到', e.data);
    const message = JSON.parse(e.data) as CustomReceiveMessage
    if (message.type === 'socket') {
      const event = message.event
      const listeners = handlers.get(event)
      if (listeners?.length) {
        const msgData = message.data
        if (Array.isArray(msgData)) {
          // msgData.push('abc')
          for(const index in msgData) {
            const val = msgData[index]
            if (typeof val === 'string') {
              try {
                //可能不是json格式
                msgData[index] = JSON.parse(val)
              } catch {}
            }
          }
        }
        for (const listener of listeners) {
          listener.apply(null, msgData)
        }
      }
    }
  })
  function postMessage(data: CustomSendMessage) {
    // logger.debug('发送', data)
    worker.port.postMessage(JSON.stringify(data))
  }
  function connect() {
    const token = getRefreshToken()
    if (token) {
      postMessage({
        type: 'connect',
        namespace,
        token,
      })
    }
  }
  function disconnect() {
    postMessage({
      type: 'disconnect',
      namespace,
    })
    worker.port.close()
  }
  function useSocketEvent(ev: string, listener: (...args)=>void) {
    //在组件则执行onMounted(fn),如果不在直接执行fn
    tryOnMounted(()=>{
      if (handlers.has(ev)) {
        handlers.get(ev).push(listener)
      } else {
        handlers.set(ev, [listener])
      }
    })
    //如果不在组件内,则什么都不做
    tryOnBeforeUnmount(()=>{
      const listeners = handlers.get(ev)
      if (listeners) {
        const index = listeners.findIndex(a=>a===listener)
        listeners.splice(index, 1)
        if (listeners.length === 0) {
          handlers.delete(ev)
        }
      }
    })
  }
  connect()
  window.addEventListener('beforeunload', disconnect)
  return {
    connect,
    disconnect,
    useSocketEvent,
  }
}