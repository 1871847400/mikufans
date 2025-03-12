<template>
  <!-- mousedown.prevent 防止脱离input焦点 -->
  <div class="drop-box" @mousedown.prevent>
    <!-- 当搜索内容为空时,显示搜索历史和热搜 -->
    <template v-if="!keyword">
      <search-history/>
      <hotkey-list/>
    </template>
    <!-- 搜索词补全 -->
    <drop-list v-else/>
  </div>
</template>

<script setup lang="ts">
import SearchHistory from './SearchHistory.vue';
import HotkeyList from './HotkeyList.vue';
import DropList from './DropList.vue';
const { keyword } = storeToRefs(useSearchStore())
</script>

<style scoped lang="scss">
.drop-box {
  padding-bottom: 16px;
}
</style>