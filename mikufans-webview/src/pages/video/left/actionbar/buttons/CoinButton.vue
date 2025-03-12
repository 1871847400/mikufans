<template>
  <div 
    class="action-item" 
    title="投币"
    :class="{ active: video.userCoin>=1 }"
    @click="openDialog(CoinDialog, { payCoin })"
  >
    <circle-progress v-model="triple.progress">
      <div ref="iconRef">
        <i class="iconfont icon-toubi text-[28px]"></i>
      </div>
    </circle-progress>
    <action-number :number="video.coins"/>
  </div>
</template>

<script setup lang="ts">
import { splash } from '@/utils/animation';
import ActionNumber from '../ActionNumber.vue';
import { openDialog } from '@/utils/dialog';
import CoinDialog from '../coin/CoinDialog.vue'
import videoApi from '@/apis/video';
const { video } = storeToRefs(useVideoStore())
const { triple } = useVideoStore()
const { w } = useMagicKeys()
watch(w, (pressed)=>{
  if (pressed && document.activeElement.tagName === 'BODY') {
    openDialog(CoinDialog, {
      payCoin,
    })
  }
})
const iconRef = ref<HTMLDivElement>(null)
function handle() {
  splash({
    container: iconRef.value,
    offset: 0,
    color: triple.color_dark,
    size: 12,
  })
  splash({
    container: iconRef.value,
    offset: 22,
    color: triple.color_light,
    size: 8,
    delay: 0.1,
  })
  payCoin(2)
}
onBeforeUnmount(triple.trigger.on(handle).off)
async function payCoin(count: number) {
  try {
    await videoApi.payCoin(video.value.id, count)
    video.value.coins += count
    video.value.userCoin += count
    message.success('投币成功')
  } catch(ignored) {}
}
</script>