<template>
  <div class="video-page">
    <nav-bar/>
    <Async :loading="loading" :error="error" :empty="nodata" empty-text="没有找到视频" min-h="500px">
      <main>
        <video-page-left/>
        <video-page-right/>
      </main>
    </Async>
  </div>
</template>

<script setup lang="ts">
import VideoPageLeft from './left/index.vue';
import VideoPageRight from './right/index.vue';
import { useVideoStore } from '@/stores/video';
import videoApi from '@/apis/video';
import { isInteger, toInteger } from 'lodash';
const videoStore = useVideoStore()
const route = useRoute()
//id或sid
const sid = useRouteParams<string>('sid')
//第多少P
const part = useRouteParams<number>('part', 1, { transform: toInteger })
const share = useRouteQuery<string>('share')
const loading = ref(true)
const error = ref(null)
const nodata = ref(false)
//当检测到url的视频id变化后重新获取视频信息
watchImmediate(sid, async (newId)=>{
  if (!sid.value || sid.value==videoStore.video?.sid) {
    return
  }
  loading.value = true
  try {
    videoStore.video = null
    const video = await videoApi.getById(sid.value)
    //如果请求完成前又变更了id,则放弃结果
    if (newId !== sid.value) {
      return
    }
    if (!video || video.canplayCount === 0) {
      nodata.value = true
      return
    }
    videoStore.video = video
    document.title = video.title + ' - Mikufans'
    if (share.value) {
      videoApi.useShare(share.value)
    }
    //如果当前不是用的短id,则更换为短id
    if (video.sid != sid.value) {
      sid.value = video.sid
    }
    //寻找sort最低的能播放的视频
    if (!isInteger(route.params?.part)) {
      const first = video.parts.find(a=>a.canplay)
      if (first && first.sort !== 1) {
        part.value = first.sort
      }
    }
  } catch(err) {
    error.value = err
  } finally {
    loading.value = false
  }
})
watchImmediate(part, ()=>{
  videoStore.partNumber = part.value
})
</script>

<style scoped lang="scss">
.video-page {
  min-height: 100vh;
}
main {
  display: flex;
  overflow: hidden;
  justify-content: center;
  .video-page-left {
    /* width: 750px; */
    width: 50%;
  }
  .video-page-right {
    overflow: hidden;
    /* width: 400px; */
    width: 26%;
    padding-left: 32px;
    box-sizing: border-box;
  }
}
</style>