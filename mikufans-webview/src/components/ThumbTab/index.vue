<template>
  <ul ref="listRef" class="thumb-tab">
    <template v-for="option, idx in options">
      <li 
        @click="beforeChange(option, $event)" 
        :class="{ active: model==option.value }" 
        @mouseenter="automove && changePositionDebounce(idx)" 
        @mouseleave="automove && changePositionDebounce(index)"
        v-resize="onResize"
      >
        <slot v-bind="option">
          <span>{{ option.label }}</span>
        </slot>
      </li>
    </template>
    <div ref="thumbRef" class="thumb"></div>
  </ul>
</template>

<script setup lang="ts" generic="T extends Option">
export interface Option {
  label: string
  value: string|number
}
const props = defineProps({
  options: array<T>().def([]),
  automove: bool().def(false), //跟随鼠标移动
  thumbStyle: object().def({}),
})
const emits = defineEmits<{ beforechange: [e: MouseEvent] }>()
const model = defineModel<Option['value']>('modelValue', { default: '' })
const listRef = useTemplateRef('listRef')
const thumbRef = useTemplateRef('thumbRef')
function changePosition(idx: number) {
  const list = listRef.value
  const thumb = thumbRef.value
  const nodes = Array.from(list.childNodes)
  const itemList = nodes.filter(li=>li instanceof HTMLLIElement)
  if (idx < 0 || idx >= itemList.length) {
    thumb.style.display = 'none'
  } else {
    const li = itemList[idx]
    thumb.style.left = li.offsetLeft + 'px'
    thumb.style.width = li.offsetWidth + 'px'
    thumb.style.display = 'block'
  }
  for (const key in props.thumbStyle) {
    thumb.style[key] = props.thumbStyle[key]
  }
}
const index = computed(()=>props.options.findIndex(i=>i.value===model.value))
//设置防抖以免thumb来回闪动
const changePositionDebounce = useDebounceFn(changePosition, 200)
onMounted(()=>{
  watchEffect(()=>{
    changePositionDebounce(index.value)
  })
})
function beforeChange(option: Option, e: MouseEvent) {
  emits('beforechange', e)
  if (!e.defaultPrevented) {
    model.value = option.value
  }
}
function onResize() {
  //防止选项大小发生变化后对不齐
  changePosition(index.value)
}
</script>

<style scoped lang="scss">
ul.thumb-tab {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  gap: 16px;
  min-height: 40px;
  /* height: 60px; 交给外部设置 */
  position: relative;
  & > li {
    user-select: none;
    cursor: pointer;
    padding: 4px;
    &.active {
      color: var(--blue1);
      font-weight: 550;
    }
  }
  & > .thumb {
    width: 0px;
    height: 3px;
    transition: left .2s;
    left: 0;
    bottom: 0;
    position: absolute;
    background-color: var(--blue1);
  }
}
</style>