<template>
  <el-dialog class="video-part-split" v-model="show" width="650px" center title="视频裁剪" :close-on-click-modal="false" destroy-on-close>
    <div class="video-length-edit">
      <video ref="videoRef" autoplay :src="url" @timeupdate="timeupdate" @durationchange="duration=videoRef.duration" @pause="pause" @contextmenu.prevent></video>
      <!-- <el-scrollbar class="video-edit" view-class="edit-view"> -->
      <div class="video-edit">
        <div class="preview-list" @click="click">
          <img v-for="i in previews" :src="i">
        </div>
        <slider v-model="pos" title="当前播放位置" extend="10px" @change="change" :min="selection[0]" :max="selection[1]"/>
        <selection v-model="selections[select]" v-if="!loading"/>
      </div>
      <div class="time-info">
        <span>{{ toHMS(selection[0] * duration, 's', true) }}</span>
        <span>{{ toHMS(position, 's', true) }}</span>
        <span>{{ toHMS(selection[1] * duration, 's', true) }}</span>
      </div>
      <section>
        <div>
          <span style="margin-right: 8px;">片段列表</span>
          <el-button @click="addSelection" size="small">添加片段</el-button>
          <!-- <el-switch v-model="final" active-text="预览结果"/> -->
        </div>
        <div ref="listRef">
          <template v-for="i,k in selections" :key="k">
            <selection-part 
              :data="i" 
              :duration="duration" 
              :focus="select==k" 
              @click="chgSelection(k)" 
              :closeable="k!==0" 
              @close="remSelection(k)"
            />
          </template>
        </div>
      </section>
      <div class="actions">
        <div class="volume">
          <i class="iconfont" @click="mute">{{ muted || volume==0 ? '&#xea0b;' : '&#xea0d;' }}</i>
          <el-slider v-model="volume" style="--el-slider-button-size:14px; width: 120px;" size="small" :min="0" :max="1" :step="0.01" :show-tooltip="false"/>
        </div>
        <el-button link @click="play" class="iconfont play">{{paused ? '&#xea82;' : '&#xea81;'}}</el-button>
        <div class="speed">
          <span class="iconfont" title="播放倍速">&#xeb7b;</span>
          <el-radio-group v-model="speed" size="small">
            <el-radio-button :value="0.5" label="X0.5"/>
            <el-radio-button :value="1" label="X1"/>
            <el-radio-button :value="2" label="X2"/>
            <el-radio-button :value="3" label="X3"/>
          </el-radio-group>
        </div>
      </div>
      <div class="mb-3">
        <el-button class="save-btn" type="primary" @click="submit">保存</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import Slider from './Slider.vue';
import Selection from './Selection.vue';
import { toHMS } from '@/utils/datetime';
import logger from '@/utils/logger';
import SelectionPart from './SelectionPart.vue';
import { useSortable } from '@vueuse/integrations/useSortable';
//接收一个视频地址
const props = defineProps<{
  url: string //视频文件的url,
  selections: [number, number][] //默认的片段列表
}>()
const emits = defineEmits<{
  submit: [data: [number, number][]]
}>()
const videoRef = ref<HTMLVideoElement>()
const show = ref(true)
//是否暂停
const paused = ref(false)
//播放位置百分比
const pos = ref(0)
const position = ref(0)
//总时长(s)
const duration = ref(0)
//声音百分比
const volume = ref(1)
//是否禁音
const muted = ref(false)
//播放速度
const speed = ref(1)
watch(volume,()=>videoRef.value.volume=volume.value)
watch(speed,()=>videoRef.value.playbackRate=speed.value)
//切割视频的起点和终点0-1
// const selection = ref<[number, number]>([0, 1])
const select = ref(0)
const selections = ref<[number, number][]>(props.selections?.length ? props.selections : [[0, 1]])
const selection = computed(()=>selections.value[select.value])
const loading = ref(true)

