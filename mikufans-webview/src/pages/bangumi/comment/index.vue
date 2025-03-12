<template>
  <div class="index">
    <div class="mb-2">
      <el-button class="submit" @click="open">发表评价</el-button>
    </div>
    <Async always :loading="loading" :error="error" :empty="list.length==0" empty-text="没有一个人评分" min-h="240px">
      <div class="rate-list" v-infinite-scroll="next" :infinite-scroll-disabled="loading">
        <div class="rate-item" v-for="i in list" :key="i.id">
          <div class="flex items-center gap-3">
            <user-avatar size="36px" :user="i.user"/>
            <span class="min-w-24">{{ i.user.nickname }}</span>
            <el-rate v-model="i.rate" :max="10" show-score disabled/>
            <span class="ml-auto grey1">{{ i.publishDate }}</span>
          </div>
          <div class="content" :blank="isBlank(i.content)">{{ isBlank(i.content) ? '这个人很懒，没有留下评论。' : i.content }}</div>
        </div>
      </div>
    </Async>
  </div>
</template>

<script setup lang="ts">
import { useBangumiStore } from '@/stores/bangumi';
import bangumiApi from '@/apis/user/rate';
import { usePage } from '@/hooks/usePage';
import { openDialog } from '@/utils/dialog';
import RateDialog from '../top/RateDialog.vue';
import { isBlank } from '@/utils/common';
const { video } = toRefs(useBangumiStore())
function search(pageNum: number) {
  return bangumiApi.listRate(video.value.bangumiId, {
    pageNum,
    pageSize: 10
  })
}
const { list, next, loading, error } = usePage(search, {
  immediate: [1],
  shallowRef: false,
  compare: (a,b)=>a.id===b.id,
})
function open() {
  openDialog(RateDialog, { 
    bangumi: video.value.bangumi,
    title: video.value.title,
    onSubmit(data) {
      const old = list.value.find(a=>a.id===data.id)
      if (old) {
        Object.assign(old, data)
      } else {
        list.value.unshift(data)
      }
    }
  })
}
</script>

<style scoped lang="scss">
.index {
  background-color: var(--bg0);
  min-height: inherit;
  padding: 16px;
}
.submit {
  border: 1px solid var(--blue0);
}
.rate-item {
  border-bottom: 1px solid #ccc;
  padding: 20px 0;
  .content {
    padding: 16px 0;
    &[blank=true] {
      color: var(--grey2);
    }
  }
  &:last-of-type {
    border: none;
  }
}
</style>