<template>
  <div v-if="hotKeys.length">
    <div class="font-bold p-3">热搜</div>
    <div class="list">
      <template v-for="v,k in hotKeys">
        <text-button :order="k+1" :bold="k<3" @click="search(v)" tabindex="0">{{ v }}</text-button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import searchHistoryApi from '@/apis/search';
const { search } = useSearchStore()
const { state: hotKeys } = useAsyncState(searchHistoryApi.getHotkeys(), [])
</script>

<style scoped lang="scss">
@media screen and (max-width: 1200px) {
  .list {
    grid-template-columns: 1fr !important;
  }
}
.list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto;
  column-gap: 10px;
  .text-button {
    height: 36px;
    justify-content: flex-start;
    border-radius: 0;
    &[bold=true]::before {
      font-weight: 550;
    }
    &::before {
      content: attr(order);
      font-size: 16px;
      padding-left: 16px;
      padding-right: 12px;
    }
    &:hover, &:focus {
      background-color: var(--bg1);
    }
  }
}
</style>