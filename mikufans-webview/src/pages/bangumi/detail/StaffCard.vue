<template>
  <div class="card">
    <div class="font-bold text-lg mb-1">{{ title }}</div>
    <div ref="contentRef" class="content" v-html="xss(content)"></div>
    <div v-if="!expand && overflow" class="flex justify-center">
      <el-button class="mt-2" size="small" link @click="expand=true">展开更多</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useOverflow } from '@/hooks/useOverflow';
import xss from 'xss';
defineProps<{ title: string, content: string }>()
const contentRef = useTemplateRef('contentRef')
const { expand, overflow } = useOverflow(contentRef, 200)
</script>

<style scoped lang="scss">
.card {
  background-color: var(--bg0);
  padding: 8px;
  border-radius: 8px;
  min-height: 100px;
  .content {
    white-space: pre-wrap;
    font-size: 14px;
    line-height: 1.75;
    /* 恢复a标签的默认样式 */
    :deep(a) {
      text-decoration: revert;
      color: revert;
    }
  }
}
</style>