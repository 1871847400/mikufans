<template>
  <section class="bangumi-intro">
    <a class="poster" :href="bangumi.uri" target="_blank" draggable="false">
      <miku-image :res-id="bangumi.posterId" class="w-full" poster draggable="false" />
    </a>
    <div class="flex-1 flex flex-col">
      <div class="flex items-center justify-between mb-2">
        <a class="text-[22px]" :href="bangumi.uri" target="_blank">{{ video.title }}</a>
        <subscribe-button :bangumi="bangumi"/>
      </div>
      <div class="mb-4 gap-4 flex">
        <span>{{ displayNumber(video.plays) }}播放</span>
        <span>{{ displayNumber(video.danmus) }}弹幕</span>
        <span>{{ displayNumber(bangumi.subscribe) }}追番</span>
      </div>
      <div class="grey1 flex gap-2 mb-2">
        <span>{{ bangumi.styleNames }}</span>
        <span>{{ bangumi.premiere || '开播日期未知' }}</span>
        <span>{{ bangumi.desc }}</span>
      </div>
      <rich-text html :content="video.intro" :rows="3" />
      <div class="flex items-center justify-between mt-auto pt-6">
        <div style="color: #f7ba2a;">
          <span class="text-xl font-bold mr-1">{{ bangumi.rate }}</span>
          <span class="mr-2">分</span>
          <span class="grey2 text-xs">{{ bangumi.rateCount }}人评分</span>
        </div>
        <div class="flex items-center cursor-pointer" @click="rate">
          <span class="mr-2">我的评分</span>
          <el-rate :min="0" :max="5" allow-half :model-value="userRate/2" disabled/>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import RateDialog from '@/pages/bangumi/top/RateDialog.vue';
import { displayNumber } from '@/utils/common';
import { openDialog } from '@/utils/dialog';
const props = defineProps<{ bangumi: Bangumi }>()
const video = toRef(props.bangumi, 'video')
const userRate = computed(()=>props.bangumi.userRate?.rate||0)
function rate() {
  openDialog(RateDialog, { 
    bangumi: props.bangumi, 
    title: video.value.title 
  })
}
</script>

<style scoped lang="scss">
.bangumi-intro {
  margin-top: 16px;
  margin-bottom: 48px;
  min-height: 48px;
  position: relative;
  font-size: 14px;
  display: flex;
  a.poster {
    /* width: 180px; */
    width: 24%;
    height: auto;
    display: block;
    flex-shrink: 0;
    border-radius: 8px;
    margin-right: 12px;
  }
}
</style>