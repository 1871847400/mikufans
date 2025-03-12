<template>
  <template v-if="!vid">
    <thumb-tab v-model="type" :options="options" @beforechange="beforeChange" style="gap: 32px; padding: 0 64px; height: 60px;" />
  </template>
  <div v-else class="font-bold text-lg py-4 px-16">编辑视频</div>
  <el-divider class="m-0"></el-divider>
  <div v-if="uploadStore.uploadSuccess" class="flex flex-col items-center mt-4">
    <img class="w-2/5" :src="SuccessImage"/>
    <p class="text-3xl" style="color:#ffb600">{{ vid ? '编辑成功！' : '恭喜您上传视频成功！' }}</p>
    <div class="buttons">
      <el-button @click="listUpload">查看稿件</el-button>
      <el-button type="primary" @click="reUpload">再投一个</el-button>
    </div>
  </div>
  <template v-else-if="!selected">
    <upload-zone @select="onSelect"/>
  </template>
  <Async v-else :loading="loading" :error="error" min-h="400px">
    <video-index />
  </Async>
</template>

<script setup lang="ts">
import UploadZone from './zone/UploadZone.vue'
import SuccessImage from '@/assets/images/miku-4.png'
import VideoIndex from './section/index.vue'
import videoApi from '@/apis/video';
import { v4 as uuidv4 } from 'uuid';
const route = useRoute()
const router = useRouter()
const uploadStore = useUploadStore()
const type = useRouteQuery<VideoType>('type', 'VIDEO', { mode: 'push' })
const vid = useRouteQuery<string>('vid', '')
const options = [
  { label: '视频投稿', value: 'VIDEO' },
  { label: '番剧投稿', value: 'ANIME' },
  { label: '电影投稿', value: 'MOVIE' },
]
//刷新网页前需要再次确认
useEventListener('beforeunload', e=>{
  if (import.meta.env.PROD) {
    e.preventDefault()
  }
})
function beforeChange(e: MouseEvent) {
  if (uploadStore.hasChanged) {
    if (!confirm('不会保存当前页面已提交的信息！')) {
      e.preventDefault()
    }
  }
}
//选择文件后才显示其它填写的参数
const selected = ref<boolean>(!!vid.value)
function onSelect(files: File[]) {
  selected.value = true
  files.forEach(f=>uploadStore.uploadFile(f))
}
//去到稿件管理
function listUpload() {
  uploadStore.reset()
  router.push('/creator/content/video')
}
function reUpload() {
  uploadStore.reset()
  vid.value = ''
  uploadStore.videoType = type.value
}
watchImmediate(type, ()=>{
  uploadStore.reset()
  uploadStore.videoType = type.value
})
const loading = ref(false)
const error = ref(null)
watchImmediate(vid, async ()=>{
  uploadStore.reset()
  if (!vid.value) {
    return
  }
  try {
    loading.value = true
    const data = uploadStore.data
    const video = await videoApi.getById(vid.value)
    if (!video) {
      return router.push('/404')
    }
    uploadStore.oldData = video
    data.type = video.type 
    data.channelId = video.channelId
    data.title = video.title
    data.intro = video.intro
    data.bannerId = video.bannerId
    data.mBannerId = video.mBannerId
    data.republish = video.republish
    data.sourceUrl = video.sourceUrl
    data.userLevel = video.userLevel
    data.tags = video.tags
    data.userLevel = video.userLevel
    const dynamic = video.dynamic
    data.dynamic = {
      id: dynamic.id,
      publishFlag: dynamic.publishFlag,
      publishTime: dynamic.publishTime,
      visible: dynamic.visible,
      commentArea: {
        id: dynamic.commentArea.id,
        commentFlag: dynamic.commentArea.commentFlag,
        userLevel: dynamic.commentArea.userLevel
      }
    }
    for (const part of video.parts) {
      const uuid = uuidv4()
      uploadStore.partList.push({
        uuid,
        subtitles: part.subtitles.map(a=>({...a,  data: ''})),
        duration: part.resource.duration,
        partName: part.partName,
        segments: [],
        fileInfo: {
          file: null,
          hash: part.resource.md5,
          loaded: 0,
          speed: 0,
          status: 'success',
          total: part.resource.fileSize,
        },
        oldData: part,
      })
    }
    const bangumi = video.bangumi
    if (bangumi) {
      data.bangumi.id = bangumi.id
      data.bangumi.posterId = bangumi.posterId
      data.bangumi.premiere = bangumi.premiere
      data.bangumi.regionId = bangumi.regionId
      data.bangumi.releaseSeason = bangumi.releaseSeason
      data.bangumi.releaseStatus = bangumi.releaseStatus
      data.bangumi.releaseTime = bangumi.releaseTime
      data.bangumi.releaseWeek = bangumi.releaseWeek
      data.bangumi.seriesIds = bangumi.seriesIds
      data.bangumi.seriesTag = bangumi.seriesTag
      data.bangumi.official = bangumi.official
      data.bangumi.staff = bangumi.staff
      data.bangumi.voice = bangumi.voice
      data.bangumi.styles = bangumi.styles.map(a=>a.id)
    }
  } catch(err) {
    error.value = err
    uploadStore.reset()
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.buttons {
  padding-top: 40px;
  .el-button {
    width: 140px;
    height: 40px
  }
}
</style>