function addSelection() {
  if (selections.value.length >= 5) {
    message.warning('最多创建5个')
  } else {
    selections.value.push([0, 1])
    chgSelection(selections.value.length - 1)
  }
}
function remSelection(i: number) {
  if (select.value == i) {
    select.value--
  }
  selections.value.splice(i, 1)
}
function chgSelection(i: number) {
  select.value = i
  videoRef.value.currentTime = selections.value[i][0] * duration.value
}
function change(p) {
  if (videoRef.value) {
    videoRef.value.currentTime = videoRef.value.duration * p
  }
}
function play() {
  if (videoRef.value.paused) {
    videoRef.value.play()
  } else {
    videoRef.value.pause()
  }
}
function pause() {
  if (!videoRef.value) return
  paused.value = true
  const s = videoRef.value.currentTime / duration.value
  if (Math.abs(selection.value[1]-s) < 0.0001) {
    if (final.value) {
      // chgSelection((select.value+1)%selections.value.length)
      // videoRef.value.play()
    } else {
      const min = selection.value[0] * duration.value
      videoRef.value.currentTime = min
      videoRef.value.play()
    }
  }
}
function click(e) {
  // console.log('click',e);
}
function timeupdate() {
  if (!videoRef.value) return
  if (!videoRef.value.paused) {
    paused.value = false
  }
  const percent = videoRef.value.currentTime / videoRef.value.duration
  const max = selection.value[1]
  if (percent >= max) {
    videoRef.value.pause()
    videoRef.value.currentTime = max * duration.value
    pos.value = max
  } else {
    pos.value = percent
  }
  position.value = videoRef.value.currentTime
}
function mute() {
  videoRef.value.muted=!videoRef.value.muted
  muted.value = videoRef.value.muted
}
function submit() {
  emits('submit', selections.value)
  show.value = false
}
function init() {
  if (!props.url) return
  const video = document.createElement('video')
  video.hidden = true
  video.onloadeddata = () => {
    const stack = []
    const duration = video.duration
    //一共截图20张
    const count = 20
    const step = duration / count
    logger.info('capture count:'+count+' step:'+step)
    for (let i = 0; i < count; i++) {
      stack.push(step*i)
    }
    capture(video, stack)
  }
  video.src = props.url
}
init()
const previews = ref([])
function capture(video: HTMLVideoElement, stack: number[]) {
  if (stack.length == 0) {
    loading.value = false
    return video.remove()
  }
  video.currentTime = stack.shift()
  video.onseeked = () => {
    const canvas = document.createElement('canvas')
    canvas.width = 500
    canvas.height = 281.25
    canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height)
    const url = canvas.toDataURL('image/png')
    canvas.remove()
    previews.value.push(url)
    capture(video, stack)
  }
}
const final = ref(false)
const listRef = ref<HTMLElement>()
onMounted(()=>{
  nextTick(()=>{
    useSortable(listRef, selections, {
      handle: '.handle',
      animation: 250,
    })
  })
})
</script>

<style scoped lang="scss">
.video-part-split {
  :deep(.el-dialog__body) {
    padding: 8px 18px;
  }
}
.video-length-edit {
  width: 100%;
  height: 100%;
  text-align: center;
  video {
    width: 100%;
    aspect-ratio: 16/9;
    background-color: #000;
  }
}
.video-edit {
  position: relative;
  box-sizing: border-box;
  width: 100%;
  /* overflow-x: hidden; */
  /* :deep(.edit-view) {
    position: relative;
    padding: 10px 0;
    padding-bottom: 16px;
  } */
}
.preview-list {
  display: flex;
  img {
    /* width: 60px;
    min-width: 60px;//节约图片加载时间,快速确定所需空间 */
    width: 5%;
    height: 50px;
  }
}
.actions {
  display: flex;
  justify-content: center;
  margin: 12px 0;
  .volume {
    display: flex;
    align-items: center;
    width: 220px;
    padding-left: 30px;
    box-sizing: border-box;
    // margin-left: 20px;
    .iconfont {
      font-size: 22px;
      margin-right: 12px;
      cursor: pointer;
      user-select: none;
    }
  }
  .speed {
    display: flex;
    align-items: center;
    width: auto;
    width: 220px;
    // margin-right: 20px;
    .iconfont {
      font-size: 26px;
      margin-right: 12px;
    }
  }
  .play {
    font-size: 36px;
    width: 120px;
    color: var(--blue-1);
  }
}
.save-btn {
  width: 140px;
  height: 36px;
}
.time-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
}
.preview-final {
  position: absolute;
  width: 400px;
  height: 300px;
  left: 50%;
  top: 50%;
  background-color: #000;
  video {
    width: 100%;
    height: 100%;
  }
}
</style>