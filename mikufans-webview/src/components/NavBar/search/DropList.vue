<template>
  <div>
    <text-button
      class="drop-item truncate" 
      v-for="i,index in tipList" 
      v-html="i.highlightText"
      @click="search(i.rawText)"
      :active="tipIndex===index"
    >
    </text-button>
  </div>
</template>

<script setup lang="ts">
const { search } = useSearchStore()
const { tipList, tipIndex } = storeToRefs(useSearchStore())
</script>

<style scoped lang="scss">
.drop-item {
  width: 100%;
  height: 30px;
  border-radius: 0;
  justify-content: flex-start;
  padding: 0 16px;
  &:hover, &[active=true] {
    background-color: var(--bg1);
  }
}
</style>