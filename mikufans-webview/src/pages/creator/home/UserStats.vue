<template>
  <div class="p-2 px-6">
    <div class="py-4">
      <span>统计数据</span>
    </div>
    <div class="grid grid-cols-4 grid-rows-2 gap-4">
      <div class="card-item" v-for="{label, value} in cards">
        <div class="label">{{ label }}</div>
        <div class="value">{{ isNumber(value) ? displayNumber(value) : value }}</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
import { isNumber } from 'lodash';
const userStore = useUserStore()
useIntervalFn(()=>{
  userStore.getUserInfo(true)
}, 10_000)
const cards = computed(()=>{
  return [
    { label: '获得粉丝', value: userStore.fans },
    { label: '发布动态', value: userStore.dynamics },
    { label: '收到点赞', value: userStore.likes },
    { label: '收到硬币', value: userStore.coins },
    { label: '投稿视频', value: userStore.videos },
    { label: '发布说说', value: userStore.publishes },
    { label: '投稿文章', value: userStore.articles },
    { label: '当前等级', value: userStore.level },
  ]
})
</script>

<style scoped lang="scss">
.card-item {
  background-color: #f5fcfe;
  border-radius: 8px;
  padding: 8px;
  .label {
    color: var(--grey1);
    font-size: 16px;
    margin-bottom: 12px;
  }
  .value {
    font-size: 22px;
    color: var(--blue0);
    font-weight: 550;
  }
}
</style>