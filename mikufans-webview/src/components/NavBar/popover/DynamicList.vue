<template>
  <el-scrollbar max-height="520px">
    <div v-if="loading" class="flex-center p-6">加载中...</div>
    <div v-else-if="error" class="text-center py-6">出现错误</div>
    <div v-else-if="list.length===0 && done" class="text-center py-6">动态为空</div>
    <div v-else class="py-2" v-infinite-scroll="next" :infinite-scroll-disabled="loading||error" :infinite-scroll-distance="30">
      <template v-for="{ id, user, source, publishTimeStr } in list" :key="id">
        <div class="dynamic-item">
          <user-avatar :user="user" size="36px" :popover="false" />
          <div class="flex-1">
            <a class="maxline-1 w-fit hoverable" :href="user.uri" target="_blank">{{ user.nickname }}</a>
            <div class="flex">
              <div>
                <div class="title maxline-2 max-w-[150px]">{{ source?.['title'] || '视频已被删除' }}</div>
                <div class="text-xs grey2">{{ publishTimeStr }}</div>
              </div>
              <a class="ml-auto" :href="source?.['uri']" target="_blank">
                <miku-image class="w-[80px]" :res-id="source?.['bannerId']" video/>
              </a>
            </div>
          </div>
        </div>
      </template>
    </div>
  </el-scrollbar>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import userDynamicApi from '@/apis/user/dynamic';
const userStore = useUserStore()
const { unreadDynamic } = useMsgStore()
const { next, run, loading, list, done, error } = usePage(search, {
  immediate: [1],
  onSuccess() {
    unreadDynamic.execute()
  }
})
async function search(pageNum: number) {
  return userDynamicApi.search({
    dynamicType: 'VIDEO',
    excludeSelf: true,
    pageNum,
    pageSize: 10,
  })
}
</script>

<style scoped lang="scss">
.dynamic-item {
  display: flex;
  gap: 8px;
  transition: all .3s;
  padding: 12px;
  &:hover {
    background-color: var(--bg2);
  }
  .title {
    line-height: 1.5;
    height: calc(2em * 1.5);
  }
}
</style>