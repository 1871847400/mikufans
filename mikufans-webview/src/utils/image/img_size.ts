/**
 * 获取图片的原始宽高
 * @param file 图片格式的文件
 * @returns 
 */
export function getImageSize(file: File) {
  return new Promise<{width:number,height:number}>((resolve, reject)=>{
    const url = URL.createObjectURL(file)
    const img = document.createElement('img')
    img.src = url
    img.hidden = true
    img.onload = ()=>{
      const width = img.naturalWidth
      const height = img.naturalHeight
      resolve({ width, height })
      img.remove()
      URL.revokeObjectURL(url)
    }
    img.onerror = (err)=>{
      reject(err)
      img.remove()
      URL.revokeObjectURL(url)
    }
  })
}