import { uploadImage } from "@/apis/image"
import videoApi from '@/apis/video';
import videoPartApi from '@/apis/video/part';
import { cloneDeep } from "lodash";

export async function submitVideo() {
  const uploadStore = useUploadStore()
  const { oldData, videoId, partList, files, banner, poster, reset } = useUploadStore()
  //复制一份,防止破坏原始数据
  const data = cloneDeep(uploadStore.data)
  if (files.some(a=>a.status==='uploading')) {
    return message.warning('当前还有上传任务正在进行中！')
  }
  //上传封面
  if (banner) {
    data.bannerId = await uploadImage(banner)
  }
  //上传海报
  if (poster) {
    data.bangumi.posterId = await uploadImage(poster)
  }
  const createParts : VideoPartDto[] = []
  const updateParts : VideoPartDto[] = []
  let sort = 1
  for (const p of partList) {
    //只上传成功的分集
    if (p.fileInfo.status !== 'success') {
      continue
    }
    let bannerId = null
    if (p.bannerFile) {
      bannerId = await uploadImage(p.bannerFile)
    }
    const part = {
      partName: p.partName,
      sort: sort++,
      bannerId,
      subtitles: p.subtitles,
    } as VideoPartDto
    if (p.oldData) {
      part.id = p.oldData.id
      updateParts.push(part)
    } else {
      part.videoId = videoId || '0', //0只占位,防后端校验
      part.taskId = p.fileInfo.taskId
      part.preprocess = {
        segments: p.segments.map(([start, end])=>({
          offset: start * p.duration,
          length: (end - start) * p.duration,
        })),
        watermark: p.watermark?.result ? {
          data: p.watermark.result,
          x: 0,
          y: 0,
        } : null,
      }
      createParts.push(part)
    }
  }
  if (data.type == 'VIDEO') {
    //防止后端校验
    delete data.bangumi
  } else {
    //用不到,只是占位
    data.channelId = '0'
  }
  if (data.dynamic.publishFlag == 0) {
    data.dynamic.publishTime = null
  }
  //存在videoId代表更新
  if (videoId) {
    for (const part of oldData.parts) {
      if (updateParts.findIndex(a=>a.id==part.id) === -1) {
        await videoPartApi.removeById(part.id)
      }
    }
    await videoApi.update({
      ...data,
      id: videoId,
      createParts,
      updateParts,
    })
    message.success('编辑成功！')
  } else {
    await videoApi.create({ ...data, createParts, updateParts, })
    message.success('投稿成功！')
  }
  reset()
  uploadStore.uploadSuccess = true
}