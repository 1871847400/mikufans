export const useBangumiStore = defineStore('bangumi', ()=>{
  //对应视频数据
  const video = ref<Video>(null)
  return {
    video,
  }
})