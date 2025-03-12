<template>
  <el-tag v-if="dictData" :type="dictData.tagType as any">{{ dictData.dictLabel }}</el-tag>
</template>

<script setup lang="ts">
import { useDictData } from '@/hooks/useDictData';
const props = defineProps<{
  type: string
  value: any
}>()
const dictData = useDictData(props.type, toRef(props, 'value'))
</script>

<style scoped lang="scss">
</style>