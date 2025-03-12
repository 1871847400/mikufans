<template>
  <div class="theme-select" @click="change()" :title="title">
    <template v-for="i in themes">
      <i v-if="theme===i.value" class="iconfont" v-html="i.icon"></i>
    </template>
  </div>
</template>

<script setup lang="ts">
import { useTheme } from '@/utils/theme';
const { theme, themes, change } = useTheme()
const title = computed(()=>themes.find(t=>t.value===theme.value)?.label+'主题')
</script>

<style scoped lang="scss">
.theme-select {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  cursor: pointer;
  box-sizing: border-box;
  border-radius: 8px;
  background-color: var(--bg0);
  border: 1px solid var(--bg2);
  user-select: none;
  &:hover {
    background-color: var(--bg1);
  }
}
</style>