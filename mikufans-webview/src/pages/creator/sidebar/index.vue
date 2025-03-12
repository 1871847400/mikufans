<template>
  <div class="sidebar">
    <el-scrollbar>
      <div class="pb-6">
        <div class="text-xl text-center font-bold mb-3">创作中心</div>
        <el-menu mode="vertical" router :default-active="$route.path">
          <template v-for="{ label, path, icon, children } in menus">
            <template v-if="children">
              <el-sub-menu :index="path">
                <template #title>
                  <div class="px-3">
                    <i class="iconfont mr-3" :class="icon"></i>
                    <span>{{ label }}</span>
                  </div>
                </template>
                <template v-for="{ label, path } in children">
                  <el-menu-item :index="path">
                    <div class="pl-5 ">{{ label }}</div>
                  </el-menu-item>
                </template>
              </el-sub-menu>
            </template>
            <template v-else>
              <el-menu-item :index="path">
              <div class="px-3">
                <i class="iconfont mr-3" :class="icon"></i>
                <span>{{ label }}</span>
              </div>
            </el-menu-item>
            </template>
          </template>
        </el-menu>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
const menus = [
  {
    label: '首页',
    icon: 'icon-shouye',
    path: '/creator',
  },
  {
    label: '我要投稿',
    icon: 'icon-shangchuan1',
    path: '/creator/upload',
    children: [
      {
        label: '视频投稿',
        path: '/creator/upload/video'
      },
      {
        label: '图文投稿',
        path: '/creator/upload/article'
      },
    ]
  },
  {
    label: '内容管理',
    icon: 'icon-fenlei',
    path: '/creator/content',
    children: [
      {
        label: '视频管理',
        path: '/creator/content/video'
      },
      {
        label: '图文管理',
        path: '/creator/content/article'
      },
    ]
  },
  {
    label: '数据中心',
    icon: 'icon-shezhi',
    path: '/creator/data',
  },
]
</script>

<style scoped lang="scss">
.sidebar {
  width: 190px;
  /* position: sticky; */
  /* top: var(--navbar-height); */
  position: fixed;
  left: 0;
  background-color: var(--bg0);
  height: 100%;
  z-index: 1;
}
.el-menu {
  border-right: none;
}
</style>
<style>
/* 调整二级菜单的颜色 */
.sidebar .el-sub-menu .el-menu-item:not(.is-active) {
  color: var(--grey2);
}
</style>