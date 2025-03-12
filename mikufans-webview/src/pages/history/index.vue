<template>
  <nav-bar/>
  <div class="history-page" v-infinite-scroll="next" :infinite-scroll-disabled="loading||error">
    <top-bar v-model="keyword" @search="reSearch" @clear="list=[]"/>
    <el-empty v-if="list.length===0 && !loading" description="没有历史记录"/>
    <ul v-else class="history-list" v-loading="loading" :data-loading="loading">
      <suspense>
        <transition-group name="list">
          <template v-for="i,j in list" :key="i.id">
            <history-item :data="i" :prev="list[j-1]" @remove="remove(i.id)"/>
          </template>
        </transition-group>
      </suspense>
    </ul>
  </div>
</template>

<script setup lang="ts">
import HistoryItem from './HistoryItem.vue'
import videoHistoryApi from '@/apis/video/history';
import TopBar from './TopBar.vue';
import { usePage } from '@/hooks/usePage';
const keyword = ref('')
function search(pageNum: number) {
  return videoHistoryApi.search({
    title: keyword.value,
    pageNum,
    pageSize: 10,
  })
}
const { list, loading, next, error, reset } = usePage(search, {
  immediate: [1],
})
function reSearch() {
  reset()
  next()
}
function remove(id: string) {
  list.value = list.value.filter(a=>a.id!==id)
}
</script>

<style scoped lang="scss">
.history-page {
  width: 75%;
  margin: 0 auto;
}
.history-list {
  --left: 86px;
  display: flex;
  flex-direction: column;
  row-gap: 20px;
  padding-bottom: 40px;
  position: relative;
  &[data-loading=true] {
    min-height: 300px;
  }
  &[data-loading=false]::before {
    content: '';
    width: 1px;
    height: 100%;
    background-color: rgba(0,0,0,.2);
    display: block;
    position: absolute;
    left: var(--left);
  }
}
.list-enter-active,
.list-leave-active {
  transition: all 0.3s ease;
}
.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>