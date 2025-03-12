<template>
  <div class="menu-list shadow-lg">
    <div class="admin-title">Mikufans</div>
    <el-scrollbar>
      <el-menu :default-active="$route.path" router>
        <template v-for="child in (tree[0]?.children || [])">
          <menu-item :route="child"/>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { useRouteTree } from '@/router/generator';
import MenuItem from './MenuItem.vue';
const props = defineProps<{ basePath: string }>()
const { tree } = useRouteTree(props.basePath)
</script>

<style scoped lang="scss">
.menu-list {
  height: 100vh;
  max-height: 100vh;
  overflow: hidden;
  background-color: #fff;
  html.dark & {
    background-color: #2a2f3b;
  }
  display: flex;
  flex-direction: column;
  .el-menu {
    --el-menu-active-color: var(--blue0);
    :deep(.el-sub-menu .el-menu-item) {
      background-color: #f7f8f9;
    }
    html.dark & {
      --el-menu-text-color: #ccc;
      --el-menu-hover-text-color: #ccc;
      --el-menu-bg-color: #2a2f3b;
      /* --el-menu-hover-bg-color:  */
      :deep(.el-sub-menu .el-menu-item) {
        background-color: #313640;
      }
    }
  }
  .admin-title {
    text-align: center;
    padding: 15px 0;
    font-size: 18px;
    font-weight: 550;
    color: var(--pink0);
  }
}
</style>