/**
 * 压缩图片,返回压缩后的数据
 * 1.降低分辨率
 * 2.canvas降低品质(只支持jpeg和webp)
 * @param file 
 * @param maxWidth 
 * @param maxHeight 
 * @param quality 降低图片的品质,0-1
 */
export function compressImage(file: File, maxWidth: number, maxHeight: number, quality?: number) {
  return new Promise<File>((resolve, reject)=>{
    const img = new Image();
    const reader = new FileReader();
    reader.onload = function(e) {
      const dataURL = e.target.result;
      img.onload = function() {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        
        let targetWidth = img.width;
        let targetHeight = img.height;
        
        // 计算压缩后的图片尺寸
        if (targetWidth > maxWidth) {
          targetWidth = maxWidth;
          targetHeight = (targetWidth / img.width) * img.height;
        }
        
        if (targetHeight > maxHeight) {
          targetHeight = maxHeight;
          targetWidth = (targetHeight / img.height) * img.width;
        }
        
        // 设置canvas的宽高
        canvas.width = targetWidth;
        canvas.height = targetHeight;
        
        // 在canvas中绘制压缩后的图像
        ctx.drawImage(img, 0, 0, targetWidth, targetHeight);
        canvas.toBlob(blob=>{
          resolve(new File([blob], file.name, { type: file.type, lastModified: file.lastModified }))
        }, file.type, quality)
      }
      img.src = dataURL as string;
    }
    reader.readAsDataURL(file);
  })
}