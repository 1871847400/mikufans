<template>
  <svg class="svg-icon" :fill="fill" :width="size" :height="size">
    <use :xlink:href="symbolId" style="fill: inherit;"/>
  </svg>
</template>

<script setup lang="ts">
const props = defineProps({
  prefix: {
    type: String,
    default: 'icon',
  },
  name: {
    type: String,
    required: true,
  },
  size: {
    type: Number,
    default: 20,
  },
  fill: {
    type: String,
    default: undefined
  }
})
const symbolId = computed(() => `#${props.prefix}-${props.name}`)
</script>

<style lang="scss" scoped>
.svg-icon {
  display: inline-block;
}
</style>