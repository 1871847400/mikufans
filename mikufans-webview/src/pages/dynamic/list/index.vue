<template>
  <div class="mt-2">
    <div v-infinite-scroll="next" :infinite-scroll-disabled="loading||error">
      <async always :loading="loading" :error="error" :empty="list.length==0" :empty-text="emptyText" min-h="200px">
        <dynamic-item v-for="data in list" :key="data.id" :data="data" />
      </async>
    </div>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import userDynamicApi from '@/apis/user/dynamic'
import DynamicItem from './DynamicItem.vue';
const { activeUser } = toRefs(useDynamicStore())
const { unreadDynamic } = useMsgStore()
const { fakeUser, useEvent } = useDynamicStore()
const { next, reset, loading, list, error } = usePage(search, { 
  shallowRef: false,
  compare(a, b) {
    return a.id === b.id
  },
  onSuccess() {
    unreadDynamic.execute()
  }
 })
function search(pageNum: number) {
  let userId = null
  if (activeUser.value.id !== fakeUser.id) {
    userId = activeUser.value.id
  }
  return userDynamicApi.search({
    userId,
    pageNum,
    pageSize: 5,
  })
}
useEvent('newDynamic', (dynamic)=>{
  if (activeUser.value.id === fakeUser.id) {
    if (!list.value.find(a=>a.id===dynamic.id)) {
      list.value.unshift(dynamic)
    }
  }
})
useEvent('delDynamic', (id)=>{
  list.value = list.value.filter(a=>a.id!=id)
})
const emptyText = computed(()=>{
  return activeUser.value.id === fakeUser.id ? '动态列表空空如也!' : '这个人还没有发布过动态!'
})
watchImmediate(activeUser, ()=>{
  reset()
  next()
})
</script>

<style scoped lang="scss">
:deep(.trick) {
  background-color: var(--bg0);
}
</style>