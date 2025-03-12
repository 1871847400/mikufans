<template>
  <div class="rounded-lg overflow-hidden">
    <el-tabs tab-position="left" style="height: 100%;" type="border-card">
      <template v-for="{ label, component } in paneList">
        <el-tab-pane :label="label" lazy>
          <div class="page-title">{{ label }}</div>
          <el-divider class="my-4"/>
          <component :is="component" />
        </el-tab-pane>
      </template>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import ProfilePane from './profile/index.vue'
import SettingPane from './setting/index.vue'
import SafePane from './safe/index.vue'
const { isSelf, changePage } = useSpaceStore()
const paneList = [
  { label: '基本信息', component: ProfilePane },
  { label: '隐私设置', component: SettingPane },
  { label: '账号安全', component: SafePane },
]
//该页面只能登录过后并且是自己才能访问
if (!isSelf) {
  changePage('')
}
</script>

<style scoped lang="scss">
:deep(.el-tabs__item) {
  width: 160px;
  font-size: 16px;
  justify-content: center !important;
  text-align: center !important;
  --el-color-primary: var(--grey1);
  --el-bg-color-overlay: var(--blue1);
  --el-tabs-header-height: 50px;
  &.is-active {
    --el-color-primary: #fff;
  }
}
.page-title {
  color: var(--blue0);
  display: flex;
  align-items: center;
  &::before {
    content: '';
    width: 4px;
    height: 1em;
    margin-right: 4px;
    background-color: var(--blue0);
    border-radius: 4px;
  }
}
</style>