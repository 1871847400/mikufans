<template>
  <div class="video-banner-selector">
    <clickable @click="onImageSelect">
      <div class="image-selector" :data-fill="!!modelValue" title="上传封面">
        <img v-if="imgUrl||placeholder" :src="imgUrl||placeholder" class="size-full" draggable="false"/>
        <div v-else class="flex-center flex-col size-full">
          <i class="iconfont icon-jia grey2"></i>
          <p>上传封面</p>
        </div>
      </div>
    </clickable>
    <div v-if="previews.length" class="flex-1">
      <div class="h-6 mb-1 flex items-center gap-2">
        <span class="text-xs grey1">请从以下随机截图中选择一张作为封面或者手动上传一张图片</span>
        <template v-if="!capturing">
          <el-button link type="primary" size="small" @click="randomCapture">重新截取</el-button>
        </template>
      </div>
      <el-scrollbar max-height="110px">
        <ul class="preview-list grid-cols-4 xl:grid-cols-3">
          <template v-for="file in previews">
            <clickable @click.prevent="model=file">
              <li :class="{'preview-item':true, select:model==file}">
                <img :src="getObjUrl(file)" draggable="false" class="size-full"/>
                <i class="iconfont icon-gou2"></i>
              </li>
            </clickable>
          </template>
        </ul>
      </el-scrollbar>
    </div>
  </div>
</template>

<script setup lang="ts">
import { captureImage } from '@/utils/video/screenshot';
import { FileUpload } from '@/stores/upload';
import { openDialog, openFileSelect } from '@/utils/dialog';
import ImageCropDialog from '@/components/ImageCropDialog/index.vue';
import { getVideoDuration } from '@/utils/video/duration';
const props = defineProps<{ placeholder?: string, files: FileUpload[] }>()
const model = defineModel<File>('modelValue', { default: null })
const imgUrl = useObjectUrl(model)
function getObjUrl(file: File) {
  return URL.createObjectURL(file)
}
const previews = ref<File[]>([])
const capturing = ref(false)
watchArray(()=>props.files, (value, oldValue, added)=>{
  //新增截图
  captureImages(added, false)
}, { immediate: true })
async function captureImages(files: FileUpload[], random = false) {
  try {
    for (const { file, hash } of files) {
      capturing.value = true
      const duration = await getVideoDuration(file)
      for (let i = 0; i < 4; i++) {
        capturing.value = true
        const offset = random ? duration * Math.random() : duration / 4 * i
        //一帧一帧截图,显示更快,更有连贯性
        const screenshot = await captureImage(file, offset)
        if (props.files.some(a=>a.hash===hash)) {
          if (previews.value.length >= 20) {
            previews.value.splice(previews.value.length - 1)
          }
          previews.value.unshift(screenshot[0])
          //如果还没有选择封面,则自动设置
          if (!model.value && !props.placeholder) {
            model.value = screenshot[0]
          }
        }
      }
    }
  } finally {
    capturing.value = false
  }
}
function randomCapture() {
  previews.value = []
  captureImages(props.files, true)
}
function onImageSelect() {
  openFileSelect({
    accept: 'image/*',
    callback(file) {
      openDialog(ImageCropDialog, { 
        src: file,
        ratio: 16/9,
        title: '裁剪封面',
        onSubmit(data) {
          model.value = data
        }
      })
    },
  })
}
</script>

<style scoped lang="scss">
.video-banner-selector {
  display: flex;
  align-items: flex-end;
  width: 100%;
}
.target-image {
  background-color: #fafafa;
  width: 240px;
  height: auto;
  aspect-ratio: 16/9;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-shrink: 0;
  align-items: center;
  color: var(--grey1);
  border-radius: 4px;
  overflow: hidden;
  user-select: none;
  cursor: pointer;
  margin-right: 40px;
  .icon-jia {
    font-size: 20px;
  }
  &[data-banner=false] {
    border: 1px dashed grey;
  }
}
.preview-list {
  display: grid;
  grid-template-rows: auto;
  grid-auto-flow: row;
  padding-right: 16px;
  gap: 12px;
}
.preview-item {
  /* width: 150px; */
  width: 100%;
  height: auto;
  aspect-ratio: 16/9;
  border-radius: 4px;
  position: relative;
  user-select: none;
  overflow: hidden;
  border: 3px solid transparent;
  cursor: pointer;
  &:hover:not(.disabled) {
    border-color: var(--blue1);
  }
  &.select {
    border-color: var(--blue1);
    &::before {
      content: '';
      background-color: #0000004a;
      position: absolute;
      inset: 0;
      z-index: 1;
    }
    .icon-gou2 {
      color: var(--blue1);
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translateX(-50%) translateY(-50%);
      font-size: 24px;
    }
  }
}
.video-banner-upload {
  @extend .preview-item;
  outline: 2px dashed gray;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  .svg-icon {
    width: 80px;
    height: 80px;
    fill: #ccc;
  }
}
.image-selector {
  width: 240px;
  height: auto;
  aspect-ratio: 16/9;
  margin-right: 20px;
  flex-shrink: 0;
  color: var(--grey1);
  border-radius: 4px;
  overflow: hidden;
  user-select: none;
  .icon-jia {
    font-size: 20px;
  }
  &[data-fill=false] {
    border: 1px dashed grey;
    &:hover {
      border-color: var(--blue1);
    }
  }
}
</style>