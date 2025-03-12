<template>
  <ul class="flex items-center">
    <template v-for="option, index in options">
      <li @click="model=option.value" :class="{ active: model==option.value }">
        <slot v-bind="option">
          <span>{{ option.label }}</span>
        </slot>
      </li>
      <el-divider v-if="index<options.length-1" direction="vertical"/>
    </template>
  </ul>
</template>

<script setup lang="ts" generic="T">
const props = defineProps({
  options: array<Option & T>().def([]),
})
// const emits = defineEmits<{ change: [value: string] }>()
const model = defineModel<Option['value']>('modelValue', { default: '' })
</script>

<style scoped lang="scss">
ul {
  li {
    transition: all .3s;
    user-select: none;
    cursor: pointer;
    padding: 4px;
    &.active {
      color: var(--blue1);
    }
  }
}
</style>