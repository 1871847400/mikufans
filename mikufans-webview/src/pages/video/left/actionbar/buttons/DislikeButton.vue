<template>
  <div 
    class="action-item" 
    title="点踩"
    :class="{ active: video.likeStatus.likeVal==2 }"
    @click="dislike"
  >
    <div class="rotate-180 scale-x-[-1]">
      <i class="iconfont icon-dianzan_kuai text-[28px]"></i>
    </div>
    <action-number :number="video.likeStatus.dislikes"/>
  </div>
</template>

<script setup lang="ts">
import ActionNumber from '../ActionNumber.vue';
const { video } = storeToRefs(useVideoStore())
const { dislike } = useVideoStore()
</script>