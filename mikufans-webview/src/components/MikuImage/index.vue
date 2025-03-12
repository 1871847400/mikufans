<template>
  <!-- 使用v-if/else,不外套div,方便让外面可以传属性 -->
  <el-skeleton v-if="loading" loading :class="classList" animated>
    <template #template>
      <el-skeleton-item variant="image" class="size-full"/>
    </template>
  </el-skeleton>
  <img v-else
    :class="classList" 
    :src="src" 
    @click="onClick" 
    v-bind="imgAttrs"
    @error="onError"
    draggable="false"
  >
</template>

<script setup lang="ts">
import { imageUrl } from '@/apis/image';
import Failed from '@/assets/images/failed.jpg'
import DefaultAvatar from '@/assets/images/avatar.jpeg'
import { openImagePreview } from '@/utils/dialog';
const props = defineProps<{
  resId?: string //图片资源id
  poster?: boolean,
  video?: boolean //视频封面
  avatar?: boolean //用户头像
  preview?: boolean | string[] //预览列表
}>()
const loading = ref(false)
const error = ref(false)
watch(()=>props.resId, ()=>{
  error.value = false
})
const src = computed(()=>{
  if (error.value) {
    return Failed
  }
  if (props.resId && props.resId !== '0') {
    return imageUrl + props.resId
  }
  return props.avatar ? DefaultAvatar : Failed
})
watchImmediate(src, ()=>{
  loading.value = true
  //使用虚拟dom提前加载图片,期间播放加载动画
  //利用缓存让真实dom瞬间加载完成
  const img = document.createElement('img')
  img.src = src.value
  img.onload = ()=>{
    loading.value = false
    img.remove()
  }
  img.onerror = onError
})
const classList = computed(()=>{
  return {
    'miku-image': true,
    '__video': props.video,
    '__poster': props.poster,
    '__avatar': props.avatar,
  }
})
function onError() {
  logger.debug('加载图片失败', src.value)
  error.value = true
}
function onClick(e: MouseEvent) {
  if (props.preview) {
    const urlList = []
    if (Array.isArray(props.preview)) {
      for (const url of props.preview) {
        urlList.push(imageUrl + url)
      }
    } else if (props.resId && props.resId != '0') {
      urlList.push(src.value)
    }
    if (urlList.length) {
      e.preventDefault()
      openImagePreview({
        urlList,
        initialIndex: Math.max(urlList.indexOf(src.value), 0)
      })
    }
  }
}
const imgAttrs = computed(()=>{
  if (props.preview) {
    return {
      title: props.preview ? '点击预览' : null,
      style: {
        'cursor': 'pointer'
      }
    }
  }
  return {}
})
</script>

<style scoped lang="scss">
//img 默认object-fit为fill
img {
  user-select: none;
}
.__video {
  height: fit-content;
  aspect-ratio: 16/9;
  overflow: hidden;
  border-radius: 8px;
}
.__poster {
  height: fit-content;
  aspect-ratio: 3/4;
  overflow: hidden;
  border-radius: 8px;
}
.__avatar {
  aspect-ratio: 1;
  border-radius: 50%;
  white-space: nowrap;
  :deep(.el-skeleton__image) {
    border-radius: 50%;
  }
}
</style>