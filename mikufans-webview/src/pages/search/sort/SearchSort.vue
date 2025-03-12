<template>
  <div v-if="Object.keys(options)?.length" class="search-sort">
    <sort-selector :option="options[0]">
      <template v-if="Object.keys(options).length>1" #append>
        <el-button class="ml-auto" @click="showAll=!showAll">
          <span>更多筛选</span>
          <miku-arrow :model-value="showAll" />
        </el-button>
      </template>
    </sort-selector>
    <div class="h-anim" :class="{ close: !showAll }">
      <div>
        <template v-for="option in options.slice(1)">
          <sort-selector :option="option" />
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import SortSelector from './SortSelector.vue';
const props = defineProps({
  options: array<QueryOption>().def([])
})
const route = useRoute()
//是否显示“更多筛选”，如果任意选项有值才会默认显示
const showAll = ref(Object.keys(props.options).slice(1).some(a=>route.query[a]))
</script>

<style scoped lang="scss">
.search-sort {
}
/* height:auto不支持过渡动画 */
.h-anim {
  transition: all .3s;
  display: grid;
  grid-template-rows: 1fr;
  overflow: hidden;
  & > div {
    min-height: 0;
  }
}
.close {
  grid-template-rows: 0fr;
}
</style>