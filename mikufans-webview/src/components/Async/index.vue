<template>
  <!-- 如果未在加载中,且数据为空,且未出现错误 -->
  <slot name="empty" v-if="empty && !loading && !error">
    <div class="trick py-10" :style="{backgroundImage:`url(${nodata})`}">
      <div class="mt-[200px]">{{ emptyText }}</div>
    </div>
  </slot>
  <!-- 显示数据 -->
  <slot v-if="always || !empty && !loading && !error"></slot>
  <!-- 数据尾部显示加载中或加载失败 -->
  <slot name="error" v-if="error">
    <div class="trick py-10 flex-col">
      <img :src="errorImg" alt="" style="width: 280px;" draggable="false">
      <div class="flex">
        <span class="mr-2">加载失败了<i style="font-size: 12px;">QAQ</i>...</span>
        <text-button title="刷新重试" @click="reload">
          <i class="iconfont icon-24gl-undo3"></i>
        </text-button>
      </div>
    </div>
  </slot>
  <slot name="loading" v-else-if="delayLoading">
    <div class="trick">
      <img :src="Loading" alt="" style="width: 1.5em;" draggable="false">
      <span>加载中...</span>
    </div>
  </slot>
</template>

<script setup lang="ts">
import Loading from '@/assets/images/loading2.gif'
import nodata from '@/assets/images/nodata.png'
import errorImg from '@/assets/images/error.png'
const props = defineProps({
  always: bool().def(false), //始终显示slot,loading或时仍然渲染组件
  loading: bool().def(false), //如果为真,显示加载组件
  error: any().def(false), //如果为真,显示错误组件
  empty: bool().def(false), //如果为真且loading为假,显示列表为空的组件
  emptyText: string().def('空空如也'),
  // mask: bool().def(false), //如果为真,无论是否加载中,都将渲染实际要展示的插槽
  height: string().def('auto'), //必须设置高点,防止不够显示图片
  minH: string().def('40px'), //最低高度
  reload: func().def(()=>location.reload()) //加载失败时如何处理重试
})
const delayLoading = ref(false) //延迟显示加载画面,避免闪烁
let timer = null
watchImmediate(()=>props.loading, (newVal, oldVal)=>{
  clearTimeout(timer)
  if (newVal) {
    timer = setTimeout(() => {
      delayLoading.value = true
    }, 200);
  } else {
    delayLoading.value = false
  }
})
onBeforeUnmount(()=>clearTimeout(timer))
</script>

<style scoped lang="scss">
.trick {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  height: v-bind(height);
  /* min-height: 220px; */
  min-height: v-bind(minH);
  box-sizing: border-box;
  /* aspect-ratio: 15; */
  color: var(--grey2);
  font-size: 16px;
  /* background-color: var(--bg0); */
  border-radius: 8px;
  background-repeat: no-repeat;
  background-position: center;
  background-size: auto; //auto图片实际大小
}
/* .layout {
  inset: 0;
  position: absolute;
  background-color: rgba(255, 255, 255, 0.9);
  z-index: 1;
  .effect {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translateX(-50%) translateY(-50%);
    color: #333;
  }
} */
</style>