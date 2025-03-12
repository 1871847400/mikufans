<template>
  <li class="flex gap-4">
    <!-- .handle才允许拖动排序 -->
    <div class="part-ico handle" title="拖动可调整顺序">P{{ index }}</div>
    <div class="flex-1">
      <input-text
        class="w-1/2 mb-2 text-base"
        v-model="part.partName"
        :maxlength="32"
        show-word-limit
        :placeholder="placeholder"
      />
      <el-progress class="py-1" :percentage="percentage" :status="status" :show-text="false" />
      <div class="flex items-center text-xs grey2">
        <template v-if="part.fileInfo.status==='success'">
          <span>上传成功</span>
          <i class="iconfont icon-gou ml-2" style="color: green;"></i>
          <span class="ml-4 min-w-[90px]">时长：{{ displayDuration(part.duration) }}</span>
          <span class="ml-4 min-w-[110px]">大小：{{ filesize(part.fileInfo.total) }}</span>
        </template>
        <span v-else-if="part.fileInfo.status=='exception'">上传失败</span>
        <span v-else>{{ speedInfo }}</span>
        <template v-if="part.fileInfo.status=='success'">
          <el-button class="ml-2" link type="primary" @click="option=!option">高级选项</el-button>
        </template>
        <div v-if="part.oldData" class="ml-6 text-sm">
          <el-text v-if="part.oldData.processFail" type="danger" size="default">服务器处理异常,请删除后重新上传！</el-text>
          <span v-else-if="part.oldData.progress<100">等待处理{{ part.oldData.progress }}%</span>
          <span v-else-if="part.oldData?.sysAudit?.auditStatus==='UNKNOWN'" class="blue0">等待审核中</span>
          <span v-else-if="part.oldData?.sysAudit?.auditStatus==='FAIL'" class="text-red-400" :title="'原因:'+part.oldData?.sysAudit.auditReason">审核未通过</span>
        </div>
        <div class="ml-auto">
          <template v-if="part.fileInfo.status=='uploading'||part.fileInfo.status=='paused'">
            <el-button link type="primary" @click="pause">{{ part.fileInfo.status=='uploading' ? '暂停' : '继续' }}</el-button>
          </template>
          <el-button v-if="!part.oldData?.processFail" link type="warning" @click="change">更换</el-button>
          <el-popconfirm
            width="150"
            hide-icon
            title="你确定删除吗?"
            @confirm="remove"
          >
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
      <height-transition :expand="option">
        <div class="bg1 mt-1">
          <el-form label-suffix=":" class="p-2">
            <el-form-item label="封面">
              <banner-selector v-model="bannerFile" :files="[part.fileInfo]" />
            </el-form-item>
            <el-form-item label="裁剪" v-if="part.fileInfo.file">
              <div class="flex gap-2 mr-3">
                <el-tag v-for="i in part.segments" size="large">
                  {{ displayDuration(i[0]*part.duration) }}~{{ displayDuration(i[1]*part.duration) }}
                </el-tag>
              </div>
              <el-button @click="slice">编辑</el-button>
            </el-form-item>
            <el-form-item label="水印" v-if="firstFrame">
              <clickable @click="watermark">
                <div class="relative w-[240px] overflow-hidden">
                  <img 
                    v-if="part.watermark" 
                    :src="part.watermark.result"
                    class="absolute w-full left-0 top-0"
                  >
                  <img :src="firstFrame" alt="" class="w-full">
                </div>
              </clickable>
            </el-form-item>
            <el-form-item label="字幕">
              <subtitle-select v-model="part.subtitles"/>
            </el-form-item>
          </el-form>
        </div>
      </height-transition>
    </div>
  </li>
</template>

<script setup lang="ts">
import { PartInfo } from '@/stores/upload';
import { displayDuration } from '@/utils/common';
import { openDialog, openFileSelect } from '@/utils/dialog';
import VideoPartSplit from '../split/VideoPartSplit.vue'
import { filesize } from 'filesize';
import BannerSelector from '../section/BannerSelector.vue';
import WatermarkDialog from '../watermark/WatermarkDialog.vue';
import { captureImage } from '@/utils/video/screenshot';
import SubtitleSelect from '../subtitle/SubtitleSelect.vue';
import { getImageData } from '@/apis/image/data';
const { part } = defineProps<{ index: number, part: PartInfo }>()
const placeholder = toRaw(part.partName)
const { partList, videoType } = toRefs(useUploadStore())
const { upload, uploadFile } = useUploadStore()
const bannerFile = customRef((track, trigger)=>{
  const bannerId = part.oldData?.bannerId
  let oldFile : File = null
  if (bannerId && bannerId != '0') {
    getImageData(bannerId, 'blob').then(blobData=>{
      oldFile = new File([blobData.data], 'banner.jpg', { type: 'image/jpg' })
      trigger()
    })
  }
  return {
    get() {
      track()
      return part.bannerFile || oldFile
    },
    set(file) {
      part.bannerFile = file
      trigger()
    }
  }
})
const option = ref(false)
//进度条状态
const status = computed(()=>{
  if (part.fileInfo.status === 'exception' || part.fileInfo.status === 'success') {
    return part.fileInfo.status
  }
})
//进度条值
const percentage = computed(()=>{
  return status.value=='success' ? 100 : ((part.fileInfo.loaded / part.fileInfo.total) * 100) | 0
})
const speedInfo = computed(()=>{
  const f = part.fileInfo
  const loaded = filesize(f.loaded)
  const total = filesize(f.total)
  const speed = filesize(f.speed)
  const progress = (f.loaded/f.total*100)|0
  const time = displayDuration((f.total-f.loaded)/f.speed*1000, true)
  let str = `已经上传：${loaded}/${total}`
  if (part.fileInfo.status === 'uploading') {
    str += `------当前速度：${speed}/s-----剩余时间：${time}`
  }
  return str + `-----进度：${progress}%`
})
function preview() {
  
}
function slice() {
  const url = URL.createObjectURL(part.fileInfo.file)
  openDialog(VideoPartSplit, {
    url,
    selections: part.segments,
    onSubmit(data) {
      //不使用v-model,在dialog保存后才更新值
      part.segments = data
    }
  })
}
//视频第一帧画面
const firstFrame = ref<string>(null)
if (part.fileInfo.file) {
  captureImage(part.fileInfo.file, 0).then(files=>{
    firstFrame.value = URL.createObjectURL(files[0])
  })
}

function watermark() {
  openDialog(WatermarkDialog, { 
    file: part.fileInfo.file,
    data: part.watermark,
    onSubmit(data) {
      part.watermark = data
    }
  })
}
function pause() {
  if (part.fileInfo.status === 'paused') {
    upload(part)
  } else {
    part.fileInfo.status = 'paused'
  }
}
async function change() {
  openFileSelect({
    accept: 'video/*,video/x-matroska',
    callback(file) {
      const index = partList.value.findIndex(a=>a.uuid===part.uuid)
      uploadFile(file, index>=0 ? index : null)
    },
  })
}
function remove() {
  partList.value = partList.value.filter(a=>a.uuid!==part.uuid)
}
</script>

<style scoped lang="scss">
.part-ico {
  cursor: grabbing;
  width: 44px;
  margin-top: 10px;
  text-indent: 8px;
  padding-top: 5px;
  color: #fff;
  user-select: none;
  background-image: url('@/assets/images/video.png');
  background-position: top;
  background-repeat: no-repeat;
  background-size: contain;
}
</style>