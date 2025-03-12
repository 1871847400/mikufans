<template>
  <li class="subscribe-item">
    <a :href="video.uri" target="_blank">
      <miku-image class="w-[110px]" poster :res-id="data.posterId"/>
    </a>
    <div class="flex flex-col py-1 relative">
      <a class="text-lg mb-1 maxline-1 w-fit" :href="video.uri" target="_blank" :title="video.title">{{ video.title }}</a>
      <div class="text-xs grey1 maxline-3">{{ video.intro }}</div>
      <div class="text-xs grey2 mt-2">{{ data.desc }}</div>
      <template v-if="isSelf">
        <div class="text-xs grey2 mt-2">{{ data.watchInfo }}</div>
        <div class="text-xs grey2 mt-auto">{{ data.subscribed.subscribeDate }}追番</div>
      </template>
      <subscribe-button style="width: 80px; font-size: 14px;" :bangumi="data"/>
    </div>
  </li>
</template>

<script setup lang="ts">
const props = defineProps<{
  data: Bangumi
}>()
const { isSelf } = toRefs(useSpaceStore())
const video = toRef(()=>props.data.video)
</script>

<style scoped lang="scss">
.subscribe-item {
  display: flex;
  gap: 12px;
}
.subscribe-button {
  position: absolute;
  right: 4px;
  bottom: 4px;
}
</style>