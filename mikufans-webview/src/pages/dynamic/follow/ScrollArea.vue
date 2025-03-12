<template>
  <div class="relative bg0 p-2 rounded-lg overflow-hidden">
    <div
      ref="el"
      class="flex gap-3 py-1 overflow-hidden"
      @scrollend="scrollend"
      v-resize="compute"
    >
      <slot></slot>
    </div>
    <transition>
      <div class="before" v-if="hasPrevious">
        <div class="button" @click="previous">
          <i class="iconfont icon-zuojiantou"></i>
        </div>
      </div>
    </transition>
    <transition>
      <div class="after" v-if="hasNext">
        <div class="button" @click="next">
          <i class="iconfont icon-youjiantou"></i>
        </div>
      </div>
    </transition>
  </div>
</template>
<!-- 使用本组件必须保证被包裹的slot(子元素)大小,数量不会变了 -->
<script setup lang="ts">
const el = ref<HTMLElement>()
let scrolling = false;
const hasPrevious = ref(false)
const hasNext = ref(false)
function compute() {
  //已滚动距离
  const scrollLeft = el.value.scrollLeft
  //最大可滚动距离
  const maxScrollLeft = el.value.scrollWidth - el.value.clientWidth
  hasNext.value = Math.abs(scrollLeft - maxScrollLeft) > 1
  hasPrevious.value = scrollLeft > 0
}
onMounted(compute)
function previous() {
  if (scrolling || !hasPrevious.value) {
    return;
  }
  scrolling = true;
  el.value.scrollBy({
    behavior: "smooth",
    left: -el.value.clientWidth,
  })
}
function next() {
  if (scrolling || !hasNext.value) {
    return;
  }
  scrolling = true;
  el.value.scrollBy({
    behavior: "smooth",
    left: el.value.clientWidth,
  })
}
function scrollend() {
  scrolling = false;
  compute()
}
</script>

<style scoped lang="scss">
.before, .after {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 45px;
  display: flex;
  justify-content: center;
  align-items: center;
  &::before {
    content: "";
    position: absolute;
    inset: 0;
    background: linear-gradient(270deg, #fff, #ffffffa2);
    filter: blur(10px);
  }
  .button {
    z-index: 1;
    cursor: pointer;
    user-select: none;
    background-color: var(--bg0);
    border: 1px solid #ccc;
    border-radius: 50%;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    .iconfont {
      font-size: 12px;
    }
  }
}
.after {
  left: unset;
  right: 0;
  &::before {
    background: linear-gradient(90deg, #fff, #ffffffa2) !important;
  }
}
</style>

<style scoped lang="scss">
.v-enter-active,
.v-leave-active {
  pointer-events: none;
  transition: all 0.2s linear;
}
.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>