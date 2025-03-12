<template>
  <scroll-area class="mt-2" v-if="isReady">
    <template v-for="i in [fakeUser, ...list]" :key="i.id">
      <div
        class="user-item"
        :active="activeUser?.id === i.id"
        :news="i.news"
        @click="activeUser = i"
      >
        <el-avatar 
          v-if="i.id===fakeUser.id" 
          :src="DynamicImage" 
          class="size-[50px] pointer-events-none"
        />
        <user-avatar
          v-else
          :user="i"
          size="50px"
          :popover="false"
          class="pointer-events-none"
        />
        <div class="nickname text-sm w-[68px] maxline-2 text-center">
          {{ i.nickname }}
        </div>
      </div>
    </template>
  </scroll-area>
</template>

<script setup lang="ts">
import DynamicImage from '@/assets/images/dynamic.png'
import ScrollArea from './ScrollArea.vue';
import userFollowApi from '@/apis/user/follow';
const { fakeUser, activeUser } = toRefs(useDynamicStore());
const { state: list, isReady } = useAsyncState(
  userFollowApi.getFollows({
    sort: 'LAST_DYNAMIC',
    pageNum: 1,
    pageSize: 25
  }).then(page=>page.records)
, [])
watch(activeUser, (user)=>{
  user.news = false
})
</script>

<style scoped lang="scss">
.user-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  position: relative;
  &:hover,
  &[active="true"] {
    :deep(.user-avatar), .el-avatar {
      border-radius: 50%;
      outline: 2px solid var(--blue0);
    }
    .nickname {
      color: var(--blue0);
    }
  }
  &[news=true]::after {
    content: '';
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: var(--blue0);
    border: 1px solid var(--bg0);
    position: absolute;
    right: 8px;
    top: 40px;
  }
}
</style>
