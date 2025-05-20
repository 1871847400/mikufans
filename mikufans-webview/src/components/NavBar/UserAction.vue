<template>
  <div class="user-action">
    <div class="user-avatar-wrap">
      <div class="user-avatar" @mouseenter="updateUser">
        <user-avatar v-if="userStore.isLogin" :popover="false" size="52px"/>
        <div v-else class="avatar-login" @click="userStore.login()">登录</div>
      </div>
      <!-- 弹出的用户资料框 -->
      <div class="popover-user">
        <template v-if="userStore.isLogin">
          <div class="text-lg">{{ userStore.nickname }}</div>
          <svg-icon class="-m-2" :name="'level_'+userStore.level" :size="32"/>
          <div class="flex gap-2 justify-center text-xs grey1">
            <span>硬币:&nbsp;{{ userStore.coin }}</span>
            <span>经验:&nbsp;{{ userStore.exp }}<span v-if="userStore.nextExp">/{{ userStore.nextExp }}</span></span>
          </div>
          <div class="my-3 flex justify-evenly">
            <template v-for="{count, label, url} in statList" >
              <a class="flex flex-col items-center gap-1 text-button" :href="url" target="_blank">
                <span class="font-semibold text-lg">{{ count }}</span>
                <span class="grey1 text-[13px]">{{ label }}</span>
              </a>
            </template>
          </div>
          <el-divider/>
          <div class="logout" @click="userStore.logout()">退出登录</div>
        </template>
        <div v-else>
          <el-button type="primary" style="width: 100%;" @click="userStore.login()">点击登录</el-button>
          <div class="text-xs my-2">
            <span>还没有账号？</span>
            <el-button size="small" link><em @click="userStore.login(1)">点击注册</em></el-button>
          </div>
        </div>
      </div>
    </div>
    <nav-icon v-for="i in getNavIconOptions()" v-bind="i as any" />
    <router-link class="upload" @click="onClick" to="/upload" target="_blank" draggable="false">
      <svg-icon name="upload" :size="width>=1200?18:22" />
      <span v-if="width>=1200">投稿</span>
    </router-link>
  </div>
</template>

<script setup lang="ts">
import NavIcon from './NavIcon.vue';
import { getNavIconOptions } from './popover';
const { width } = useWindowSize()
const userStore = useUserStore()
const updateUser = useDebounceFn(()=>{
  userStore.getUserInfo(true)
}, 1000)
const statList = [
  {
    label: '关注',
    count: toRef(()=>userStore.follows),
    url: `/space/${userStore.id}/follow`
  },
  {
    label: '粉丝',
    count: toRef(()=>userStore.fans),
    url: `/space/${userStore.id}/fans`
  },
  {
    label: '动态',
    count: toRef(()=>userStore.dynamics),
    url: `/space/${userStore.id}/dynamic`
  },
]
function onClick(e: Event) {
  if (!userStore.isLogin) {
    e.preventDefault()
    userStore.login(0)
  }
}
</script>

<style scoped lang="scss">
.user-action {
  display: flex;
  justify-content: right;
  align-items: center;
}
.user-avatar-wrap {
  color: #000;
  margin: 0 24px;
  position: relative;
  z-index: 1;
  // user-select: none;
  .user-avatar {
    position: relative;
    transition: transform .3s;
    z-index: 2;
  }
  &:hover .user-avatar {
    transform: translate3d(-25%, 50%, 0) scale(1.5);
  }
  &:hover .popover-user {
    display: block;
  }
  .popover-user {
    color: var(--grey0);
    z-index: 1;
    display: none;
    position: absolute;
    background-color: var(--bg0);
    left: 25%;
    top: 100%;
    transform: translateX(-50%);
    box-sizing: border-box;
    width: 300px;
    box-shadow: 0 0 30px #0000001a;
    border-radius: 8px;
    padding: 8px;
    padding-top: 100%;
    text-align: center;
    .logout {
      user-select: none;
      cursor: pointer;
      width: 120px;
      margin: 16px auto;
      height: 32px;
      line-height: 32px;
      border-radius: 8px;
      &:hover {
        background-color: var(--bg1);
      }
    }
  }
}

.upload {
  padding: 0 20px;
  height: 34px;
  border: none;
  background-color: #fb7299;
  border-radius: 8px;
  font-size: 14px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
  cursor: pointer;
  gap: 4px;
  font-weight: 550;
  letter-spacing: 2px;
  &:hover {
    background-color: #e27392;
  }
}
.avatar-login {
  width: 42px;
  height: 42px;
  line-height: 42px;
  text-align: center;
  background-color: #00aeec;
  border-radius: 50%;
  color: #fff;
  font-weight: 550;
  user-select: none;
  cursor: pointer;
}
</style>