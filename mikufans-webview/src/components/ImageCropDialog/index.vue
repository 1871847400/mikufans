<template>
  <el-dialog v-model="visible" center :title="title" width="710px" append-to-body :close-on-click-modal="false" @opened="opened" @close="close">
    <div class="box flex gap-4 justify-center">
      <div class="left">
        <div class="crop-zone">
          <img ref="imgRef" class="w-[500px]" :src="srcValue" alt="" draggable="false">
        </div>
        <div class="grey2 p-1 text-xs" v-if="size">
          <span>原图分辨率</span>
          <span class="pr-4">{{ size.width }}x{{ size.height }}</span>
          <span>截取分辨率</span>
          <span>{{ size.cropWidth }}x{{ size.cropHeight }}</span>
        </div>
      </div>
      <div class="right">
        <p class="text-center pb-2 select-none">预览图像</p>
        <canvas ref="canvasRef" width="200px" height="auto"></canvas>
      </div>
    </div>
    <div class="flex justify-center mt-2">
      <el-button type="primary" @click="submit" :loading="submitting" style="width: 120px; height: 40px;">确定</el-button>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import Cropper from 'cropperjs';
import 'cropperjs/dist/cropper.min.css'
const props = defineProps({
  title: string().def('裁剪图片'),
  ratio: number(),//要求比例,网页端首页视频封面16/9,手机端首页4/3
  round: bool().def(false), //预览图为圆形
  src: oneOfType([File, string()]).isRequired, //源文件或base64或url
})
const emits = defineEmits<{
  submit: [ File ]
}>()
const srcValue = computed(()=>{
  if (props.src instanceof File) {
    return URL.createObjectURL(props.src)
  } else {
    return props.src
  }
})
const visible = ref(true)
const imgRef = ref<HTMLImageElement>(null)
const canvasRef = ref<HTMLCanvasElement>(null)
const size = ref<{
  width: number,
  height: number,
  cropWidth: number,
  cropHeight: number,
}>()
let cropper : Cropper = null
function opened() {
  cropper = new Cropper(imgRef.value, {
    minCropBoxWidth: 10,
    minCropBoxHeight: 10,
    aspectRatio: props.ratio,
    cropBoxMovable: true,
    background: true, //网格背景
    movable: false,
    center: true,
    zoomable: false,
    ready(e) {
      //刚开始框选整个图片
      const { width, height } = cropper.getCanvasData()
      cropper.setCropBoxData({
        left: 0,
        top: 0,
        width,
        height,
      })
    },
    crop(e) {
      const data = cropper.getImageData()
      size.value = {
        width: data.naturalWidth,
        height: data.naturalHeight,
        cropWidth: e.detail.width|0,
        cropHeight: e.detail.height|0,
      }
      const previewData = cropper.getData(true)
      const ctx = canvasRef.value.getContext('2d');
      const width = 150
      const height = Math.min(width * 2, e.detail.height / e.detail.width * width)
      canvasRef.value.width = width
      canvasRef.value.height = height
      if (props.round) {
        ctx.beginPath();
        // x,y为中心点,radius半径画圆
        ctx.arc(width/2, height/2, width/2, 0, Math.PI * 2);
        ctx.clip();
      }
      ctx.drawImage(imgRef.value, previewData.x, previewData.y, previewData.width, previewData.height, 0, 0, width, height)
    },
  })
}
function close() {
  cropper?.destroy()
}
const submitting = ref(false)
function submit() {
  submitting.value = true
  cropper?.getCroppedCanvas().toBlob(async (blob)=>{
    try {
      const file = new File([blob], 'out.jpg', {
        type: 'image/jpg',
      })
      emits('submit', file)
      visible.value = false
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped lang="scss">

</style>