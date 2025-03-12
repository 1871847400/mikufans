import { defineStore } from "pinia";
import Hls from 'hls.js';
import { useLike } from "@/hooks/useLike";

export const useVideoStore = defineStore('video', ()=>{
  const hls = shallowRef<Hls>(null)
  const playerElement = ref<HTMLElement>(null)
  const videoElement = ref<HTMLVideoElement>(null)
  //当前正在观看的视频
  const video = ref<Video>(null)
  //分P
  const partNumber = ref(1)
  //当前正在播放的分集
  const videoPart = computed<VideoPart>(()=>{
    return video.value?.parts?.find(a=>a.sort===partNumber.value)
  })
  //已加载弹幕
  const danmus = reactive<VideoDanmu[]>([])
  //分集展示模式
  const listType = ref(true)
  //分集显示顺序反转
  const reverse = ref(false)
  //一键三连配置
  const triple = reactive({
    progress: 0, //转动的进度
    trigger: createEventHook(), //触发事件
    color_dark: '#f6789c', //波纹颜色
    color_light: '#f6acc1',
  })
  //视频播放设置
  const videoSettings = useLocalStorage('video-settings', {
    volume: 1, //视频声音
    muted: false, //是否静音
    playbackRate: 1, //播放倍数
    autoPlayNext: true, //自动连播
  })
  const { like, dislike } = useLike(toRef(()=>video.value?.likeStatus))
  return {
    hls,
    playerElement,
    videoElement,
    partNumber,
    video,
    videoPart,
    videoSettings,
    triple,
    like,
    dislike,
    listType,
    reverse,
    danmus,
  }
})