<template>
  <transition :name="anim">
    <span class="number" :key="number">{{ displayNumber(Math.max(0, number)) }}</span>
  </transition>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
import { isNumber } from 'lodash';
const props = defineProps<{ number: number }>()
const anim = ref('')
watch(()=>props.number, (newVal, oldVal)=>{
  if (isNumber(oldVal)) {
    if (newVal > oldVal) {
      anim.value = 'plus'
    } else {
      anim.value = 'minus'
    }
  }
})
</script>

<style scoped>
.plus-enter-active,
.plus-leave-active {
  transition: all 0.5s ease;
}
.plus-enter-from {
  opacity: 0;
  transform: translateY(-35px);
}
.plus-leave-to {
  opacity: 0;
  transform: translateY(25px);
}
</style>

<style scoped>
.minus-enter-active,
.minus-leave-active {
  transition: all 0.5s ease;
}
.minus-enter-from {
  opacity: 0;
  transform: translateY(35px);
}
.minus-leave-to {
  opacity: 0;
  transform: translateY(-25px);
}
</style>