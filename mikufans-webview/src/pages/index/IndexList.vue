<template>
  <div class="flex">
    <span class="mr-4 flex-shrink-0 mt-1 grey2">{{ label }}</span>
    <miku-radio v-model="model" :options="_options" class="flex-wrap" />
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  label: string
  query: string
  options: MaybeRef<Option[]>
}>()
const _options = computed(()=>unref(props.options))
const model = useRouteQuery(props.query, unref(props.options)[0].value+'', { mode: 'push' })
</script>

<style scoped lang="scss">
</style>