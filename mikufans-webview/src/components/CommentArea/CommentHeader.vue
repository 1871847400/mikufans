<template>
  <div class="comment-header">
    <span class="text-sm grey1">{{ comment.user.nickname }}</span>
    <svg-icon :name="'level_'+comment.user.level" :size="32"/>
    <span class="uploader" v-if="comment.userId===area.userId">UP</span>
    <span class="selected" v-else-if="comment.selected">精选</span>
    <span class="top" v-if="comment.top && !comment.isChild">置顶</span>
    <span class="uid" @click="locate">#{{ comment.uid }}</span>
  </div>
</template>

<script setup lang="ts">
import { useStore } from './store';
const props = defineProps<{ comment: UserComment }>()
const { area } = useStore()
function locate(e: MouseEvent) {
  if (e.target instanceof HTMLElement) {
    e.target.scrollIntoView({
      behavior: 'smooth',
      inline: 'start',
      block: 'center',
    })
  }
}
</script>

<style scoped lang="scss">
.comment-header {
  display: flex;
  align-items: center;
  column-gap: 6px;
  /* &[child=true] {
    display: inline-flex;
    margin-right: 12px;
  } */
  .top {
    color: var(--pink0);
    border: 1px solid var(--pink0);
    border-radius: 4px;
    padding: 0 1px;
    font-size: 12px;
    user-select: none;
  }
  .selected {
    background-color: #ff5bcb;
    border-radius: 4px;
    color: #fff;
    font-size: 12px;
    padding: 0 2px;
  }
  .uploader {
    background-color: #ff679a;
    border-radius: 4px;
    color: #fff;
    font-size: 12px;
    padding: 0 2px;
  }
  .uid {
    font-size: 12px;
    margin-left: auto;
    padding-left: 4px;
    color: #999;
    align-self: flex-start;
    user-select: none;
    cursor: pointer;
  }
}
</style>