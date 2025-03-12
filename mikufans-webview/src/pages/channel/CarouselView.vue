<template>
  <div class="px-[64px]" v-if="state.length">
    <el-carousel :interval="4000" type="card" height="200px" indicator-position="outside">
      <!-- 875 : 250 = 3.5 -->
      <el-carousel-item v-for="{ id, title, url, bannerId } in state" :key="id">
        <a class="carousel-item" :href="url" target="_blank">
          <miku-image :res-id="bannerId" class="size-full object-cover" />
          <span class="title">{{ title }}</span>
        </a>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup lang="ts">
import { listCarousels } from '@/apis/system';
const props = defineProps({
  channelId: string().isRequired,
})
function search() {
  return listCarousels({
    position: 'CHANNEL',
    channelId: props.channelId,
  })
}
const { state } = useAsyncState(search, [])
</script>

<style scoped lang="scss">
.carousel-item {
  display: flex;
  justify-content: center;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  width: 100%;
  height: 100%;
  .title {
    position: absolute;
    left: 12px;
    bottom: 12px;
    color: #fff;
    font-size: 18px;
  }
}
</style>