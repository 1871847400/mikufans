<template>
  <div>
    <div class="flex items-center justify-between mb-3">
      <div class="flex gap-1 items-center">
        <img class="w-8" :src="Heart" alt="" draggable="false">
        <span class="text-2xl font-bold">正在追番</span>
      </div>
      <a :href="`/space/${userStore.id}/subscribe`" target="_blank" draggable="false">
        <el-button>
          <template #icon>
            <i class="iconfont icon-heart-add-2-line text-lg"></i>
          </template>
          <span>全部追番</span>
        </el-button>
      </a>
    </div>
    <div class="relative">
      <el-skeleton class="anime-list" :loading="!page" animated :count="pageSize">
        <template #template>
          <div class="anime-item">
            <el-skeleton-item class="anime-image" variant="image"/>
            <el-skeleton-item class="mt-2" variant="h1"/>
          </div>
        </template>
        <template v-if="page">
          <transition-group name="list" tag="ul" class="anime-list" :class="[dir]">
            <template v-for="{ video, watchProgress, watchInfo, desc } in page.records" :key="Symbol()">
              <div class="anime-item">
                <a class="anime-image" :href="getUrl(video)" target="_blank" draggable="false">
                  <miku-image class="w-full" video :res-id="video.bannerId"/>
                  <div class="overlay" :style="{'--progress': watchProgress*100+'%'}">{{ watchInfo }}</div>
                </a>
                <a class="text-bold text-base maxline-1 my-1 w-fit hoverable" :href="getUrl(video)" target="_blank">{{ video.title }}</a>
                <div class="text-[13px] grey2">{{ desc }}</div>
              </div>
            </template>
          </transition-group>
          <prev-and-next v-model="dir" :page="page.current" :done="done" @prev="prev" @next="next" offset="20px" />
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup lang="ts">
import bangumiApi from '@/apis/bangumi';
import Heart from '@/assets/images/heart.png'
import { usePage } from '@/hooks/usePage';
import PrevAndNext from '../PrevAndNext.vue';
const dir = ref('right')
const userStore = useUserStore()
const { width } = useWindowSize()
const pageSize = computed(()=>width.value<=1535?5:6)
const { prev, next, done, page } = usePage(search, { immediate: [1] })
async function search(pageNum: number) {
  return await bangumiApi.search({
    videoType: 'ANIME',
    pageNum,
    pageSize: pageSize.value,
    subscribedUserId: userStore.id,
  })
}
function getUrl(video: Video) {
  const target = video.history?.part || video
  return target.uri
}
</script>

<style scoped lang="scss">
div :deep(i.prev, i.next) {
  transform: translateY(-10px);
}
.anime-list {
  gap: 16px;
  overflow: hidden;
  display: flex;
  flex-wrap: nowrap;
  --width: calc((100% - v-bind('pageSize - 1') * 16px) / v-bind(pageSize));
  .anime-item {
    width: var(--width);
    flex: 0 0 var(--width);
    height: auto;
    .anime-image {
      width: 100%;
      height: auto;
      aspect-ratio: 16/9;
      border-radius: 8px;
      overflow: hidden;
      position: relative;
    }
  }
}
.overlay {
  pointer-events: none;
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--img-shadow);
  color: #fff;
  padding: 12px 8px;
  font-size: 13px;
  &::before, &::after {
    content: '';
    position: absolute;
    left: 8px;
    right: 8px;
    bottom: 8px;
    height: 2px;
    background-color: #bbb;
  }
  &::after {
    background-color: #fff;
    transform: scaleX(var(--progress));
    transform-origin: left;
  }
}
</style>

<style scoped lang="scss">
.list-move, /* 对移动中的元素应用的过渡 */
.list-enter-active,
.list-leave-active {
  transition-property: transform, opacity;
  transition-duration: .3s;
}
.list-enter-from, .list-leave-to {
  opacity: 0;
}
.list-enter-active {
  transition-delay: .3s;
}
.anime-list.left {
  .list-leave-to {
    transform: translateX(30px);
  }
  .list-enter-from {
    transform: translateX(-30px);
  }
}
.anime-list.right {
  .list-leave-to {
    transform: translateX(-30px);
  }
  .list-enter-from {
    transform: translateX(30px);
  }
}
.list-leave-active {
  /* position: absolute; */
}
</style>