<template>
  <div ref="container" 
    class="sortable" 
    draggable="false"
    @dragstart="dragstart" 
    @dragenter="dragenter"
    @dragover="dragover" 
    @dragend="dragend"
  >
    <transition-group name="list">
      <template v-for="item,index in modelValue.filter(a=>!!a)" :key="item[itemKey]">
        <slot :index="index" :item="item"></slot>
      </template>
    </transition-group>
  </div>
</template>

<script setup lang="ts" generic="T">
import { getParentByClass, isChildElement } from '@/utils/dom'
const props = defineProps({
  modelValue: array<T>().def([]),
  itemKey: any<keyof T>().isRequired,
  sortable: func<(item: T)=>boolean>().def(()=>true)
})
const emits = defineEmits<{
  'update:modelValue': [value: T[]],
  change: [sourceIndex: number, curIndex: number]
}>()
defineSlots<{
  default(props: { index: number, item: T }): any
}>()
const container = ref<HTMLElement>()
watch(()=>props.modelValue, ()=>{
  nextTick(()=>{
    const children = Array.from(container.value?.children ?? [])
    for (const element of children) {
      if (element instanceof HTMLElement) {
        element.draggable = true
        element.classList.add('draggable-item')
      }
    }
  })
}, { deep: true, flush: 'post', immediate: true })
let startIndex, sourceNode, sourceIndex, targetNode : HTMLElement, targetIndex
function dragstart(e: DragEvent) {
  if (e.target instanceof HTMLElement) {
    const target = e.target as HTMLElement
    //如果当前焦点在输入框,则不触发拖动,否则会无法选中输入框的文本
    if (document.activeElement?.tagName === 'INPUT') {
      if (isChildElement(document.activeElement, target)) {
        return e.preventDefault()
      }
    }
    const children = Array.from(container.value.children)
    const index = children.indexOf(target)
    //判断该item是否支持排序
    if (!props.sortable(props.modelValue[index])) {
      return e.preventDefault()
    }
    //给容器添加上正在拖拽中的标记
    container.value.classList.add('dragging')
    setTimeout(() => {
      //异步添加class防止拖动产生的元素样式被改变
      target.classList.add('moving')
    }, 0);
    if (e.dataTransfer) {
      e.dataTransfer.effectAllowed = 'move'
    }
    startIndex = sourceIndex = index
    sourceNode = target
  }
}
function dragover(e: DragEvent) {
  e.preventDefault()
}
function dragenter(e: DragEvent) {
  e.preventDefault();
  if (!(e.target instanceof HTMLElement) || e.target === container.value) return
  targetNode = e.target
  if (!targetNode.classList.contains('draggable-item')) {
    // 防止孙节点抢先子节点
    targetNode = getParentByClass(targetNode, 'draggable-item')
  }
  //可能enter到拖拽node本身
  if (targetNode === sourceNode) return
  // console.log(targetNode, targetNode.textContent);
  const children = Array.from(container.value.children)
  sourceIndex = children.indexOf(sourceNode)
  targetIndex = children.indexOf(targetNode)
  //如果不能换顺序
  if (!props.sortable(props.modelValue[targetIndex])) {
    return
  }
  // console.log(targetNode.textContent, startIndex, sourceIndex, targetIndex);
  if (sourceIndex !== -1 && targetIndex !== -1) {
    const newArr = [...props.modelValue]
    const tmp = newArr[sourceIndex]
    newArr[sourceIndex] = newArr[targetIndex]
    newArr[targetIndex] = tmp
    emits('update:modelValue', newArr)
  }
}
function dragend() {
  container.value.classList.remove('dragging')
  if (sourceNode) {
    sourceNode.classList.remove('moving')
  }
  const children = Array.from(container.value.children)
  const curIndex = children.indexOf(sourceNode)
  if (startIndex !== curIndex) {
    emits('change', startIndex, curIndex)
  }
}
</script>

<style scoped lang="scss">
.sortable {
  :deep(.moving) {
    background: transparent!important;
    color: transparent!important;
    outline: transparent!important;
    border: transparent!important;
    /* border: 1px dashed #ccc; */
  }
  /* 对移动中的元素应用的过渡 */
  :deep(
    .list-move,
    .list-enter-active,
    .list-leave-active
  )
   {
    transition: all 0.3s ease;
  }
}
</style>