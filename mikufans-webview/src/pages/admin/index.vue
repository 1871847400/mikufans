<template>
  <div class="admin-page">
    <transition>
      <aside v-if="expand">
        <menu-list base-path="/admin" />
      </aside>
    </transition>
    <main class="bg1 flex-1">
      <top-bar v-model="expand" />
      <div class="p-2">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import TopBar from './topbar/index.vue'
const expand = ref(true)
const router = useRouter()
if (!useSysUserStore().isLogin) {
  router.replace('/admin/login')
}
</script>

<style scoped lang="scss">
.admin-page {
  display: flex;
  min-height: 100vh;
  max-width: 100%;
  :deep(.el-table) {
    min-height: 400px;
  }
  :deep(.search-bar) {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    margin-bottom: 16px;
    column-gap: 16px;
    background-color: var(--bg0);
    padding: 0 16px;
    padding-top: 12px;
    border-radius: 6px;
  }
  :deep(.el-input) {
    --el-input-width: 160px;
  }
  :deep(.el-select) {
    --el-select-width: 160px;
  }
}
aside {
  width: 200px;
  flex-shrink: 0;
  .menu-list {
    width: 200px;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 2;
  }
}
.v-enter-active,
.v-leave-active {
  transition: width .3s ease;
  .menu-list {
    display: none;
  }
}
.v-enter-from,
.v-leave-to {
  width: 0;
}
</style>