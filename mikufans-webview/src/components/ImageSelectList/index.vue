<template>
  <div ref="el" class="flex flex-wrap gap-1 my-1">
    <div v-for="i in files" :key="i.id" class="img-item" :data-status="i.uploadStatus">
      <img :src="i.blobUrl" class="size-full object-cover">
      <div class="buttons absolute inset-0 text-white hidden">
        <i class="iconfont icon-search mr-2 text-xl" title="预览" @click="preview(i)"></i>
        <i class="iconfont icon-lajixiang" title="删除" @click="remove(i)"></i>
      </div>
      <el-progress v-if="i.uploadStatus==='uploading'" :percentage="i.progress" :show-text="false" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useSortable } from '@vueuse/integrations/useSortable'
import { ImageSelect } from '@/hooks/useImageSelect';
import { openImagePreview } from '@/utils/dialog';

const el = ref<HTMLElement>()
const files = defineModel<ImageSelect[]>('modelValue')
useSortable(el, files, {
  animation: 300
})
function preview(file: ImageSelect) {
  openImagePreview({
    urlList: files.value.map(i=>i.blobUrl),
    initialIndex: files.value.indexOf(file)
  })
}
function remove(file: ImageSelect) {
  files.value = files.value.filter(a=>a.id!==file.id)
}
</script>

<style scoped lang="scss">
.img-item {
  width: 90px;
  height: 90px;
  position: relative;
  border-radius: 4px;
  overflow: hidden;
  cursor: move;
  &:hover {
    .buttons {
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #00000076;
      .iconfont {
        cursor: pointer;
      }
    }
  }
  .el-progress {
    position: absolute;
    left: 4px;
    right: 4px;
    bottom: 5px;
  }
  &[data-status=error]::after {
    content: '上传失败';
    position: absolute;
    width: 100%;
    padding: 4px 0;
    bottom: 0;
    text-align: center;
    color: #eee;
    font-size: 12px;
    background-color: #00000094;
  }
}
</style>