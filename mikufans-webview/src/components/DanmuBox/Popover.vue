<template>
  <div class="popover" :style="{ '--x': x+'px', }">
    <div class="actions">
      <div class="action-item" @click="like" title="点赞">
        <i class="iconfont" 
          :class="{ 
            'icon-dianzan_kuai': likeStatus.likeVal==1, 
            'icon-dianzan': likeStatus.likeVal!=1 
          }"
        ></i>
        <span v-if="likeStatus.likes" class="favors-tip">{{ likeStatus.likes>99?'99+':likeStatus.likes }}</span>
      </div>
      <div class="action-item" @click="onCopy" title="复制">
        <i class="iconfont icon-fuzhi"></i>
      </div>
      <div v-if="danmu.self" class="action-item" @click="onRemoves" title="撤回">
        <i class="iconfont icon-chehui"></i>
      </div>
      <div v-else class="action-item" @click="onReport" title="举报">
        <i class="iconfont icon-jinggao"></i>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import videoDanmuApi from '@/apis/video/danmu';
import ReportDialog from '@/dialogs/ReportDialog.vue';
import { useLike } from '@/hooks/useLike';
import { copyText } from '@/utils/copy';
import { openDialog } from '@/utils/dialog';
const props = defineProps<{
  danmu: Danmu,
  x: number
}>()
const { danmus } = toRefs(useVideoStore())
const currentDanmu = computed(()=>danmus.value.find(a=>a.id===props.danmu.id))
const { likeStatus } = toRefs(currentDanmu.value)
const emits = defineEmits<{ remove: [] }>()
const { like } = useLike(likeStatus)
function onCopy() {
  copyText(props.danmu.text)
  message.success('已复制弹幕')
}
function onReport() {
  openDialog(ReportDialog, {
    reportType: 'DANMU',
    targetId: props.danmu.id,
  })
}
async function onRemoves() {
  await videoDanmuApi.remove(props.danmu.id)
  danmus.value = danmus.value.filter(a=>a.id!==props.danmu.id)
  emits('remove')
}
</script>

<style scoped lang="scss">
.popover {
  /* display: none; */
  left: calc(var(--x) - 50%);
  top: 100%;
  width: fit-content;
  position: absolute;
  color: #fff;
  cursor: auto;
  .actions {
    width: fit-content;
    background: #00000086;
    display: flex;
    align-items: center;
    border-radius: 16px;
    padding: 4px 16px;
    gap: 24px;
    box-shadow: 0 0 4px #eee;
    .action-item {
      cursor: pointer;
      padding: 2px;
      position: relative;
      .favors-tip {
        position: absolute;
        z-index: 1;
        right: -0;
        top: -2px;
        transform: translateX(100%);
        font-size: 12px;
      }
    }
  }
}
</style>