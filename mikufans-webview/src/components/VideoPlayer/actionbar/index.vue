<template>
  <div class="flex items-center justify-between">
    <play-part />
    <danmu-bar v-if="isFullscreen" />
    <setting-part />
  </div>
</template>

<script setup lang="ts">
import PlayPart from './PlayPart.vue';
import SettingPart from './SettingPart.vue';
import DanmuBar from '../danmubar/index.vue'
const { playerElement } = toRefs(useVideoStore())
const { isFullscreen } = useFullscreen(playerElement, { autoExit: true })
</script>

<style scoped lang="scss">
.danmu-bar {
  --grey1: #fff;
  --grey2: #ccc;
  --bg1: #666666a5;
}
</style>