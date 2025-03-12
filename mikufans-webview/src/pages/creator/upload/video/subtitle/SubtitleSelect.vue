<template>
  <div>
    <!-- <div class="text-xs grey2 mt-2">支持文件格式：{{ SubtitleTypes.join(', ') }}</div> -->
    <div class="flex gap-4">
      <el-select v-model="regionId" placeholder="请选择语言" style="width: 120px;">
        <template v-for="{ id, langName } in languages">
          <el-option :label="langName" :value="id" :disabled="subtitles.some(a=>a.regionId==id)" />
        </template>
      </el-select>
      <el-button @click="selectFile" :disabled="!regionId">
        <i class="iconfont icon-tianjia pr-1"></i>
        <span>选择文件</span>
      </el-button>
    </div>
    <template v-if="subtitles.length">
      <div class="grey2 text-xs py-1 mt-1">已上传的字幕文件</div>
      <div class="flex flex-wrap gap-3">
        <template v-for="s in subtitles">
          <div class="flex items-center px-2 rounded-md bg2 w-fit">
            <span class="pr-4 grey2">{{ languages.find(a=>a.id==s.regionId)?.langName }}</span>
            <span class="pr-1">{{ s.filename }}</span>
            <i class="iconfont icon-chacha1 cursor-pointer grey2 text-xs" @click="remove(s.regionId)"></i>
          </div>
        </template>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { listRegions } from '@/apis/system';
import { SubtitleTypes } from '@/apis/video/resource';
import { openFileSelect } from '@/utils/dialog';
const subtitles = defineModel<SubtitleFile[]>('modelValue', { default: [] })
const regionId = ref<string>(null)
const { state: languages } = useAsyncState(listRegions(), [])
function selectFile() {
  openFileSelect({
    async callback(file) {
      const type = getType(file.name)
      if (!type) {
        return message.warning('不支持的字幕格式')
      }
      const text = await file.text()
      subtitles.value.push({
        data: text,
        regionId: regionId.value,
        type,
        filename: file.name,
      })
      regionId.value = null
    },
  })
}
function getType(filename: string) : SubtitleType {
  for (const suffix of SubtitleTypes) {
    if (filename.toLowerCase().endsWith(`.${suffix.toLowerCase()}`)) {
      return suffix
    }
  }
}
function remove(id: string) {
  subtitles.value = subtitles.value.filter(a=>a.regionId!=id)
}
</script>

<style scoped lang="scss">
</style>