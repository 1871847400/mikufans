import { ref } from "vue";

/**
 * 优化浏览器白屏时间
 *  判断当前执行帧数是否已经超过传入值
 * @param maxFrameCount 默认1000
 * @returns 
 */
export function useDefer(maxFrameCount = 1000) {
  //计数器,每过一个动画帧+1,直到最大值
  const frameCount = ref(0)
  const refreshFrameCount = ()=>{
    requestAnimationFrame(()=>{
      frameCount.value++
      if(frameCount.value < maxFrameCount) {
        refreshFrameCount()
      }
    })
  }
  refreshFrameCount()
  return (showInFrameCount: number) => {
    return frameCount.value >= showInFrameCount
  }
}