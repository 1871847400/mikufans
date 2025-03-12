/**
 * 获取视频文件的持续时间(秒)
 */
export function getVideoDuration(file: File) {
  return new Promise<number>((resolve)=>{
    const videoELe = document.createElement('video')
    const url = URL.createObjectURL(file)
    videoELe.src = url
    videoELe.ondurationchange = ()=>{
      resolve(videoELe.duration)
      videoELe.remove()
      URL.revokeObjectURL(url)
    }
    videoELe.onerror = ()=>{
      videoELe.remove()
      URL.revokeObjectURL(url)
    }
  })
}