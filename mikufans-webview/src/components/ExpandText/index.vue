<template>
  <div class="expand-text">
    <div v-if="html" ref="el" class="content" v-html="html"></div>
    <div v-else ref="el" class="content">
      <slot></slot>
    </div>
    <div v-if="overflow">
      <span class="cursor-pointer select-none blue0" @click="__expand=!__expand">{{ __expand ? '收起' : '展开' }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  rows: number().def(5),
  html: string(),
})
const el = ref<HTMLElement>()
const overflow = ref(false)
useMutationObserver(el, calcOverflow, { 
  childList: true,
  subtree: true,
  characterData: true,
})
onMounted(calcOverflow)
//是否展开了
const __expand = ref(false)
//判断是否有溢出
function calcOverflow() {
  overflow.value = el.value && el.value.scrollHeight - el.value.clientHeight > 0.1
}
//显示行数
const __rows = computed(()=>{
  return (props.rows == -1 || __expand.value) ? 99999 : props.rows
})
</script>

<style scoped lang="scss">
.content {
  display: block;
  line-height: 1.5;
  max-height: calc(v-bind(__rows) * 1.5em);
  word-break: break-word; //保留连续的空白符,支持换行
  word-wrap: break-word; //长英文单词可提前换行
  white-space: pre-wrap; //允许长的内容可以自动换行
  line-break: anywhere;
  overflow: hidden;
  text-overflow: ellipsis;
  line-clamp: v-bind(__rows);
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: v-bind(__rows);
}
</style>