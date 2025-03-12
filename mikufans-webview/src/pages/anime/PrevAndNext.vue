<template>
  <i v-if="page>1" @click="prev" class="prev iconfont icon-zuojiantou"></i>
  <i v-if="!done" @click="next" class="next iconfont icon-youjiantou"></i>
</template>

<script setup lang="ts">
defineProps({
  page: number().def(0),
  done: bool().def(true),
  offset: string().def('0px')
})
const emits = defineEmits<{ next:[], prev: [] }>()
const model = defineModel<string>()
function prev() {
  model.value = 'left'
  nextTick(()=>{
    emits('prev')
  })
}
function next() {
  model.value = 'right'
  nextTick(()=>{
    emits('next')
  })
}
</script>

<style scoped lang="scss">
.prev, .next {
  position: absolute;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: var(--bg1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, .4);
  cursor: pointer;
  user-select: none;
  transition: scale .3s;
  top: calc(50% - v-bind(offset));
  z-index: 10;
  &:hover {
    scale: 1.1;
  }
  &::before {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%);
  }
}
.next {
  right: 0;
  transform: translateX(50%) translateY(-50%);
}
.prev {
  left: 0;
  transform: translateX(-50%) translateY(-50%);
}
</style>