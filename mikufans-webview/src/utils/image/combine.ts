import { max } from "lodash"

/**
 * 混合多张图片
 */
export async function combieImage(srcList: string[]) {
  const imgList : HTMLImageElement[] = []
  const promises = []
  for (const src of srcList) {
    const img = document.createElement('img')
    img.src = src
    promises.push(new Promise<void>((resolve)=>{
      img.onload = ()=>{
        imgList.push(img)
        resolve()
      }
    }))
  }
  await Promise.all(promises)
  const canvas = document.createElement('canvas')
  canvas.width = max(imgList.map(a=>a.width))
  canvas.height = max(imgList.map(a=>a.height))
  const ctx = canvas.getContext('2d')
  const canvas2 = document.createElement('canvas')
  const ctx2 = canvas2.getContext('2d')
  for (const img of imgList) {
    canvas2.width = img.width
    canvas2.height = img.height
    ctx2.reset()
    ctx2.drawImage(img, 0, 0)
    const data = ctx2.getImageData(0, 0, img.width, img.height)
    ctx.putImageData(data, 0, 0)
    img.remove()
  }
  // const a = document.createElement('a')
  // a.href = canvas.toDataURL('image/png')
  // a.download = 'a'
  // a.click()
  const result = canvas.toDataURL('image/png')
  canvas.remove()
  canvas2.remove()
  return result
}