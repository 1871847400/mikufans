/**
 * 截图视频中指定时间点的图片
 * @param file 视频文件
 * @param times 时间(秒)
 */
export function captureImage(file: File, ...times: number[]) {
  return new Promise<File[]>((resolve)=>{
    if (!times?.length) {
      return resolve([])
    }
    const videoEle = document.createElement('video')
    //浏览器自动播放策略:必须静音
    videoEle.muted = true
    videoEle.autoplay = true
    videoEle.controls = false
    const objUrl = URL.createObjectURL(file)
    videoEle.src = objUrl
    const result = []
    let index = 0
    const canvas = document.createElement('canvas')
    videoEle.oncanplay = ()=>{
      if (index === 0) {
        canvas.width = videoEle.videoWidth
        canvas.height = videoEle.videoHeight
        videoEle.currentTime = times[index++]
      }
    }
    videoEle.onseeked = ()=>{
      // console.log('seek', videoEle.currentTime);
      const ctx = canvas.getContext('2d')
      ctx.reset()
      ctx.drawImage(videoEle, 0, 0)
      canvas.toBlob(async data=>{
        const filename = videoEle.currentTime + '.png'
        result.push(new File([data], filename, {type:'image/png'}))
        if (index >= times.length) {
          canvas.remove()
          videoEle.remove()
          URL.revokeObjectURL(objUrl)
          resolve(result)
        } else {
          videoEle.currentTime = times[index++]
        }
      })
    }
  })
}