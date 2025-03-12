import { createChunks, md5Chunks } from "@/utils/file";
import axios, { AxiosProgressEvent } from "axios";
import { createUploadTask, mergeChunks, uploadChunk } from "./upload";

/**
 * 上传任务的进度回调
 * 返回 1:中断上传任务
 */
export type ProgressCallback = (progressEvent: AxiosProgressEvent) => void | 1
/**
 * 上传大文件
 * @returns 上传成功后返回taskId
 */
export async function uploadLargeFile(params: {
  file: File, //目标文件
  onProgress?: ProgressCallback
}) : Promise<string> {
  const file = params.file
  //分片大小,不能太大,防止网络慢导致连接超时
  const chunkSize = 1024 * 256
  //文件分片集
  const chunks = createChunks(file, chunkSize)
  //文件hash
  const md5 = await md5Chunks(chunks)
  //分片下标
  let chunkCode = 1
  //创建上传任务
  const task = await createUploadTask({
    chunkSize,
    md5,
    fileName: file.name,
    fileSize: file.size,
    mediaType: file.type
  })
  //如果任务已经成功
  if (task.uploadStatus==='SUCCESS' && task.chunkCode===task.chunkCount) {
    return task.id
  }
  //如果不是从0开始,代表断点续传
  if (task.chunkCode > 0) {
    //去除已上传的分片
    chunks.splice(0, task.chunkCode)
    chunkCode = task.chunkCode + 1
  }
  try {
    for(const chunk of chunks) {
      const chunkFile = new File([chunk], file.name, {
        lastModified: file.lastModified,
        type: file.type,
      })
      const cancelToken = axios.CancelToken.source()
      await uploadChunk({
        file: chunkFile,
        chunkCode,
        taskId: task.id,
      }, {
        cancelToken: cancelToken.token,
        onUploadProgress(e) {
          const loaded = (chunkCode - 1) * chunkSize + e.loaded //已经上传的总量
          const total = file.size
          if (typeof params.onProgress === 'function') {
            const result = params.onProgress({
              ...e,
              loaded,
              total
            })
            if (result === 1) {
              //中断会触发异常
              cancelToken.cancel('中断文件上传')
            }
          }
        },
      })
      chunkCode++
    }
    //所有分片上传完成后,请求合并
    await mergeChunks(task.id)
  } catch (err) {
    if (axios.isCancel(err)) {}
    throw err //抛出异常,中断外部执行逻辑
  }
  return task.id
}