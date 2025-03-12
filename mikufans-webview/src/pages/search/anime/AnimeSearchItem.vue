<template>
  <div class="anime-search-item">
    <div class="banner">
      <a :href="bangumi.uri" target="_blank" draggable="false">
        <miku-image :res-id="bangumi.posterId" class="size-full" draggable="false"/>
      </a>
      <subscribe :data="bangumi">
        <template #active>
          <i class="iconfont icon-heart-add-2-fill"></i>
        </template>
        <template #inactive>
          <i class="iconfont icon-heart-add-2-line"></i>
        </template>
      </subscribe>
      <a class="play-link" :href="video.uri" target="_blank" draggable="false">
        <i class="iconfont icon-24gf-playCircle"></i>
      </a>
      <div class="mask"></div>
    </div>
    <div class="flex flex-col flex-1 pl-4 relative">
      <div class="flex items-center gap-1 mb-1">
        <span class="bangumi-tag">{{ video.typeTag || '视频' }}</span>
        <a :href="bangumi.uri" target="_blank" class="maxline-1 text-lg w-fit" v-html="video.highlightTitle"></a>
      </div>
      <div class="grey1 flex gap-2 mb-2 text-xs">
        <span>{{ bangumi.styleNames }}</span>
        <span v-if="bangumi.premiere">{{ bangumi.premiere }}</span>
        <span>{{ bangumi.desc }}</span>
      </div>
      <rich-text :rows="3" html :content="'简介: '+video.intro" class="text-xs grey1" />
      <div class="mt-auto">
        <template v-if="bangumi.rateCount<1">
          <span class="text-lg">暂无评分</span>
        </template>
        <template v-else>
          <span class="mr-1 text-xs">{{ displayNumber(bangumi.rateCount) }}人评分</span>
          <span class="font-bold text-[#ffa058]">
            <span class="text-[22px]">{{ bangumi?.rate }}</span>
            <span>分</span>
          </span>
        </template>
      </div>
      <div class="flex items-center gap-2 relative mt-1">
        <a :href="video.uri" target="_blank">
          <el-button type="primary">立即观看</el-button>
        </a>
        <div class="hover-resize" ref="boxRef">
          <ol class="part-list">
            <template v-for="i in video.canplayCount">
              <a :href="video.uri+'/'+i" target="_blank">{{ i }}</a>
            </template>
          </ol>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { displayNumber } from '@/utils/common';
const { video } = defineProps<{ video: Video }>()
const bangumi = video.bangumi
const boxRef = useTemplateRef('boxRef')
const hover = useElementHover(boxRef)
watch(hover, ()=>{
  boxRef.value.scrollTo({ top: 0 })
})
</script>

<style scoped lang="scss">
.anime-search-item {
  display: flex;
  padding-right: 20px;
  padding-bottom: 20px;
  box-sizing: border-box;
  /* overflow: hidden; */
  // height: 206px; //padding+height
}
.bangumi-tag {
  color: var(--blue1);
  border: 1px solid var(--blue0);
  padding: 2px 6px;
  font-size: 12px;
  border-radius: 4px;
}
.banner {
  width: 148px;
  height: 198px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  .mask {
    opacity: 0;
    transition: opacity .3s;
    pointer-events: none;
    position: absolute;
    inset: 0;
    background-color: #00000052;
  }
  &:hover {
    .mask, .subscribe, .play-link {
      opacity: 1;
    }
  }
  .subscribe {
    color: #fff;
    font-size: 22px;
    position: absolute;
    left: 8px;
    top: 8px;
    opacity: 0;
    transition: opacity .3s;
    z-index: 1;
  }
  .play-link {
    position: absolute;
    right: 8px;   
    bottom: 8px;
    color: #fff;
    background: none;
    outline: none;
    font-size: 36px;
    z-index: 1;
    opacity: 0;
    transition: opacity .3s;
    cursor: pointer;
  }
}
$size: 32px;
.part-list {
  display: flex;
  flex-wrap: wrap;
  gap: calc($size / 4);
  /* padding-right: 10px; //避开滚动条 */
  a {
    width: $size;
    height: $size;
    line-height: $size;
    display: block;
    text-align: center;
    border-radius: 4px;
    box-sizing: border-box;
    transition: background .3s;
    border: 1px solid var(--bg2);
    &:hover {
      background-color: var(--bg2);
    }
  }
}
.hover-resize {
  top: 0;
  left: 100px;
  position: absolute;
  height: $size;
  overflow-x: hidden;
  overflow-y: hidden;
  &:hover {
    height: unset;
    min-height: calc($size + 1px);
    max-height: calc($size * 3);
    z-index: 100;
    background-color: var(--bg0);
    overflow-y: auto;
  }
}
</style>