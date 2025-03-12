<template>
  <div class="type-selector">
    <template v-for="{ label, value } in types" :key="label">
      <div class="selector-item" :class="{active:value==type}" @click="type=value">{{ label }}</div>
    </template>
  </div>
</template>

<script setup lang="ts">
const type = defineModel<BusinessType>()
const types : { label: string, value: BusinessType }[] = [
  { label: '全部', value: null },
  { label: '视频', value: 'VIDEO' },
  { label: '说说', value: 'PUBLISH' },
  { label: '点评', value: 'RATE' },
]
</script>

<style scoped lang="scss">
.type-selector {
  position: fixed;
  transform: translateX(calc(-100% - 20px));
  /* bottom: 10px; */
  background-color: var(--bg0);
  height: fit-content;
  border-radius: 6px;
  padding: 8px 0;
  .selector-item {
    color: var(--grey0);
    width: 50px;
    height: 30px;
    line-height: 30px;
    font-size: 14px;
    cursor: pointer;
    user-select: none;
    text-align: center;
    &.active {
      background-color: var(--blue0);
      color: #fff;
    }
  }
}
</style>