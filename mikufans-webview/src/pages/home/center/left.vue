<template>
  <el-skeleton class="carousel-container pb-8" :loading="isLoading" animated>
    <template #template>
      <el-skeleton-item class="size-full rounded-lg" variant="image"/>
    </template>
    <div v-if="isReady" class="carousel-container">
      <miku-carousel :list="state">
        <template v-slot="{ url, bannerId }">
          <a :href="url" target="_blank" draggable="false">
            <miku-image class="w-full" :res-id="bannerId" video />
          </a>
        </template>
        <template #footer="{ mainColor, title }">
          <div class="carousel-footer">
            <div class="carousel-mask" :style="{background: mainColor}"></div>
            <div class="carousel-title">{{ title }}</div>
          </div>
        </template>
      </miku-carousel>
    </div>
  </el-skeleton>
</template>

<script setup lang="ts">
import { listCarousels } from '@/apis/system';
const { state, isLoading, isReady } = useAsyncState(listCarousels({ position: 'HOME'}), null)
</script>

<style scoped lang="scss">
.carousel-container {
  grid-column: 1/3;
  grid-row: 1/3;
}
.carousel-footer {
  width: 100%;
  height: 80px;
}
.carousel-mask {
  position: absolute;
  pointer-events: none;
  inset: 0;
  mask-image: linear-gradient(0,#2f3238 24%, transparent 44%);
}
.carousel-title {
  text-indent: 10px;
  position: absolute;
  color: #fff;
  font-size: 18px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>