<template>
  <div class="overflow-hidden" @mousedown.stop>
    <div class="grey0 text-sm p-2">选择你想@的用户</div>
    <div class="px-2 grey1">关注列表</div>
    <el-scrollbar max-height="200px">
      <div v-infinite-scroll="next" :infinite-scroll-disabled="loading||error">
        <template v-for="i in list" :key="i.id">
          <div class="at-user-item" @click="$emit('select', i)">
            <user-avatar :user="i" :popover="false" size="40px" class="pointer-events-none"/>
            <div class="flex-1">
              <p class="text-sm maxline-1">{{ i.nickname }}</p>
              <p class="text-xs flex gap-2">
                <span>等级:{{ i.level }}</span>
                <span>粉丝:{{ displayNumber(i.fans) }}</span>
              </p>
            </div>
          </div>
        </template>
      </div>
      <p v-visible="loading" class="grey2 text-sm text-center">正在加载中...</p>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import { displayNumber } from '@/utils/common';
import userFollowApi from '@/apis/user/follow';
export type SimpleUser = Pick<User, 'id'|'nickname'>
defineEmits<{ select: [user: SimpleUser] }>()
const { list, next, loading, error } = usePage((pageNum)=>{
  return userFollowApi.getFollows({
    sort: 'ACCESS',
    pageNum,
    pageSize: 10,
  })
}, {
  immediate: [1]
})
</script>

<style scoped lang="scss">
.at-user-item {
  height: 50px;
  padding: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  column-gap: 6px;
  user-select: none;
  &:hover {
    background-color: #eee;
  }
}
</style>