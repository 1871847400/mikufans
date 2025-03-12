<template>
  <div class="px-16 py-6">
    <el-form ref="formRef" label-width="100px" label-position="top" size="large" :disabled="submitting">
      <el-collapse v-model="actives">
        <el-collapse-item name="1">
          <template #title>
            <div class="mb-4">
              <span class="text-lg font-bold">文件上传</span>
              <span class="text-xs mx-2">({{ uploadStore.files.length }}/200)</span>
              <span class="text-xs grey2">支持mp4,mkv,avi等格式</span>
            </div>
          </template>
          <part-container/>
        </el-collapse-item>
        <el-collapse-item name="2">
          <template #title>
            <div class="mb-4 text-lg font-bold">基本信息</div>
          </template>
          <base-info />
        </el-collapse-item>
        <el-collapse-item name="3">
          <template #title>
            <div class="mb-4 text-lg font-bold">高级设置</div>
          </template>
          <senior-section />
        </el-collapse-item>
        <el-collapse-item name="4" v-if="uploadStore.videoType!=='VIDEO'">
          <template #title>
            <div class="mb-4 text-lg font-bold">{{ getPrefix }}设置</div>
          </template>
          <bangumi-section />
        </el-collapse-item>
      </el-collapse>
      <el-form-item>
        <el-button
          @click="submit"
          class="upload-button" 
          color="#00a1d6" 
          :loading="submitting"
        >
          {{ uploadStore.videoId ? '保存编辑' : '立即投稿' }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import BangumiSection from '../anime/index.vue';
import BaseInfo from './BaseInfo.vue'
import PartContainer from '../part/PartContainer.vue'
import SeniorSection from '../senior/index.vue'
import { submitVideo } from './submit';
const formRef = useTemplateRef('formRef')
const actives = ref(['1', '2', '3', '4'])
const uploadStore = useUploadStore()
const submitting = ref(false)
async function submit() {
  try {
    submitting.value = true
    await submitVideo()
  } finally {
    submitting.value = false
  }
}
const getPrefix = computed(()=>{
  switch (uploadStore.videoType) {
    case 'ANIME':
      return '番剧'
    case 'MOVIE':
      return '电影'
    default:
      return ''
  }
})
</script>

<style scoped lang="scss">
.upload-button {
  width: 120px;
  height: 40px;
}
.setting-btn {
  width: fit-content;
  font-size: 18px;
  font-weight: 550;
  user-select: none;
  cursor: pointer;
  padding-bottom: 16px;
}
</style>