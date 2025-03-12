<template>
  <form class="search-form" @submit.prevent="onSearch" ref="formRef">
    <div class="search-border">
      <div class="search-box">
        <div class="search-content">
          <slot name="prefix-icon"></slot>
          <input
            class="search-input"
            ref="inputRef"
            placeholder="输入关键字搜索" 
            type="text" 
            v-model.trim="keyword" 
            maxlength="64"
            @keydown="keydown"
          >
          <slot name="suffix-icon">
            <i v-if="keyword" class="iconfont clear" @click="keyword=''">&#xe648;</i>
          </slot>
        </div>
        <slot name="submit">
          <button class="iconfont submit" type="submit" title="搜索" tabindex="-1">&#xe7b3;</button>
        </slot>
      </div>
      <template v-if="!keyword||tipList.length>0"> <!-- 防止空白内容凸起一部分 -->
        <drop-box class="hidden"/>
      </template>
    </div>
  </form>
</template>

<script setup lang="ts">
import DropBox from './DropBox.vue'
const { search } = useSearchStore()
const { keyword, tipList, tipIndex, inputRef } = storeToRefs(useSearchStore())
const formRef = ref<HTMLFormElement>()
const props = defineProps({
  searchFunc: func(),
})
function keydown(e: KeyboardEvent) {
  let offset = 0
  if (e.code === 'ArrowUp') {
    offset = -1
  } else if (e.code === 'ArrowDown') {
    offset = 1
  }
  if (offset) {
    e.preventDefault()
    const newIndex = tipIndex.value + offset
    if (newIndex >= tipList.value.length) {
      tipIndex.value = -1
    } else if(newIndex < -1) {
      tipIndex.value = tipList.value.length - 1
    } else {
      tipIndex.value = newIndex
    }
  }
}
function onSearch() {
  if (props.searchFunc) {
    props.searchFunc()
  } else {
    search()
  }
}
</script>

<style scoped lang="scss">
.search-form {
  position: relative;
  flex: 1 1 0;
  margin: 0 16px;
  height: 40px;
  color: var(--grey0); //防止继承到navbar的颜色
}
.search-border {
  position: absolute;
  box-sizing: border-box;
  left: 0;
  right: 0;
  top: 0;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--bg2);
  transition: border .3s;
  &:focus-within {
    background-color: var(--bg0);
    .search-box {
      opacity: 1;
    }
    .drop-box {
      display: block;
    }
    .search-content {
      /* background-color: #e3e5e7; */
      background-color: var(--bg1);
    }
  }
}
.search-box {
  display: flex;
  align-items: center;
  border-radius: 8px;
  background-color: var(--bg0);
  padding: 5px;
  opacity: .85;
  transition: opacity .2s;
  &:hover {
    opacity: 1;
  }
  .navbar-inner[transparent=false] &:not(&:focus-within) {
    /* background-color: #f0f0f0; */
  }
}
.search-content {
  height: 30px;
  border-radius: 8px;
  flex: 1;
  padding: 0 8px;
  display: flex;
  align-items: center;
  input.search-input {
    border: none;
    outline: none;
    background: none;
    font-size: 14px;
    flex: 1;
    height: 100%; //增加点击区域
  }
  i.clear {
    color: var(--grey2);
    cursor: pointer;
    padding: 0 4px;
    &:hover {
      color: var(--grey1);
    }
  }
}
button.submit {
  width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  white-space: nowrap;
  border-radius: 6px;
  margin-left: 5px;
  border: none;
  outline: none;
  background: none;
  cursor: pointer;
  font-size: 24px;
  &:hover {
    background-color: var(--bg1);
  }
}
</style>