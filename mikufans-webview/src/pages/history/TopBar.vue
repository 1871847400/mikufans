<template>
  <div class="flex items-center py-8">
    <h1 class="mr-auto flex items-center gap-1 text-[#e3936c] text-2xl">
      <i class="iconfont text-3xl">&#xeb7b;</i>
      <span>历史记录</span>
    </h1>
    <search-box
      class="mr-6"
      v-model="keyword" 
      placeholder="搜索历史记录" 
      @search="$emit('search', $event)" 
      maxlength="32"
    />
    <el-button type="danger" @click="clearHistory" :icon="Delete">清空记录</el-button>
  </div>
</template>

<script setup lang="ts">
import { Delete } from '@element-plus/icons-vue'
import videoHistoryApi from "@/apis/video/history";
const keyword = defineModel('modelValue', { default: '' })
const emits = defineEmits(['search', 'clear'])
async function clearHistory() {
  await message.confirm('你确定要清除所有播放记录吗?')
  await videoHistoryApi.removeAll()
  emits('clear')
  message.success('已清空历史记录')
}
</script>