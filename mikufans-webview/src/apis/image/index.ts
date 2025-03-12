import { FILE_UPLOAD_MAX_SIZE } from '@/utils/consts';
import { baseURL, request } from "../service";
import axios, { AxiosRequestConfig } from 'axios'
import { compressImage } from '@/utils/image/compress_image';
import { filesize } from 'filesize';
import { uploadLargeFile } from '../file';
import { md5File } from '@/utils/file';

export const imageUrl = baseURL + '/image/download/'

/**
 * 1.上传本地图片
 * 2.上传第三方链接的图片
 * @returns 返回图片id
 */
export async function uploadImage(file: File | string, options?: AxiosRequestConfig) : Promise<string> {
  //如果是上传链接
  if (typeof file === 'string') {
    return request<string>({
      url: '/image/upload/url',
      method: 'post',
      data: { url: file }
    })
  }
  //如果图片已存在则取消上传,防止触发重复提交
  const md5 = await md5File(file)
  const res = await request<ImageResource>({ url: '/image/hash/' + md5 })
  if (res) {
    return res.id
  }
  const fileSize = file.size
  //图片超过3MB自动压缩
  if (fileSize > 3 * 1024 * 1024) {
    file = await compressImage(file, 1920*2, 1080*2, 0.8)
    logger.debug(`图片大于3MB,自动压缩:${filesize(fileSize)}=>${filesize(file.size)}`)
  }
  //如果图片超过限制大小,改为大文件上传模式
  if (file.size > FILE_UPLOAD_MAX_SIZE) {
    const taskId = await uploadLargeFile({ 
      file,
      onProgress: options?.onUploadProgress
    })
    return request<string>({ 
      url: '/image/upload/task/' + taskId, 
      method: 'post' 
    })
  }
  return request<string>({
    url: '/image/upload',
    method: 'post',
    timeout: 15_000,
    timeoutErrorMessage: '上传图片超时',
    data: { file },
    headers: {
      'content-type': 'multipart/form-data',
    },
    ...options
  })
}