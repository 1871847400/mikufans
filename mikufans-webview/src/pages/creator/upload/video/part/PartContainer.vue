<template>
  <div>
    <el-scrollbar max-height="800px" class="mb-4">
      <ul ref="el" class="flex flex-col gap-6 pr-4">
        <template v-for="part,index in partList" :key="part.uuid">
          <part-item :index="index+1" :part="part"/>
        </template>
      </ul>
    </el-scrollbar>
    <el-button type="primary" @click="appendFile" size="large">
      <i class="iconfont icon-jia mr-2"></i>
      <span>添加视频</span>
    </el-button>
  </div>
</template>

<script setup lang="ts">
import { useSortable } from '@vueuse/integrations/useSortable';
import PartItem from './PartItem.vue'
import { openFileSelect } from '@/utils/dialog';
const { data, partList, videoType } = toRefs(useUploadStore())
const { uploadFile } = useUploadStore()
const el = ref<HTMLElement>()
useSortable(el, partList, {
  animation: 250,
  handle: '.handle' //只有该class才能拖动
})
function appendFile() {
  openFileSelect({
    accept: 'video/*,video/x-matroska',
    multiple: true,
    callback: uploadFile,
  })
}
</script>

<style scoped lang="scss">
.part-append {
  margin-left: 32px;
  width: 120px;
  padding: 8px 0;
  border-radius: 6px;
  margin-bottom: 16px;
  text-align: center;
  cursor: pointer;
  user-select: none;
  color: var(--grey1);
  background-color: #fff;
  &:hover {
    background-color: #eee;
  }
}
</style>