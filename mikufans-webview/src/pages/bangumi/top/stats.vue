<template>
  <div class="flex items-center justify-between">
    <div class="number-tip">
      <div>总播放</div>
      <div class="font-bold">{{ displayNumber(video.plays) }}</div>
    </div>
    <div class="number-tip">
      <div>追番人数</div>
      <div class="font-bold">{{ displayNumber(video.bangumi.subscribe) }}</div>
    </div>
    <div class="number-tip last">
      <div>弹幕总数</div>
      <div class="font-bold">{{ displayNumber(video.danmus) }}</div>
    </div>
    <div class="score-number ml-auto mr-4">{{ video.bangumi.rate }}</div>
    <div class="cursor-pointer" title="我要点评" @click="openRate">
      <el-rate class="pointer-events-none" v-model="video.bangumi.rate" :max="10" allow-half disabled/>
      <div class="text-[12px]">{{video.bangumi.rateCount}}人评</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import RateDialog from './RateDialog.vue';
import { displayNumber } from '@/utils/common';
import { openDialog } from '@/utils/dialog';
const { video } = toRefs(useBangumiStore())
function openRate() {
  openDialog(RateDialog, {
    bangumi: video.value.bangumi,
    title: video.value.title,
  })
}
</script>

<style scoped lang="scss">
.score-number {
  font-size: 36px;
  color: #ffa726;
}
.number-tip {
  padding-right: 50px;
  margin-right: 16px;
  display: flex;
  flex-direction: column;
  white-space: nowrap;
  gap: 4px;
  &:not(.last) {
    border-right: 2px solid #eee;
  }
}
</style>