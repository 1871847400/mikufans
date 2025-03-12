<template>
  <div class="my-text" v-bind="props">
    <a v-if="html" class="content" ref="contentRef" :class="{show:__expand}" :href="href" v-html="content"></a>
    <a v-else class="content" ref="contentRef" :class="{show:__expand}" :href="href" v-text="content"></a>
    <div class="expand" v-show="showExpand" @click="__expand=!__expand">{{ __expand ? '收起' : '展开' }}</div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  content: {
    type: String,
    default: '',
  },
  //是否以v-html插入content
  html: {
    type: Boolean,
  },
  //默认最大显示行数,-1不做限制
  rows: {
    type: [Number, String],
    default: -1,
  },
  //是否能展开,展开后无视行数限制
  expand: {
    type: Boolean,
    default: false,
  },
  //链接地址
  href: {
    type: String,
  },
  //字体大小
  fs: {
    type: String,
    default: '14px',
  },
})
const contentRef = ref<HTMLElement>()
//是否展开了
const __expand = ref(false)
//是否有溢出
function isOverflow() {
  return contentRef.value && contentRef.value.scrollHeight > contentRef.value.offsetHeight
}
//是否显示展开按钮
const showExpand = computed(()=>{
  props.content
  return props.expand && isOverflow()
})
const __rows = computed(()=>{
  return (props.rows == -1 || __expand.value) ? 999 : props.rows
})
</script>

<style scoped lang="scss">
.my-text {
  position: relative;
  font-size: v-bind(fs);
}
.content {
  display: block;
  color: inherit;
  font-size: inherit;
  line-height: 1.5;
  max-height: calc(v-bind(__rows) * 1.5em);
  word-break: break-word;
  word-wrap: break-word;
  white-space: pre-wrap;
  line-break: anywhere;
  overflow: hidden;
  text-overflow: ellipsis;
  line-clamp: v-bind(__rows);
  // 谷歌浏览器下最大显示两行
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: v-bind(__rows);
}
.expand {
  user-select: none;
  cursor: pointer;
  position: absolute;
  font-size: inherit;
  color: #00a1d6;
  bottom: 0;
  left: 0;
  transform: translateY(100%);
  &:hover {
    color: rgb(3, 188, 250);
  }
}
.show {
  max-height: fit-content!important;
}
</style>