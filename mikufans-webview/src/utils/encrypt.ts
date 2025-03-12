import Buffer from 'buffer'

/**
 * 注意：atob转出来的并非字节流而是字符串，请使用Buffer库
 * 
 * ts视频类型: video/mp2t
 * m4s视频类型: video/iso.segment
 */
export function base64toBlob(base64: string, type: string)  {  
  // 将base64转为Unicode规则编码
  let bstr = atob(base64) 
  let len = bstr.length
  let u8arr = new Uint8Array(len)
  while (len--) {  
      u8arr[len] = bstr.charCodeAt(len) // 转换编码后才可以使用charCodeAt 找到Unicode编码
  }  
  return new Blob([u8arr], {  
    type,
  })
}