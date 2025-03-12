<template>
  <div class="flex gap-4 bg1 relative">
    <type-selector v-model="type" />
    <div class="flex-1 rounded-lg" v-infinite-scroll="next" :infinite-scroll-disabled="loading||error">
      <async always :loading="loading" :empty="list.length==0" :empty-text="emptyText" :error="error" min-h="200px">
        <dynamic-item v-for="data in list" :key="data.id" :data="data"/>
      </async>
    </div>
    <right-list/>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import userDynamicApi from '@/apis/user/dynamic'
import DynamicItem from '@/pages/dynamic/list/DynamicItem.vue';
import RightList from '../home/RightList.vue';
import TypeSelector from './TypeSelector.vue';
const { userId } = storeToRefs(useSpaceStore())
const keyword = useRouteQuery<string>('kw')
const type = ref<BusinessType>(null)
const emptyText = computed(()=>{
  return keyword.value ? `没有找到关于"${keyword.value}"的动态` : 'TA还没有任何动态'
})
const { next, reset, loading, list, error } = usePage(search, { shallowRef: false })
function search(pageNum: number) {
  return userDynamicApi.search({
    keyword: keyword.value,
    userId: userId.value,
    dynamicType: type.value,
    pageNum,
    pageSize: 5,
  })
}
watchImmediate([userId, keyword, type], ()=>{
  reset()
  next()
})
</script>

<style scoped lang="scss">
:deep(.trick) {
  background-color: var(--bg0);
}
</style>