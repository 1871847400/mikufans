<template>
  <a ref="el" class="anime-item" :href="data.video.uri" target="_blank" draggable="false" v-resize="resize">
    <div v-if="hover" class="relative rounded-lg overflow-hidden">
      <mini-video-player :data="data.video" />
      <div class="anime-styles">
        <span v-for="{ styleName } in data.styles">{{ styleName }}</span>
      </div>
    </div>
    <miku-image class="poster size-full object-cover rounded-lg" :res-id="data.posterId"/>
    <div class="anime-info">
      <div class="maxline-1 pb-1 hoverable">{{ data.video.title }}</div>
      <div class="text-xs grey2">{{ data.desc }}</div>
    </div>
  </a>
</template>

<script setup lang="ts">
const props = defineProps<{ data: Bangumi, pageSize: number }>()
const el = useTemplateRef('el')
//hover后显示的宽和高
const _width = ref('0')
const _height = ref('100%')
const { width } = useElementSize(el)
//延迟.3s才显示video,需要和transition一致,以免视频大小也触发过渡
const hover = useElementHover(el, { delayEnter: 300 })
function resize({ width, height }) {
  const posterHeight = el.value.parentElement.clientWidth * (1/props.pageSize) * 4 / 3
  _height.value = posterHeight + 'px'
  _width.value = posterHeight * (16 / 9) + 'px'
}
</script>

<style scoped lang="scss">
a.anime-item {
  transition: flex .3s;
  flex: 0 0 var(--width);
  width: var(--width);
  height: v-bind(_height);
  position: relative;
  &:hover {
    .poster {
      transition: opacity 3s;
      opacity: 0;
      inset: 0;
      position: absolute;
    }
    flex: 0 0 v-bind(_width);
    width: v-bind(_width);
  }
  /* 弥补首尾两个元素突出的部分 */
  .full & {
    &:first-of-type {
      margin-left: calc(v-bind('width+"px"') - var(--width));
    }
    &:last-of-type {
      margin-right: calc(v-bind('width+"px"') - var(--width));
    }
  }
  .anime-info {
    position: absolute;
    left: 0;
    right: 0;
    top: 100%;
    transform: translateY(8px);
  }
  .anime-styles {
    position: absolute;
    left: 8px;
    bottom: 8px;
    display: flex;
    gap: 8px;
    span {
      border-radius: 4px;
      background: #dddddd40;
      color: #fff;
      padding: 4px;
      font-size: 14px;
    }
  }
}
</style>