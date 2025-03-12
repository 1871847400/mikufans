<template>
  <el-form-item label="封面" required>
    <banner-selector v-model="banner" :placeholder="placeholder" :files="files"/>
  </el-form-item>
  <el-form-item label="标题" required>
    <el-input 
      v-model.trim="data.title"
      show-word-limit
      :maxlength="64"
      placeholder="视频的标题"
    />
  </el-form-item>
  <el-form-item label="类型" required v-if="videoType=='VIDEO'">
    <el-radio-group v-model="data.republish">
      <el-radio :value="0">自制</el-radio>
      <el-radio :value="1">转载</el-radio>
    </el-radio-group>
    <el-input 
      v-if="data.republish==1" 
      v-model="data.sourceUrl" 
      placeholder="请输入转载的地址来源等信息" 
      :maxlength="200" 
      show-word-limit 
    />
  </el-form-item>
  <el-form-item label="分区" required v-if="videoType=='VIDEO'">
    <channel-select v-model="data.channelId"/>
  </el-form-item>
  <el-form-item label="标签" v-if="videoType=='VIDEO'">
    <input-tag class="flex-1" v-model="data.tags" :max="10"/>
  </el-form-item>
  <el-form-item label="简介">
    <video-desc v-model="data.intro"/>
  </el-form-item>
</template>

<script setup lang="ts">
import VideoDesc from './VideoDesc.vue'
import BannerSelector from './BannerSelector.vue'
import { imageUrl } from '@/apis/image';
const { data, files, banner, videoType } = toRefs(useUploadStore())
const placeholder = computed(()=>{
  const id = data.value.bannerId
  if (id && id != '0') {
    return imageUrl + data.value.bannerId
  }
})
</script>

<style scoped lang="scss">
</style>