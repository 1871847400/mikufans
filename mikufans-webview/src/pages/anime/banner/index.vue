<template>
  <div class="banner-section">
    <miku-carousel v-if="state?.length" ref="carousel" :list="state">
      <template v-slot="{ bannerId, url }">
        <a :href="url" target="_blank">
          <miku-image :res-id="bannerId" class="size-full" />
        </a>
      </template>
    </miku-carousel>
    <div class="banner-container" v-if="current">
      <div class="anime-action">
        <a class="anime-play" :href="current.url" target="_blank">
          <i class="iconfont icon-24gf-playCircle play-ico"></i>
          <a class="hoverable">
            <span>{{ current.title }}</span>
            <span class="text-sm ml-3">{{ current.subtitle }}</span>
          </a>
        </a>
        <subscribe-button v-if="current.video?.bangumi" type="icon" :bangumi="current.video.bangumi"/>
      </div>
      <div class="banner-list">
        <template v-for="i,k in state" :key="i.id">
          <a class="banner-item" :href="i.url" target="_blank" :data-active="carousel.index===k" @mouseenter="mouseenter(k)" @mouseleave="mouseleave">
            <miku-image :res-id="i.thumbnailId || i.bannerId"/>
            <div class="title-shadow"></div>
            <span class="title">{{ i.title }}</span>
            <div v-if="carousel.index===k" class="banner-progress"></div>
          </a>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { listCarousels } from '@/apis/system';
const carousel = useTemplateRef('carousel')
const { state } = useAsyncState(listCarousels({ position: 'ANIME'}), [])
const current = computed<SysCarousel>(()=>state.value[carousel.value?.index])
function mouseenter(idx: number) {
  carousel.value.pause()
  carousel.value.moveTo(idx)
}
function mouseleave(e: MouseEvent) {
  carousel.value.resume()
  // if (e.target instanceof HTMLElement) {
  //   e.target.getAnimations({subtree:true})[0].currentTime = 0
  // }
}
</script>

<style scoped lang="scss">
.banner-section {
  position: relative;
  min-height: 150px; //防止没有数据被遮挡
  .miku-carousel {
    width: 100%;
    height: auto;
    aspect-ratio: 3;
    border-radius: 0;
    :deep(.carousel-arrow), :deep(.carousel-dot) {
      display: none;
    }
  }
  .banner-container {
    position: absolute;
    padding-top: 100px; //撑起背景高度
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(180deg,hsla(0,0%,100%,0),hsla(0,0%,100%,.07) 11.79%,hsla(0,0%,100%,.08) 21.38%,hsla(0,0%,100%,.0704) 29.12%,hsla(0,0%,100%,.120652) 35.34%,hsla(0,0%,100%,.181481) 40.37%,hsla(0,0%,100%,.2512) 44.56%,hsla(0,0%,100%,.328119) 48.24%,hsla(0,0%,100%,.410548) 51.76%,hsla(0,0%,100%,.4968) 55.44%,hsla(0,0%,100%,.585185) 59.63%,hsla(0,0%,100%,.674015) 64.66%,hsla(0,0%,100%,.7616) 70.88%,hsla(0,0%,100%,.846252) 78.62%,hsla(0,0%,100%,.926281) 88.21%,#fff);
  }
  .anime-action {
    display: flex;
    column-gap: 12px;
    align-items: center;
    margin-left: 64px;
    margin-bottom: 32px;
  }
  .anime-play {
    display: flex;
    width: fit-content;
    border-radius: 25px;
    background-color: rgba(0,0,0,.6);
    height: 50px;
    line-height: 50px;
    color: #fff;
    font-size: 16px;
    padding-right: 12px;
    cursor: pointer;
    .play-ico {
      font-size: 46px;
      margin-right: 4px;
      transition: scale 0.3s;
      color: v-bind('current?.mainColor');
      opacity: .9;
      &:hover {
        scale: 1.3;
      }
    }
  }
  .banner-list {
    display: flex;
    flex-wrap: nowrap;
    padding-bottom: 16px;
    margin: 0 auto;
    width: fit-content;
    column-gap: 20px;
  }
  .banner-item {
    position: relative;
    border-radius: 8px;
    overflow: hidden;
    box-sizing: border-box;
    padding: 4px;
    width: 180px;
    height: auto;
    aspect-ratio: 1.8;
    background-color: rgb(255, 255, 255, .5);
    cursor: pointer;
    transition: scale 0.3s;
    &[data-active=true], &:hover {
      scale: 1.2;
    }
    .miku-image {
      position: relative;
      z-index: 1;
      width: 100%;
      height: 100%;
      border-radius: 8px;
      cursor: pointer;
      pointer-events: none;
      &::after {
        content: '';
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        height: 30px;
        background: var(--img-shadow);
      }
    }
    .title {
      position: absolute;
      left: 10px;
      right: 10px;
      bottom: 10px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      color: #fff;
      z-index: 2;
    }
    .title-shadow {
      position: absolute;
      left: 5px;
      right: 5px;
      bottom: 5px;
      height: 36px;
      border-radius: 4px;
      z-index: 2;
      background: var(--img-shadow);
    }
    @keyframes width {
      0% {
        right: 100%;
      }
      100% {
        right: 0;
      }
    }
    .banner-progress {
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      background-color: #fff;
      animation: width 4s linear forwards;
    }
    &:hover .banner-progress {
      animation-play-state: paused;
    }
  }
}
</style>