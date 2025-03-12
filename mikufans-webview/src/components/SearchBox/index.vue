<template>
  <div class="search-box" :class="{ stretch }">
    <form class="search-container" @submit.prevent="search">
      <button class="iconfont search-icon" type="submit">&#xe7b3;</button>
      <input ref="inputRef" v-model="value" type="text" :placeholder="placeholder" :maxlength="maxlength">
      <i class="iconfont search-clear" :style="{visibility:value?'visible':'hidden'}" @click="clear" title="清空">&#xe648;</i>
    </form>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  placeholder: string().def('请输入关键词...'),
  maxlength: oneOfType([number(), string()]),
  debounce: number().def(0), //更新model时防抖
  stretch: bool().def(true), //宽度拉伸动画
})
const inputRef = ref<HTMLInputElement>()
const emits = defineEmits<{ search: [string], clear: [string] }>()
const model = defineModel('modelValue', { default: '' })
const value = ref(model.value)
watchImmediate(model, ()=>{
  value.value = model.value
})
watchDebounced(value, ()=>{
  model.value = value.value
}, {
  debounce: props.debounce
})
function search() {
  emits('search', value.value)
  inputRef.value.focus()
  model.value = value.value
}
function clear() {
  emits('clear', value.value)
  value.value = ''
  model.value = ''
}
</script>

<style scoped lang="scss">
.search-box {
  box-sizing: border-box;
  /* padding: 0 12px; */
  width: 240px;
  height: auto;
  transition: all 0.2s;
  &.stretch {
    width: 140px;
    &:focus-within {
      width: 240px;
    }
  }
  .search-container {
    display: flex;
    align-items: center;
    border-radius: 16px;
    border: 1px solid #ccc;
    font-size: 14px;
    padding: 4px 0;
    .search-icon, 
    .search-clear {
      padding: 0 4px;
      color: #999;
      cursor: pointer;
      margin-right: 4px;
      &:hover {
        color: #666;
      }
    }
    .search-icon {
      font-size: 22px;
      border: none;
      outline: none;
      background: none;
    }
    input {
      outline: none;
      border: none;
      background: none;
      width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      &::placeholder {
        color: #999;
      }
    }
  }
}
</style>