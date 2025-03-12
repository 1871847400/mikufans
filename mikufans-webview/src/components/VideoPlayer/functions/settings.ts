export function useDefaultSettings(videoRef: Ref<HTMLVideoElement>) {
  const { videoSettings } = useVideoStore()
  const { video, videoPart } = toRefs(useVideoStore())
  const router = useRouter()
  watchImmediate(videoRef, (video)=>{
    if (video) {
      video.volume = videoSettings.volume
      video.muted = videoSettings.muted
      video.playbackRate = videoSettings.playbackRate
    }
  })
  useEventListener(videoRef, 'volumechange', ()=>{
    videoSettings.volume = videoRef.value.volume
    videoSettings.muted = videoRef.value.muted
  })
  useEventListener(videoRef, 'ratechange', ()=>{
    videoSettings.playbackRate = videoRef.value.playbackRate
  })
  //自动播放下一集
  useEventListener(videoRef, 'ended', ()=>{
    if (videoSettings.autoPlayNext && video.value) {
      const index = video.value.parts.findIndex(a=>a.id===videoPart.value?.id)
      if (index !== -1) {
        for (const part of video.value.parts.slice(index + 1)) {
          if (part.canplay) {
            router.push(part.uri)
            break
          }
        }
      }
    }
  })
}