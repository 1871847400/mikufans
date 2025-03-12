<template>
  <div class="rich-text text-wrapper">
    <input :id="uid" class="exp" type="checkbox">
    <div class="text" ref="text">
      <!-- <label for="exp1" class="btn" v-if="overflow"></label> -->
    </div>
  </div>
</template>

<script setup lang="ts">
import { convertToHtml } from '@/utils/emoji';
import { uniqueId } from 'lodash';
const props = defineProps({
  content: string().def(''),
  html: bool().def(true), //是否转换成html
  rows: number().def(3),
  expand: bool().def(true),//是否溢出后显示展开
  lineHeight: number().def(1.5) //行高
})
const lh = computed(()=>props.lineHeight+'em')
const uid = uniqueId('rich-text-')
const text = ref<HTMLElement>()
onMounted(()=>{
  watchImmediate(()=>props.content, ()=>{
    text.value.innerHTML = props.html ? convertToHtml(props.content) : props.content
    // console.log(text.value.clientHeight);
    const lineHeight = getComputedStyle(text.value).lineHeight
    // console.log(lineHeight);
    const maxH = parseFloat(lineHeight.replace('px', '')) * props.rows
    const overflow = text.value.scrollHeight - maxH > 1
    //大表情会超出一般行高,会影响溢出判断
    const overflow2 = text.value.scrollHeight - text.value.clientHeight > 1
    if (overflow && overflow2 && props.expand) {
      text.value.innerHTML = `<label for="${uid}" class="btn"></label>` + text.value.innerHTML
    }
    // console.log(lineHeight, text.value.scrollHeight, maxH, overflow);
  })
})
</script>

<style scoped lang="scss">
.text-wrapper {
  display: flex; //方便计算高度
  overflow: hidden;
  /* test */
  /* width: 500px;
  padding: 15px;
  background-color: lightgreen; */
  .text {
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    text-align: justify;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: v-bind(rows);
    line-break: anywhere;
    white-space: pre-wrap; //遇到\n换行
    word-break: break-all;
    position: relative;
    line-height: v-bind(lineHeight);
    &::before {
      content: '';
      height: calc(100% - v-bind(lh) + 0.5px);
      float: right;
      /* width: 10px;
      background-color: red; */
    }
  }
  /* 不显示checkbox */
  .exp {
    display: none;
  }
  /* 如果选择展开了 */
  .exp:checked+.text {
    -webkit-line-clamp: 999;
    :deep(.btn::before) {
      content: '收起';
    }
  }
  :deep(.btn) {
    margin-left: 10px;
    color: var(--blue0);
    cursor: pointer;
    float: right;
    clear: both;
    &::before {
      content: '展开';
    }
  }
}
</style>