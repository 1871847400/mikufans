<template>
  <div class="grid grid-cols-4 gap-4">
    <template v-for="{ counts, icon, title }, key in state" :key="key">
      <div class="card-item">
        <div class="flex items-center gap-1 grey1">
          <i class="iconfont" :class="icon"></i>
          <span>{{ title }}</span>
          <router-link to="/admin/stats" title="查看统计数据" class="ml-auto">
            <el-icon><Histogram class="grey2" /></el-icon>
          </router-link>
        </div>
        <el-divider class="my-4" />
        <h5 class="count">{{ counts.TOTAL }}</h5>
        <div class="day-count">今日增长：{{ counts.DAY }}</div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { getStatPeriod } from '@/apis/admin/stat';
const { state } = useAsyncState(getStatPeriod(), {})
</script>

<style scoped lang="scss">
.card-item {
  background-color: var(--bg0);
  padding: 10px;
  border-radius: 8px;
  transition: all .3s;
  box-shadow: 0 0 1px 1px #00000011;
  .count {
    font-size: 22px;
    color: var(--blue0);
  }
  .day-count {
    font-size: 14px;
    padding: 8px 0;
    color: var(--grey2);
  }
  &:hover {
    box-shadow: 0 2px 2px 2px #00000011;
    transform: translateY(-4px);
  }
}
</style>