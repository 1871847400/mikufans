<template>
  <div class="flex items-center gap-4 my-3">
    <!-- <router-link class="dynamic" to="/dynamic">
      <svg-icon name="person-active" :size="30"/>
      <span>动态</span>
    </router-link> -->
    <div class="flex-1 grid gap-6 grid-cols-10">
      <template v-for="i in state">
        <router-link class="channel-link" :to="i.url" target="_blank" :title="i.channelName">
          <svg-icon :name="i.iconName" :size="26"/>
          <span class="hidden-xs">{{ i.channelName }}</span>
        </router-link>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { listVideoChannels } from '@/apis/video/channel';
const { state } = useAsyncState(listVideoChannels(), [])
</script>

<style scoped lang="scss">
.dynamic {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 12px;
  margin-right: 16px;
}
.channel-link {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  /* font-size: max(1vw, 16px); */
  /* border: 1px solid #ccc;
  background-color: #eee; */
  border-radius: 8px;
  user-select: none;
  transition: all .2s;
  height: 36px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: clip;
  &:hover {
    background-color: var(--bg1);
  }
}
</style>