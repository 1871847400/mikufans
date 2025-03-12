/**
 * 自定义video不支持的事件
 */
export const CustomVideoEvents = {
 } as const

export function dispatchEvent<T = keyof typeof CustomVideoEvents>(videoElement: MaybeRef<HTMLVideoElement>, eventName: T) {
  const event = new Event(eventName+'', { cancelable: false })
  unref(videoElement)?.dispatchEvent(event)
}