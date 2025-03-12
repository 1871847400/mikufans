<template>
  <!-- 外部navbar仅仅占据空间 -->
  <nav class="navbar" :data-virtual="virtual">
    <div 
      class="navbar-inner" 
      ref="navbarRef" 
      :transparent="transparent && scrollY < 64"
    >
      <nav-area />
      <search-form v-if="search" />
      <user-action />
    </div>
  </nav>
</template>

<script setup lang="ts">
import NavArea from './NavArea.vue';
import SearchForm from './search/SearchForm.vue';
import UserAction from './UserAction.vue';
const props = defineProps({
  transparent: bool(),
  virtual: bool(),
  search: bool().def(true)
})
const navbarRef = ref<HTMLElement>()
const { y: scrollY } = useWindowScroll()
</script>

<style scoped lang="scss">
.navbar {
  width: 100%;
  height: var(--navbar-height);
  &[data-virtual=true] {
    height: 0;
  }
}
.navbar-inner {
  min-width: 1100px;
  max-width: 2048px;
  width: 100%;
  height: var(--navbar-height);
  min-height: var(--navbar-height);
  box-shadow: 0 2px 4px #00000014;
  top: 0;
  &[transparent=true] {
    /* 阴影方便显示按钮 */
    background: linear-gradient(180deg, rgba(0,0,0,.4), rgba(0,0,0,.3), rgba(0,0,0,.05));
    /* background: transparent; */
    color: #fff;
    box-shadow: none;
  }
  transition: background-color 0.5s;
  background: var(--bg0);
  position: fixed; //相对于window定位,宽高需单独设置
  display: flex;
  align-items: center;
  box-sizing: border-box;
  z-index: 100;
  justify-content: space-between;
  /*
    100vw=win宽度(含滚动条) 100%=可用宽度(不含滚动条)
    相减后：有滚动条时为 -17px 没有滚动条时为0
  */
  padding-right: calc(100% - 100vw + 36px); 
  padding-left: 36px;
}
.nav-area {
  flex: 1;
}
.user-action {
  flex: 1;
}
.shadow {
  box-shadow: 0 0 1px rgba(0,0,0,.3);
}
</style>