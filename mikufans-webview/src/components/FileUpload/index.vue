<template>
  <div class="file-upload"
    @click="onClick"
    @dragenter.prevent 
    @dragover.prevent
    @drop.prevent="onDrop"
  >
    <slot>
      <div class="file-upload-content">
        <div>
          <span class="filename">{{ targetFile?.name || '点击上传或拖拽文件到此处' }}</span>
          <i v-if="targetFile" class="iconfont icon-chacha1" title="清除文件" @click.capture.stop="clearFile"></i>
        </div>
        <template v-if="targetFile && autoUpload">
          <el-progress :percentage="percentage" :show-text="false" :status="percentage>=100?'success':''"/>
          <div class="progress-tip">正在上传中 {{ percentage }}%</div>
        </template>
      </div>
    </slot>
  </div>
</template>

<script setup lang="ts">
import { openFileSelect } from '@/utils/dialog';
import { ref } from 'vue'
type FileStatus = 'none'|'prepare'|'uploading'|'uploaded'
const status = ref<FileStatus>('none')
defineProps<{
  modelValue?: File,
  autoUpload?: boolean,
}>()
const emits = defineEmits<{
  'update:modelValue': [file: File]
}>()
const percentage = ref(0)
// setInterval(()=>{
//   percentage.value++
// }, 100)
const targetFile = ref<File>(null)
function onClick() {
  openFileSelect({
    accept: '*/*',
    callback(file) {
      targetFile.value = file
      emits('update:modelValue', file)
    },
  })
}
function onDrop(e: DragEvent) {
  if (e.dataTransfer.files.length) {
    const file = e.dataTransfer.files.item(0)
    targetFile.value = file
    emits('update:modelValue', file)
  }
}
function clearFile() {
  targetFile.value = null
  emits('update:modelValue', null)
}
</script>

<style scoped lang="scss">
.file-upload {
  width: fit-content;
  cursor: pointer;
}
.file-upload-content {
  box-sizing: border-box;
  border: 1px dashed #ccc;
  min-width: 180px;
  text-align: center;
  padding: 10px 14px;
  border-radius: 6px;
  user-select: none;
  .filename {
    color: #000;
    font-weight: 500;
    &:hover {
      color: #666;
    }
  }
  .iconfont {
    padding: 0 6px;
    vertical-align: middle;
    &:hover {
      color: #666;
    }
  }
  .progress-tip {
    font-size: 12px;
    color: #888;
  }
}
</style>