<template>
  <div class="user-search-item">
    <user-avatar class="mr-2" :user="user" size="64px"/>
    <div class="profile">
      <p class="flex items-center">
        <a class="nickname" :title="user.nickname" :href="'/space/'+user.id" v-html="user.highlightNickname||user.nickname"></a>
        <svg-icon :name="'level_'+user.level" :size="32"/>
      </p>
      <div class="text-xs grey2 flex gap-2">
        <span>关注:{{ displayNumber(user.follows) }}</span>
        <span>粉丝:{{ displayNumber(user.fans) }}</span>
        <span>投稿:{{ displayNumber(user.videos) }}</span>
      </div>
      <div class="maxline-1 text-xs grey2 max-w-[60%]" :title="user.sign">{{ user.sign }}</div>
      <user-follow-button v-if="!hideFollow" :user-id="user.id" :status="user.follow"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
const props = defineProps<{
  user: User,
  hideFollow?: boolean, //是否隐藏关注按钮
}>()
</script>

<style scoped lang="scss">
.user-search-item {
  display: flex;
  box-sizing: border-box;
  /* overflow: hidden; */
}
.profile {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-evenly;
  flex: 1;
  overflow: hidden;
  .nickname {
    font-size: 18px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 50%;
    margin-right: 12px;
    &:hover {
      color: #00AEEC;
    }
  }
}
.user-follow-button {
  position: absolute;
  right: 24px;
  top: 0;
}
</style>