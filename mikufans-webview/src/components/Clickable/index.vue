<template>
  <slot></slot>
</template>

<script setup lang="ts">
import { formContextKey } from 'element-plus';
//获取当前最近的el-form的实例
const emits = defineEmits<{ click: [MouseEvent] }>()
const props = defineProps<{ disabled?: boolean }>()
const formInstance = inject(formContextKey, null)
const disabled = computed(()=>!!formInstance?.disabled || props.disabled)
const el = useCurrentElement()
let target : HTMLElement = null
onMounted(()=>{
  if (el.value instanceof HTMLElement) {
    target = el.value
  } else if (el.value instanceof Text) {
    if (el.value.nextElementSibling instanceof HTMLElement) {
      //插槽内第一个元素
      target = el.value.nextElementSibling
    }
  }
  if (target == null) {
    throw new Error('没有找到元素')
  }
  target.classList.add('clickable')
  target.addEventListener('click', e=>{
    if (!disabled.value) {
      emits('click', e)
    }
  })
  watchImmediate(disabled, ()=>{
    if (disabled.value) {
      target.classList.add('disabled')
      target.setAttribute('disabled', '')
    } else {
      target.classList.remove('disabled')
      target.removeAttribute('disabled')
    }
  })
})
</script>

<style lang="scss">
.clickable {
  &:not(.disabled) {
    cursor: pointer;
  }
  &.disabled {
    cursor: not-allowed;
  }
}
</style>