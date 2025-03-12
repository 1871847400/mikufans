<template>
  <div class="bottom-bar">
    <theme-select />
    <transition name="gotop">
      <button class="go-top" title="回到顶部" @click.prevent="scrollToTop" v-if="y>500">
        <i class="iconfont icon-shouqi"></i>
        <div>顶部</div>
      </button>
    </transition>
  </div>
</template>

<script setup lang="ts">
const { y } = useWindowScroll()
function scrollToTop() {
  scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}
</script>

<style scoped lang="scss">
.bottom-bar {
  position: fixed;
  bottom: 24px;
  right: 10px;
  display: flex;
  flex-direction: column;
  row-gap: 12px;
  z-index: 3;
  .go-top {
    /* min-width: 36px;
    min-height: 40px; */
    /* padding: 1px 4px; */
    text-align: center;
    font-size: 12px;
    line-height: 1.3;
    cursor: pointer;
    border-radius: 8px;
    background-color: var(--bg0);
    border: 1px solid var(--bg2);
    &:hover {
      background-color: var(--bg1);
    }
    .iconfont {
      font-size: 12px;
    }
  }
}
</style>

<style scoped>
.gotop-enter-active,
.gotop-leave-active {
  transition: all 0.3s ease;
}
.gotop-enter-from,
.gotop-leave-to {
  opacity: 0;
  transform: translateY(50px);
}
</style>