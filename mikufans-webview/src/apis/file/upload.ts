import { AxiosRequestConfig, CancelToken } from "axios";
import { request } from "../service";

export function listUploadTask(status?: UploadStatus[]) {
  return request<Page<UploadTask>>({
    url: '/upload/list',
    method: 'get',
    params: { status }
  })
}

export function createUploadTask(data: UploadTaskDto) {
  return request<UploadTask>({
    url: '/upload/create',
    method: 'post',
    data
  })
}

export function uploadChunk(data: { 
  file: File, 
  chunkCode: number, 
  taskId: string,
}, config?: AxiosRequestConfig) {
  return request<void>({
    url: `/upload/chunk/${data.taskId}/${data.chunkCode}`,
    method: 'post',
    data: {
      file: data.file
    },
    timeout: 30_000,
    timeoutErrorMessage: '上传文件超时',
    headers: {
      'content-type': 'multipart/form-data',
    },
    ...config
  })
}

export function mergeChunks(taskId: string) {
  return request<void>({
    url: `/upload/merge/${taskId}`,
    method: 'post',
  })
}