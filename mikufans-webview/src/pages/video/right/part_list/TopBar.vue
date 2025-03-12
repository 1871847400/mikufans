<template>
  <div class="flex pb-2 gap-1 items-center">
    <span>正片</span>
    <span class="grey2 text-xs">({{ partNumber }}/{{ canplayCount }})</span>
    <span class="iconfont pl-2 text-button grey1" @click="listType=!listType">
      <span v-if="listType" class="icon-liebiao1" title="列表"></span>
      <span v-else class="icon-dasuolvetuliebiao" title="缩略图"></span>
    </span>
    <span class="iconfont text-button mr-auto pl-2 font-bold" @click="reverse=!reverse">
      <span v-if="reverse" class="icon-pailie2" title="降序"></span>
      <span v-else class="icon-pailie1" title="升序"></span>
    </span>
    <el-switch v-model="videoSettings.autoPlayNext" active-text="自动连播" inactive-text="自动连播" inline-prompt/>
  </div>
</template>

<script setup lang="ts">
const { listType, reverse, video, videoSettings, partNumber } = toRefs(useVideoStore())
const canplayCount = computed(()=>video.value.parts.filter(a=>a.canplay).length)
</script>

<style scoped lang="scss">
</style>