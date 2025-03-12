/**
 * 处理长按向右键开启倍数播放
 */
export function useLongPress(active: Ref<boolean>, videoElement: Ref<HTMLVideoElement>) {
  const timestamp = useTimestamp({})
  const presstime = ref(0)
  const { ArrowRight } = useMagicKeys()
  const longtime = computed(()=>presstime.value>0&&Math.abs(timestamp.value - presstime.value)>250)
  useEventListener('keydown', e=>{
    if (active.value && e.code == 'ArrowRight') {
      if (presstime.value == 0) {
        presstime.value = timestamp.value
      }
    }
  })
  //按键抬起来才触发一次
  useEventListener('keyup', e=>{
    if (active.value) {
      switch(e.code) {
        case 'ArrowRight':
          //短按才快进时长
          if (!longtime.value) {
            videoElement.value.currentTime += 5;
          }
          presstime.value = 0
          break
      }
    }
  })
  let playbackRate = 1
  const longpress = computed(()=>longtime.value && ArrowRight.value)
  watch(longpress, (newVal, oldVal)=>{
    if (newVal) {
      playbackRate = videoElement.value.playbackRate
      videoElement.value.playbackRate = playbackRate * 3
      if (videoElement.value.paused) {
        videoElement.value.play()
      }
    } else {
      videoElement.value.playbackRate = playbackRate
    }
  })
  return longpress
}