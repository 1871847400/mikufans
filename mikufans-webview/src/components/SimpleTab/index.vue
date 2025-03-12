<template>
  <ul class="simple-tab">
    <template v-for="item in options" :key="getKey(item)">
      <li @click="selectTab(item)" :class="{active: isSelect(item), [type]: true}">{{ show(item) }}</li>
    </template>
  </ul>
</template>

<script setup lang="ts" generic="T">
const props = defineProps({
  options: array<T>().isRequired, //选项列表
  label: string<keyof T>().def('label'),
  value: string<keyof T>().def('value'),
  type: string<'button'|'text'>().def('button'),
})
const modelValue = defineModel('modelValue', { default: null })
const modelIndex = defineModel<number>('index', { default: 0 })
function selectTab(item: T) {
  if (typeof item === 'object') {
    modelValue.value = item[props.value]
  } else {
    modelValue.value = item
  }
  modelIndex.value = props.options.indexOf(item)
}
function getKey(item: T) {
  if (typeof item === 'object') {
    return item[props.value]
  }
  return item
}
modelValue.value = getKey(props.options[0])
function show(item: any) {
  return typeof item === 'object' ? item[props.label] : item
}
function isSelect(item: T) {
  if (typeof item === 'object') {
    return item[props.value] === modelValue.value
  }
  return item === modelValue.value
}
</script>

<style scoped lang="scss">
ul {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  li {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    user-select: none;
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    font-size: inherit;
    cursor: pointer;
    transition: all .3s;
    position: relative;
  }
  li.button {
    height: 40px;
    font-size: 18px;
    aspect-ratio: 2.5/1;
    border-radius: 5px;
    border: 2px solid transparent;
    background-color: #eee;
    border: 1px solid #ccc;
    &.active {
      background-color: var(--blue0);
      color: #fff;
    }
  }
  li.text {
    padding-bottom: 0.2em;
    /* padding-bottom: 6px; */
    border-bottom: 2px solid transparent;
    &.active {
      color: var(--blue0);
      border-color: var(--blue0);
      //实现滑块上的小三角
      &::before {
        content: '';
        background-color: var(--blue0);
        width: 6px;
        height: 4px;
        position: absolute;
        left: 50%;
        bottom: -0.5px;
        transform: translateX(-50%);
        -webkit-clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
        clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
      }
    }
  }
}
</style>