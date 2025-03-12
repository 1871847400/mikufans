<template>
  <div class="selection" ref="boxRef">
    <slider title="片段开始位置" ref="leftRef" extend="5px" v-model="start" :max="end-0.01" @change="change"/>
    <slider title="片段结束位置" ref="rightRef" extend="5px" v-model="end" :min="start+0.01" @change="change"/>
    <div class="mask"></div>
  </div>
</template>

<script setup lang="ts">
import { PropType, computed, onMounted, ref, watch } from 'vue'
import Slider from './Slider.vue'
const props = defineProps({
  modelValue: Object as PropType<[number, number]>,
})
watch(()=>props.modelValue, newModel=>{
  if (newModel) {
    start.value = newModel[0]
    end.value = newModel[1]
    requestAnimationFrame(()=>{
      update()
    })
  }
}, { deep:true })
const emits = defineEmits(['update:modelValue'])
const start = ref(props.modelValue?.[0] ?? 0)
const end = ref(props.modelValue?.[1] ?? 1)
const boxRef = ref<HTMLElement>()
const leftRef = ref()
const rightRef = ref()
const left = ref('0')
const right = ref('100%')
function change() {
  update()
  emits('update:modelValue', [start.value, end.value])
}
function update() {
  left.value = getComputedStyle(leftRef.value.element()).left
  let r = getComputedStyle(rightRef.value.element()).left
  right.value = `calc(${r} - ${left.value})`
}
onMounted(update)
</script>

<style scoped lang="scss">
.selection {
}
.slider {
  cursor: col-resize;
  width: 8px;
  background-color: #a0cfff;
  &:hover {
    background-color: #b0d1f2;
  }
}
.mask {
  left: v-bind(left);
  width: v-bind(right);
  top: 0;
  bottom: 0;
  position: absolute;
  background-color: var(--blue-1);
  opacity: 0.25;
}
</style>