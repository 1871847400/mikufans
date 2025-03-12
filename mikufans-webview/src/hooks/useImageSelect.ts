import { uploadImage } from "@/apis/image"
import { openFileSelect } from "@/utils/dialog"
import { md5File } from "@/utils/file"
import { clamp, uniqueId } from "lodash"

export interface ImageSelect {
  id: string //唯一id
  file: File 
  hash: string //md5
  blobUrl: string
  uploadStatus: 'none'|'uploading'|'success'|'error'//上传状态
  progress: number //上传进度 0-100
  resId?: string //上传完成后的图片id
}

export function useImageSelect(params: {
  accept?: string
  multiple?: boolean, //可以多选
  max?: number, //总共可以选择多少个文件
  reset?: boolean, //每次选择文件后,清除之前选择的
  autoUpload?: boolean //自动上传
}) {
  const files = ref<ImageSelect[]>([])
  async function open() {
    const selectFiles = await openFileSelect({
      accept: 'image/*',
      ...params
    })
    if (selectFiles.length === 0) {
      return
    }
    if (params.reset) {
      files.value = []
    }
    const max = params.max ?? Number.MAX_VALUE
    const overflow = selectFiles.length + files.value.length - max
    if (overflow > 0) {
      message.warning(`最多选择${max}张图片！`)
      selectFiles.splice(selectFiles.length - overflow)
      if (selectFiles.length === 0) {
        return
      }
    }
    const newFiles : ImageSelect[] = []
    for (const file of selectFiles) {
      const id = uniqueId('image-')
      const hash = await md5File(file)
      const blobUrl = URL.createObjectURL(file)
      newFiles.push({ 
        id, 
        file, 
        hash, 
        blobUrl, 
        uploadStatus: 'none',
        progress: 0,
      })
    }
    files.value = [...files.value, ...newFiles]
    if (params.autoUpload) {
      for (const { id } of newFiles) {
        const file = files.value.find(a=>a.id===id)
        if (file) {
          file.uploadStatus = 'uploading'
          uploadImage(file.file, {
            onUploadProgress(e) {
              file.progress = clamp(e.loaded / file.file.size * 100, 0, 100)
            },
          }).then(resId=>{
            file.resId = resId
            file.uploadStatus = 'success'
          }).catch(()=>{
            file.uploadStatus = 'error'
          })
        }
      }
    }
  }
  watch(files, (newFiles, oldFiles)=>{
    //url绑定document的生命周期,必须手动释放
    if (oldFiles) {
      oldFiles.filter(a=>!newFiles.some(b=>a.id===b.id))
        .forEach(a=>{
          URL.revokeObjectURL(a.blobUrl)
        })
    }
  })
  tryOnUnmounted(()=>{
    files.value = [] //会触发watch
  })
  return { open, files }
}