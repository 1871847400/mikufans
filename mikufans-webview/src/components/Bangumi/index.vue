<template>
  <div>
    <a class="relative" :href="'/video/'+video.sid" target="_blank">
      <miku-image class="w-full" :res-id="posterId" poster />
      <span class="tip">
        <slot name="tip" :video="video" :bangumi="_bangumi">
          <span class="text-lg">
            {{ rate > 0 ? rate + '分' : '暂无评分' }}
          </span>
        </slot>
      </span>
    </a>
    <a class="maxline-2 my-1 w-fit" 
      :href="'/video/'+video.sid" 
      target="_blank" 
      :title="video.title">
      {{ video.title }}
    </a>
    <div class="maxline-1 grey2 text-xs">{{ desc }}</div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  video: object<Video>().isRequired,
  bangumi: object<Bangumi>(),
})
const _bangumi = props.bangumi || props.video.bangumi
const { desc, posterId, rate } = _bangumi
</script>

<style scoped lang="scss">
.tip {
  background: linear-gradient(0, rgba(0,0,0,.4), rgba(0,0,0,0));
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  padding-top: 24px;
  padding-bottom: 12px;
  text-indent: 12px;
  color: #fff;
}
</style>