<template>
  <div class="anime-card">
    <div class="banner">
      <a class="mask" :href="bangumi.uri" target="_blank"></a>
      <miku-image class="size-full" poster :res-id="bangumi.posterId"/>
      <!-- 鼠标悬浮显示 -->
      <i class="play-icon iconfont pointer-events-none">&#xe87e;</i>
      <div class="desc">
        <div class="maxline-1 font-bold text-center mb-2">
          <a class="hoverable" :href="bangumi.uri" target="_blank" :title="video.title">{{ video.title }}</a>
        </div>
        <div class="text-xs maxline-6">{{ video.intro }}</div>
      </div>
      <div class="rate">
        <span>{{ bangumi.rate }}</span>
      </div>
    </div>
    <a class="maxline-1 hoverable w-fit my-1 text-lg" :href="bangumi.uri" target="_blank" :title="video.title">{{ video.title }}</a>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ bangumi: Bangumi }>()
const video = toRef(props.bangumi, 'video')
</script>

<style scoped lang="scss">
.anime-card {
  box-sizing: border-box;
  overflow: hidden;
}
.banner {
  width: 100%;
  aspect-ratio: 0.74;
  overflow: hidden;
  border-radius: 12px;
  position: relative;
  cursor: pointer;
  border-bottom: 2px solid #eee;
  &:hover .desc {
    top: 100px;
  }
  &:hover .play-icon {
    opacity: 1;
    transform: translateX(-50%) translateY(-50%) scale(1);
  }
  &:hover .mask {
    opacity: .2;
  }
}
.play-icon {
  position: absolute;
  top: 50px;
  left: 50%;
  transform: translateX(-50%) translateY(-50%) scale(.5);
  font-size: 32px;
  opacity: 0;
  transition: all 500ms;
  color: #fff;
}
.desc {
  border-radius: 8px;
  position: absolute;
  width: 100%;
  height: 100%;
  top: 100%;
  left: 0;
  background-color: var(--bg0);
  border: 1px solid var(--bg2);
  z-index: 1;
  transition: top 0.5s;
  padding: 12px;
  box-sizing: border-box;
  cursor: auto;
}
.mask {
  width: 100%;
  height: 100%;
  display: block;
  position: absolute;
  background-color: #000;
  opacity: 0;
  transition: all 1s;
}
.rate {
  border-radius: 12px;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 2px 8px;
  padding-top: 10px;
  text-align: right;
  color: #fff;
  background: var(--img-shadow);
  font-size: 24px;
}
</style>