<template>
  <div class="topbar">
    <span class="cursor-pointer mr-5" @click="model=!model">
      <i class="iconfont icon-gengduo4 text-[22px]"></i>
    </span>
    <el-breadcrumb separator="/">
      <template v-for="i in $route.matched" :key="i.path">
        <el-breadcrumb-item :to="i.path">{{ i.meta?.title }}</el-breadcrumb-item>
      </template>
    </el-breadcrumb>
    <span class="mr-auto"></span>
    <el-dropdown trigger="hover" placement="bottom-start" @command="handlers[$event]()">
      <div class="user-avatar-box">
        <miku-image class="size-[40px] mr-1 object-cover" :res-id="sysUserStore.avatarId" avatar />
        <span>{{ sysUserStore.username }}</span>
        <i class="iconfont icon-zhankai"></i>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
  </el-dropdown>
  </div>
</template>

<script setup lang="ts">
// import UserAvatar from '@/assets/images/avatar.jpeg'
const model = defineModel('modelValue', { default: true })
const router = useRouter()
const sysUserStore = useSysUserStore()
const handlers = {
  async logout() {
    sysUserStore.logout()
    await router.push('/admin/login')
    message.success('退出成功')
  }
}
</script>

<style scoped lang="scss">
.topbar {
  display: flex;
  align-items: center;
  padding: 4px 8px;
  background-color: var(--bg0);
}
.user-avatar-box {
  padding: 8px;
  display: flex;
  align-items: center;
  user-select: none;
  cursor: pointer;
  .iconfont {
    font-size: 12px;
    transform: translateY(1.5px);
  }
}
</style>