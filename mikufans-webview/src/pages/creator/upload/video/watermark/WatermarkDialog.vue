<template>
  <el-dialog v-model="visible" width="800px" center title="添加水印">
    <div class="flex gap-2">
      <div class="flex-1 relative overflow-hidden h-fit">
        <video ref="video" :src="value" class="w-full" autoplay muted @contextmenu.prevent loop></video>
        <div ref="el" class="watermark" :style="style">
          <span v-if="mode==0" :style="textStyle">{{ text.value }}</span>
          <img v-else-if="mode==1" :src="image.value" alt="" :style="imgStyle">
        </div>
      </div>
      <div class="w-[200px] px-2">
        <el-radio-group v-model="mode">
          <el-radio :value="0">文字水印</el-radio>
          <el-radio :value="1">图片水印</el-radio>
        </el-radio-group>
        <el-divider class="my-1" />
        <el-form v-if="mode==0" label-position="top" @submit.prevent>
          <el-form-item label="内容">
            <el-input v-model="text.value" />
          </el-form-item>
          <el-form-item label="大小">
            <el-select v-model="text.size" style="width: 70px;">
              <el-option v-for="i in 14" :label="9+i" :value="9+i"/>
            </el-select>
          </el-form-item>
          <el-form-item label="颜色">
            <el-color-picker v-model="text.color" show-alpha @active-change="text.color=$event"/>
          </el-form-item>
        </el-form>
        <el-form v-else-if="mode==1" @submit.prevent>
          <el-form-item>
            <el-button @click="imgSelect">选择图片</el-button>
          </el-form-item>
          <el-form-item label="图片大小">
            <el-input-number v-model="image.width" :step="3" :min="10" :max="100" />
          </el-form-item>
        </el-form>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" class="mx-auto w-[200px] h-[40px]" size="large" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { openFileSelect } from '@/utils/dialog';
export interface WatermarkData {
  mode: number
  x: number
  y: number
  text: {
    value: string
    size: number
    color: string
  },
  image: {
    value: string
    width: number
  },
  result?: string
}
const props = defineProps<{
  file: File
  data?: WatermarkData
}>()
const emits = defineEmits<{
  submit: [data: WatermarkData]
}>()
const visible = ref(true)
const { value } = useObjectUrl(props.file)
const video = ref<HTMLVideoElement>()
const data = reactive<WatermarkData>(props.data || {
  mode: 0,
  x: 0,
  y: 0,
  text: {
    value: '我是水印',
    size: 16,
    color: '#fff'
  },
  image: {
    value: '',
    width: 30,
  }
})
const { mode, text, image } = toRefs(data)
const el = ref<HTMLElement>()
const { x, y, style } = useDraggable(el, {
  initialValue: { x: data.x, y: data.y },
  containerElement: toRef(()=>el.value?.parentElement)
})
const textStyle = computed(()=>({
  color: text.value.color,
  fontSize: text.value.size + 'px',
}))
const imgStyle = computed(()=>({
  width: image.value.width + 'px',
}))
function imgSelect() {
  openFileSelect({
    accept: 'image/*',
    callback(file) {
      data.image.value = URL.createObjectURL(file)
    },
  })
}
function submit() {
  const { videoWidth, videoHeight, clientWidth, clientHeight } = video.value
  const scale = videoWidth / clientWidth
  const canvas = document.createElement('canvas')
  canvas.width = videoWidth
  canvas.height = videoHeight
  const offsetX = x.value / clientWidth * videoWidth
  const offsetY = y.value / clientHeight * videoHeight
  const ctx = canvas.getContext('2d')
  if (mode.value == 0) {
    ctx.fillStyle = textStyle.value.color
    ctx.font = text.value.size * scale + 'px Microsoft YaHei UI'
    ctx.fillText(text.value.value, offsetX, offsetY + text.value.size * scale)
  } else if (mode.value == 1) {
    const img = document.createElement('img')
    img.src = data.image.value
    const dw = image.value.width * scale
    const dh = img.height / img.width * dw
    ctx.drawImage(img, offsetX, offsetY, dw, dh)
  }
  data.x = x.value
  data.y = y.value
  data.result = canvas.toDataURL('image/png')
  emits('submit', toRaw(data))
  canvas.remove()
  visible.value = false
}
</script>

<style scoped lang="scss">
.watermark {
  position: absolute;
  padding: 4px;
  border: 2px dashed transparent;
  cursor: move;
  user-select: none;
  span {
    white-space: nowrap;
    font-family: Microsoft YaHei UI;
  }
  img {
    pointer-events: none;
  }
  &:hover, &:focus {
    border-color: #eee;
  }
}
</style>