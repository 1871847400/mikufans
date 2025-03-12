<template>
  <!-- 首页按钮的悬浮框 -->
  <div class="channel-list">
    <div v-if="isLoading" class="text-center w-full">加载中...</div>
    <div v-if="state.length === 0 && !isLoading" class="text-center w-full">分区为空！</div>
    <section v-else v-for="i in chunk(state, 6)">
      <a :href="j.url" v-for="j in i">
        <svg-icon :name="j.iconName" :size="24"/>
        <span>{{ j.channelName }}</span>
      </a>
    </section>
  </div>
</template>

<script setup lang="ts">
import { listVideoChannels } from '@/apis/video/channel';
import { chunk } from 'lodash';
const { state, isLoading } = useAsyncState(listVideoChannels({}), [])
</script>

<style scoped lang="scss">
.channel-list {
  z-index: 1;
  width: 100%;
  min-width: 150px;
  height: auto;
  border-radius: 8px;
  padding: 16px 0;
  box-shadow: 0 0 30px #0000001a;
  display: flex;
  background-color: var(--bg0);
  & > section {
    flex: 1;
    list-style: none;
    display: flex;
    flex-direction: column;
    padding: 0 16px;
    &:nth-child(n+2) {
      border-left: 1.5px solid #eee;
    }
    & > a {
      display: flex;
      align-items: center;
      width: 120px;
      height: 36px;
      line-height: 36px;
      padding: 0 4px;
      margin-bottom: 10px;
      border-radius: 6px;
      text-decoration: none;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      *:first-child {
        margin-left: 3px;
        margin-right: 5px;
      }
      &:hover {
        background-color: var(--bg1);
      }
    }
  }
}
</style>