<template>
  <div class="pac-man" :class="{ active, [dir]: true }">
    <div class="before"></div>
    <div class="after"></div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  active: bool().def(true),
  dir: string<'left'|'right'>().def('right'),
  color: string().def('#eee'),
  activeColor: string().def('#fff')
})
</script>

<style scoped lang="scss">
@keyframes play {
  0% {
    rotate: 0;
  }
  100% {
    rotate: 45deg;
  }
}
@keyframes play2 {
  0% {
    rotate: 0;
  }
  100% {
    rotate: -45deg;
  }
}
.pac-man {
  width: 64px;
  height: auto;
  aspect-ratio: 1;
  position: relative;
  border-radius: 50%;
  overflow: hidden;
  background-color: v-bind(color);
  &.active {
    background-color: transparent;
    &.left {
      .before {
        animation-name: play!important;
      }
      .after {
        animation-name: play2!important;
      }
    }
    &.right {
      .before {
        animation-name: play2!important;
      }
      .after {
        animation-name: play!important;
      }
    }
    .before, .after {
      position: absolute;
      inset: 0;
      animation: none 0.3s linear 0s 4 alternate;
      background-color: v-bind(activeColor);
    }
    .before {
      /* 这个值决定开口大小 */
      transform: translateY(-45%);
    }
    .after {
      transform: translateY(45%);
    }
  }
}
</style>