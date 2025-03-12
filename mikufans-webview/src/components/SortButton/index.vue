<template>
  <button class="sort-button" :class="{ active, desc, asc: !desc }" @click="onClick">
    <span class="content">
      <slot></slot>
    </span>
    <div class="flex flex-col">
      <i class="iconfont icon-xiangshang"></i>
      <i class="iconfont icon-xiangxia"></i>
    </div>
  </button>
</template>

<script setup lang="ts">
const props = defineProps<{ value: Option['value'] }>()
const model = defineModel<Option['value']>('modelValue', { default: '' })
const desc = defineModel('desc', { default: '1' })
const active = computed(()=>props.value===model.value)
function onClick() {
  if (active.value) {
    desc.value = desc.value=='1'?'':'1'
  }
  model.value = props.value
}
</script>

<style scoped lang="scss">
.sort-button {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  background: none;
  color: inherit;
  font-size: inherit;
  .iconfont {
    font-size: 9px;
    color: var(--grey2);
  }
  .icon-xiangshang {
    transform: translateY(1px);
  }
  .icon-xiangxia {
    transform: translateY(-1px);
  }
  &.active {
    .content {
      color: var(--blue0);
    }
    &.asc .icon-xiangshang {
      color: var(--blue0);
    }
    &.desc .icon-xiangxia {
      color: var(--blue0);
    }
  }
}
</style>