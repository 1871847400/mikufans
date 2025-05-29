<template>
  <div class="grid grid-cols-3 grid-rows-[360px] gap-4">
    <my-chart v-if="state" name="注册人数" :data="state" />
    <my-chart v-if="state1" name="视频投稿" :data="state1" />
    <my-chart v-if="state2" name="文章发布" :data="state2" />
  </div>
</template>

<script setup lang="ts">
import { getStatTrend } from '@/apis/admin/stat';
import MyChart from './MyChart.vue';
import dayjs from 'dayjs';
const { state } = useAsyncState(getStatTrend({
  type: 'USER_REGISTER',
  start: dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
  end: dayjs().format('YYYY-MM-DD'),
}), null)
const { state: state1 } = useAsyncState(getStatTrend({
  type: 'VIDEO_UPLOAD',
  start: dayjs().subtract(3000, 'day').format('YYYY-MM-DD'),
  end: dayjs().format('YYYY-MM-DD'),
}), null)
const { state: state2 } = useAsyncState(getStatTrend({
  type: 'ARTICLE_UPLOAD',
  start: dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
  end: dayjs().format('YYYY-MM-DD'),
}), null)
</script>

<style scoped lang="scss">
</style>