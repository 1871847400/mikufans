<template>
  <i class="stats-icon iconfont" :class="icon">
    <slot>
      {{ displayNumber(count) }}
    </slot>
  </i>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
defineProps({
  icon: string().def(''),
  count: number().def(0)
})
</script>

<style scoped lang="scss">
.stats-icon.iconfont {
  white-space: nowrap;
  flex-shrink: 0;
  &::before {
    padding-right: 4px;
  }
  & + .stats-icon {
    margin-left: 14px;
  }
}
</style>