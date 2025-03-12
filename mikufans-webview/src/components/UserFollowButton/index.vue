<template>
  <button 
    class="user-follow-button iconfont"
    :class="{'icon-tianjia':followed=='UNFOLLOWED'}"
    :data-ready="ready"
    :data-active="followed!='UNFOLLOWED'"
    @click.prevent="follow"
  >
    {{ showMsg }}
  </button>
</template>

<script setup lang="ts">
import userFollowApi from '@/apis/user/follow';
import { useAsyncFn } from '@/hooks/useAsyncFn';
import { displayNumber } from '@/utils/common';
import { emitGlobalEvent, useGlobalEvent } from '@/hooks/mitt';
const userStore = useUserStore()
const { userId, fans, status } = defineProps<{
  userId: string,
  fans?: number //显示粉丝数量
  status?: UserFollowStatus //关注状态,如果未指定则获取
}>()
const followed = ref<UserFollowStatus>(status || 'UNFOLLOWED')
const ready = ref(false) //控制按钮是否能点击
async function init() {
  if (userStore.isLogin && !status) {
    ready.value = false
    if (userId) {
      followed.value = (await userFollowApi.getStatus(userId))
    } else {
      followed.value = 'UNFOLLOWED'
    }
  }
  ready.value = true
}
watchImmediate(()=>userId, init)
const follow = useAsyncFn(async ()=>{
  if (!userStore.isLogin) {
    return userStore.login()
  }
  if (!ready.value) {
    return
  }
  if (followed.value!='UNFOLLOWED') {
    await userFollowApi.unfollow(userId)
    followed.value = 'UNFOLLOWED'
    message.info('已取消关注')
  } else {
    followed.value = await userFollowApi.follow(userId)
    message.success('关注成功！')
  }
  emitGlobalEvent('userFollow', { userId, follow: followed.value })
})
useGlobalEvent('userFollow', ({ userId: eventUserId, follow })=>{
  if (userId === eventUserId) {
    followed.value = follow
  }
})
const showMsg = computed(()=>{
  if (followed.value === 'UNFOLLOWED') {
    return '关注'
  }
  let str = followed.value == 'EACH_FOLLOWED' ? '已互粉' : '已关注'
  //如果粉丝数大于0，则额外显示粉丝量
  if (fans) {
    str += ' ' + displayNumber(fans)
  }
  return str
})
</script>

<style scoped lang="scss">
.user-follow-button {
  color: #fff;
  box-sizing: border-box;
  width: 90px;
  height: 30px;
  line-height: 26px; //行高不包括border
  border-radius: 4px;
  white-space: nowrap;
  transition: all .3s;
  cursor: pointer;
  background-color: var(--blue0);
  border: 2px solid var(--blue0);
  &:hover {
    background-color: var(--blue0-hover);
    border: 2px solid var(--blue1);
  }
  &::before {
    padding-right: 4px;
  }
  &[data-ready=true] {
    cursor: pointer;
  }
  &[data-active=true] {
    color: var(--grey2);
    background-color: var(--bg2);
    border: 2px solid var(--bg2);
    &:hover {
      background-color: var(--bg1);
      border: 2px solid var(--bg2);
    }
  }
}
</style>