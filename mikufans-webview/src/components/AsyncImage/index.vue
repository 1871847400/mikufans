<template>
  <!-- 使用a标签,鼠标右键会当做链接处理 -->
  <a class="async-image" :class="{ circle, 'cursor-pointer': href.startsWith('/') }" :href="href" @click="onClick">
    <!-- 主要加载的图片 禁止使用v-bind($attrs) -->
    <img v-if="value" :src="value" @error="onImgError" :draggable="false">
    <!-- 默认加载的图片 -->
    <img v-else-if="src" :src="src" :draggable="false">
    <!-- 加载中... -->
    <el-skeleton v-else class="size-full" animated>
      <template #template>
        <el-skeleton-item variant="image" class="size-full aspect-square"/>
      </template>
    </el-skeleton>
  </a>
</template>

<script setup lang="ts">
import { imageUrl } from '@/apis/image';
import Failed from '@/assets/images/failed.jpg'
import { openImagePreview } from '@/utils/dialog';
import { RouteLocationRaw } from 'vue-router';
const props = defineProps({
  //默认加载的图片
  src: string(),
  //服务器图片id
  resId: string(),
  //圆形图片,宽高一致
  circle: bool().def(false),
  //这个链接会根据开头有无 "/" 来判断是否从当前浏览器路径追加还是重置
  //如果使用 "#" 会到页面上方,使用空函数才不会做任何事
  //如果不使用默认值，自动设置cursor
  href: string().def('javascript:void(0);'),
  //路由地址
  route: any<RouteLocationRaw>(),
  //裁剪方法
  fit: string<'contain'|'cover'|'fill'|'scale-down'>().def('fill'),
  //点击后预览 如果true只预览当前图片,如果string[]遍历预览
  preview: oneOfType([bool(), array<string>()]).def(false),
})
const router = useRouter()
const value = ref<string>(props.src)
const urlList = []
watchImmediate(()=>props.resId, async ()=>{
  value.value = props.src
  if (props.resId && props.resId !== '0') {
    try {
      value.value = imageUrl + props.resId
      urlList.push(value.value)
    } catch(err) {
      onImgError()
    }
  }
})
function onImgError() {
  value.value = Failed
}
watchDeep(()=>props.preview, async (preview)=>{
  if (Array.isArray(preview)) {
    for (const index in preview) {
      urlList[index] = imageUrl + preview[index]
    }
    urlList.splice(preview.length)
  }
}, { immediate: true })
function onClick(e: MouseEvent) {
  if (props.route) {
    e.preventDefault()
    router.push(props.route)
  }
  else if (props.preview && value.value) {
    e.preventDefault()
    openImagePreview({
      urlList,
      initialIndex: urlList.indexOf(value.value)
    })
  }
}
</script>

<style scoped lang="scss">
.async-image {
  display: block;
  white-space: nowrap;
  /* background-color: var(--grey2); */
  box-sizing: border-box;
  overflow: hidden;
  user-select: none;
  flex-shrink: 0;
}
.circle {
  border-radius: 50%;
  aspect-ratio: 1;
  overflow: hidden;
  /* padding: 1px; */
}
img {
  width: 100%;
  height: 100%;
  display: block; //默认inline-block无法完全匹配父容器高度
  object-fit: v-bind(fit);
  .circle & {
    border-radius: 50%;
  }
}
</style>