<template>
  <div 
    class="action-item" 
    title="转发"
    @click="click"
  >
    <circle-progress>
      <i class="iconfont icon-zhuanfa2 text-[28px]"></i>
    </circle-progress>
    <action-number :number="video.shares"/>
    <span class="copy">点击复制链接</span>
  </div>
</template>

<script setup lang="ts">
import { copyText } from '@/utils/copy';
import ActionNumber from '../ActionNumber.vue';
import videoApi from '@/apis/video';
const { video } = toRefs(useVideoStore())
const userStore = useUserStore()
async function click() {
  let url = location.href
  if (userStore.isLogin) {
    const code = await videoApi.createShare(video.value.id)
    if (url.includes('?')) {
      url += '&share=' + code
    } else {
      url += '?share=' + code
    }
  }
  copyText(url)
  message.success('已复制分享链接到剪切板')
}
</script>

<style scoped lang="scss">
.action-item {
  .copy {
    display: none;
    padding-left: 8px;
    font-size: 14px;
  }
  &:hover {
    :deep(.number) {
      display: none;
    }
    .copy {
      display: inline;
    }
  }
}
</style>