<template>
  <div class="upload-zone" ref="uploadRef" @click="openFilesSelect" title="上传视频">
    <i class="iconfont icon-shangchuan"></i>
    <p class="grey1">将视频文件拖拽至此处上传</p>
    <div class="submit">上传视频</div>
    <p class="grey2 text-sm">
      <span class="mr-1">当前服务器状态:</span>
      <el-text v-if="state" :type="state.type as any">{{ state.label }}</el-text>
    </p>
  </div>
</template>

<script setup lang="ts">
import { getProcessStatus } from '@/apis/video/process';
import { openFileSelect } from '@/utils/dialog';
const emits = defineEmits<{ select: [files: File[]] }>()
const uploadRef = ref<HTMLElement>(null)
useDropZone(uploadRef, {
  //dataTypes: ['video/*', 'video/x-matroska'], bug
  multiple: true,
  onDrop(files, event) {
    files = files.filter(file=>file.type.startsWith('video/'))
    upload(files)
  },
})
//video/x-matroska代表mkv格式的视频,video/*不包含它！！
function openFilesSelect() {
  openFileSelect({
    accept: 'video/*,video/x-matroska',
    multiple: true,
    callbacks: upload,
  })
}
function upload(files: File[]) {
  if (files.length) {
    emits('select', files)
  }
}
const { state } = useAsyncState(getProcessStatus(), null)
</script>

<style scoped lang="scss">
.upload-zone {
  border: 2px dashed var(--grey2);
  width: 70%;
  padding-top: 30px;
  padding-bottom: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 40px auto;
  cursor: pointer;
}
.icon-shangchuan {
  font-size: 50px;
  color: var(--grey2);
}
.submit {
  width: 180px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  border-radius: 6px;
  background-color: var(--blue1);
  margin: 16px 0;
  color: #fff;
}
</style>