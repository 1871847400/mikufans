import { _GettersTree, defineStore } from "pinia";
import { getPrefix, md5File } from "@/utils/file";
import { cloneDeep, find, isEqual, isEqualWith, isInteger, now, uniqueId } from "lodash";
import { uploadLargeFile } from "@/apis/file";
import { v4 as uuidv4 } from 'uuid';
import axios, { Axios, AxiosError } from "axios";
import { WatermarkData } from "@/pages/creator/upload/video/watermark/WatermarkDialog.vue";
import { getVideoDuration } from "@/utils/video/duration";

export interface ImageFile {
  url: string //blob url
  md5: string
  file: File
}
export interface FileUpload {
  file?: File //视频文件,编辑视频状态下=null
  hash: string //文件哈希
  speed: number //上传速度/s(字节)
  loaded: number //已上传大小(字节)
  total: number //总上传大小(字节)
  status: 'uploading' | 'exception' | 'success' | 'paused' //上传状态
  taskId?: string //上传成功后获得
}
export interface PartInfo {
  uuid: string //仅区分Part
  partName: string //分集标题
  fileInfo: FileUpload //视频文件
  bannerFile?: File //封面文件
  duration: number //时长(毫秒)
  subtitles: SubtitleFile[] //字幕
  segments: [number, number][] //裁剪片段
  watermark?: WatermarkData //水印
  oldData?: VideoPart //如果存在,代表该part之前已经上传过了,当前为编辑状态
}

const initialData = ()=>{
  return {
    channelId: '',
    title: '',
    intro: '',
    tags: [],
    bannerId: '',
    republish: 0,
    sourceUrl: '',
    userLevel: 0,
    dynamic: {
      visible: 1,
      publishFlag: 0,
      publishTime: '',
      commentArea: {
        commentFlag: 'NORMAL',
        userLevel: 0,
      },
    },
    bangumi: {
      releaseStatus: 0,
      styles: [],
    }
  } as VideoDto
}
export const useUploadStore = defineStore('upload', ()=>{
  //旧数据,编辑状态下存在
  const oldData = shallowRef<Video>(null)
  //提交数据
  const data = reactive(initialData())
  //封面图片
  const banner = shallowRef<File>(null)
  //海报图片
  const poster = shallowRef<File>(null)
  //分集列表
  const partList = ref<PartInfo[]>([])
  //文件列表
  const files = computed(()=>partList.value.map(part=>part.fileInfo))
  //上传新文件
  async function uploadFile(file: File, targetIndex?: number) {
    //限制分P数量
    if (partList.value.length >= 200 && !isInteger(targetIndex)) {
      return message.warning('超过上传数量限制')
    }
    //防止重复上传
    const hash = await md5File(file)
    if (partList.value.find(p=>p.fileInfo.hash===hash)) {
      return message.warning('已经上传过: ' + file.name)
    }
    //视频持续时长
    const duration = (await getVideoDuration(file)) * 1000
    //唯一id
    const uuid = uuidv4()
    if (!isInteger(targetIndex)) {
      targetIndex = partList.value.length
    }
    //分集名称
    const partName = getPrefix(file.name)
    partList.value[targetIndex] = {
      uuid,
      duration,
      segments: [],
      partName,
      subtitles: [],
      fileInfo: {
        file,
        hash,
        loaded: 0,
        speed: 0,
        status: 'paused',
        total: file.size,
      }
    }
    const part = partList.value[targetIndex]
    upload(part)
    if (data.title === '') {
      data.title = partName
    }
  }
  //从暂停状态继续上传
  function upload(part: PartInfo) {
    if (part.fileInfo.status !== 'paused') {
      return
    }
    part.fileInfo.status = 'uploading'
    let cache = []
    uploadLargeFile({
      file: part.fileInfo.file,
      onProgress: ({ loaded, total, bytes }) => {
        //如果文件被移除了或暂停
        if (!partList.value.some(a=>a.uuid===part.uuid) || part.fileInfo.status==='paused') {
          return 1
        }
        if (total) {
          part.fileInfo.total = total
        }
        part.fileInfo.loaded = loaded
        //记录本次上传的时间和量
        cache.push([now(), bytes])
        //删除超过5秒的上传记录
        cache = cache.filter(a=>a[0] >= now() - 5000)
        part.fileInfo.speed = cache.reduce((prev, cur)=>prev+cur[1], 0) / 5
      }
    }).then(taskId=>{
      part.fileInfo.taskId = taskId
      part.fileInfo.status = 'success'
    }).catch(err => {
      if (axios.isCancel(err)) {
        logger.debug('取消上传文件,原因:', err.message)
      } else {
        logger.error('上传文件时异常', err)
        part.fileInfo.status = 'exception'
      }
    })
  }
  //显示投稿成功画面
  const uploadSuccess = ref(false)
  //正在编辑的视频id
  const videoId = computed(()=>oldData.value?.id)
  //当前选择的视频类型
  const videoType = toRef(data, 'type')
  function reset() {
    partList.value = []
    banner.value = null
    poster.value = null
    oldData.value = null
    Object.assign(data, initialData())
    uploadSuccess.value = false
  }
  //是否填写过数据
  const hasChanged = computed(()=>{
    const curData = cloneDeep(toRaw(data))
    delete curData.type
    return partList.value.length || !isEqual(curData, initialData())
  })
  return {
    videoId,
    oldData,
    videoType,
    reset,
    data,
    files,
    partList,
    banner,
    poster,
    uploadFile,
    upload,
    uploadSuccess,
    hasChanged
  }
})