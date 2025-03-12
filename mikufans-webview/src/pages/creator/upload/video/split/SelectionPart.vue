<template>
  <div class="selection-part" :class="{ focus }">
    <i class="iconfont icon-gengduo5 cursor-grabbing mr-2 handle" @click.stop></i>
    <span>{{ toTimeString(0) }}</span>
    <span class="px-2">~</span>
    <span>{{ toTimeString(1) }}</span>
    <span v-if="closeable" class="iconfont close" title="删除此段" @click.stop="$emit('close')">&#xe648;</span>
  </div>
</template>

<script setup lang="ts">
import { toHMS } from '@/utils/datetime';
const props = defineProps<{
  data: [number, number],
  duration: number,
  focus: boolean,
  closeable: boolean
}>()
defineEmits(['close'])
function toTimeString(i: number) {
  return toHMS(props.data[i]*props.duration, 's', true)
}
</script>

<style scoped lang="scss">
.selection-part {
  box-sizing: border-box;
  border: 2px solid #888;
  display: inline-block;
  margin: 4px 6px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  padding: 0 6px;
  border-radius: 6px;
  user-select: none;
  cursor: pointer;
  &:hover:not(.focus) {
    border-color: #8eddf7;
  }
  &.focus {
    border-color: #00a1d6;
  }
}
.close {
  padding-left: 6px;
  color: #ccc;
  &:hover {
    color: #00a1d6;
  }
}
</style>