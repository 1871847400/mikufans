<template>
  <div class="top-section">
    <div class="bg-blur" :style="{'background-image':'url('+bgUrl+')'}"></div>
    <div class="bangumi-info">
      <div class="poster">
        <miku-image class="w-[220px]" :res-id="video.bangumi.posterId" preview poster/>
      </div>
      <div class="information">
        <div class="flex flex-wrap mb-4 gap-5 items-center">
          <span class="text-2xl font-bold">{{ video.title }}</span>
          <div class="flex flex-wrap gap-2">
            <span v-for="i in video.bangumi.styles" class="style-item">{{ i.styleName }}</span>
          </div>
        </div>
        <stats/>
        <div class="mt-5 mb-4 text-xs">
          <span class="mr-4">{{ video.bangumi.premiere }}开播</span>
          <span>{{ video.bangumi.desc }}</span>
        </div>
        <div class="flex-1 overflow-hidden pb-4">
          <el-scrollbar class="text-xs leading-5">{{ video.intro }}</el-scrollbar>
        </div>
        <div class="flex items-center gap-5 mt-auto pb-2">
          <el-button class="play-btn" tag="a" :href="'/video/'+video.sid" color="#2ab9ed">立即播放</el-button>
          <subscribe-button :bangumi="video.bangumi"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { imageUrl } from '@/apis/image';
import Stats from './stats.vue';
const { video } = toRefs(useBangumiStore())
const bgUrl = imageUrl + video.value.bangumi.posterId
</script>

<style scoped lang="scss">
.top-section {
  height: 360px;
  position: relative;
  padding-top: var(--navbar-height);
  .bg-blur {
    width: 100%;
    height: 100%;
    background-repeat: no-repeat;
    background-size: 100%;
    background-position: center;
    filter: blur(40px);
    position: absolute;
    left: 0;
    top: 0;
    z-index: -1;
    background-color: #333;
  }
}
.bangumi-info {
  width: 78%;
  height: 300px;
  display: flex;
  align-items: center;
  margin: 0 auto;
  margin-top: 24px;
}
.poster {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  padding: 2px;
}
.information {
  flex: 1;
  padding-left: 16px;
  color: #fff;
  font-size: 14px;
  height: 100%;
  display: flex;
  flex-direction: column;
  p {
    margin-block-end: 2px;
  }
  .play-btn {
    color: #fff;
    width: 140px;
    height: 45px;
    border-radius: 4px;
    font-size: 18px;
  }
  .subscribe-button {
    width: 120px; 
    height: 40px;
    font-size: 18px;
    background-color: var(--pink0);
  }
}
.style-item {
  border: 1px solid #fff;
  border-radius: 4px;
  padding: 0 4px;
  font-size: 14px;
}
</style>