import SparkMD5 from 'spark-md5';

/**
 * 获得文件名前缀
 */
export function getPrefix(filename: string) {
  let idx = filename.lastIndexOf('.')
  return idx == -1 ? filename : filename.substring(0, idx)
}
/**
 * 修改文件名后缀
 */
export function suffix(filename: string, suffix?: string) {
  let result = filename
  let idx = filename.lastIndexOf('.')
  if(idx != -1) {
    result = filename.substring(0, idx)
  }
  return result + (suffix||'')
}

export function clearSuffix(filename: string) {
  let result = filename
  let idx = filename.lastIndexOf('.')
  if(idx != -1) {
    result = filename.substring(0, idx)
  }
  return result
}

/**
 * 增量hash运算大文件分片,防止内存溢出
 * 和直接验算单文件结果一样
 */
export function md5Chunks(chunks: Blob[]) {
  return new Promise<string>((resolve, reject)=>{
    const spark = new SparkMD5.ArrayBuffer
    function _read(i: number) {
      if (i>=chunks.length) {
        return resolve(spark.end())
      }
      const reader = new FileReader
      reader.onload = e=>{
        if (e.target.result instanceof ArrayBuffer) {
          spark.append(e.target.result)
        }
        _read(i+1)
      }
      reader.onerror = e=>{
        reject()
      }
      reader.readAsArrayBuffer(chunks[i])
    }
    _read(0)
  })
}
/**
 * 将文件分成多个blob块
 */
export function createChunks(file: File, chunkSize: number) {
  const chunks = <Blob[]>[]
  for (let i = 0; i < file.size; i += chunkSize) {
    chunks.push(file.slice(i, i + chunkSize))
  }
  return chunks
}

/**
 * 计算文件的MD5,如果文件大小大于10MB将会分片计算(节约内存)
 */
export function md5File(file: File) {
  let chunks : Blob[]
  const chunkSize = 1024 * 1024 * 10 //10MB
  if (file.size > chunkSize) {
    chunks = createChunks(file, chunkSize)
  } else {
    chunks = [file]
  }
  return md5Chunks(chunks)
}

/**
 * 计算字符串hash
 */
export function md5(str: string) {
  return SparkMD5.hash(str)
}

/**
 * 将Blob转为base64格式的字符串: data:URL格式的字符串（base64 编码）
 */
export function base64(blob: Blob) {
  return new Promise<string>((resolve)=>{
    const reader = new FileReader
    reader.onload = (e) => {
      if (typeof e.target.result === 'string') {
        resolve(e.target.result)
      }
    }
    reader.readAsDataURL(blob)
  })
}