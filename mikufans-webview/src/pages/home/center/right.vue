<template>
  <el-skeleton v-for="i in pageSize" :loading="isLoading||state?.length==0" animated>
    <template #template>
      <el-skeleton-item class="w-full h-auto aspect-video" variant="image"/>
      <el-skeleton-item class="h-[20px] mt-[6px]" variant="h1"/>
      <el-skeleton-item class="h-[18px] w-[75%]" variant="h1"/>
      <div class="flex items-center mt-[2px] h-[18px]">
        <el-skeleton-item class="size-[24px] mr-1" variant="circle"/>
        <el-skeleton-item class="w-[40%]" variant="text"/>
        <el-skeleton-item class="w-[20%] ml-auto" variant="text"/>
      </div>
    </template>
  </el-skeleton>
  <template v-if="!isLoading" v-for="i in state.toSpliced(pageSize)" :key="i.id">
    <video-item :video="i" mode="inline"/>
  </template>
  <div class="change-btn" @click.prevent="execute(300)" :class="{active:state&&isLoading}">
    <i class="iconfont">&#xe633;</i>
    <span>换一换</span>
  </div>
</template>

<script setup lang="ts">
import videoApi from '@/apis/video';
import VideoItem from '../VideoItem.vue';
const props = defineProps<{ pageSize: number }>()
const { pageSize } = toRefs(props)
const { state, execute, isLoading } = useAsyncState(()=>videoApi.getRecommend({ 
  size: pageSize.value
}), null, { 
  resetOnExecute: false, 
})
//当视口变大但显示的视频数量不足时刷新
watch(pageSize, (size, oldSize)=>{
  if (size > oldSize && state?.value && state.value.length < size) {
    execute(10)
  }
})
</script>

<style scoped lang="scss">
.change-btn {
  position: absolute;
  right: -10px;
  top: 0;
  transform: translateX(100%);
  background-color: var(--bg0);
  border: 1px solid #E3E5E7;
  border-radius: 8px;
  user-select: none;
  font-size: 12px;
  line-height: 14px;
  width: 40px;
  box-sizing: border-box;
  text-align: center;
  padding: 10px 10px;
  cursor: pointer;
  transition: all .3s;
  .iconfont {
    display: block;
    margin-bottom: 6px;
  }
  &:hover {
    background-color: var(--grey3);
  }
  &.active .iconfont {
    rotate: 360deg;
    transition: rotate 0.3s; //需要execute延时,否则一闪而过
  }
  &:active {
    scale: .9;
    transform-origin: right center;
  }
}
</style>