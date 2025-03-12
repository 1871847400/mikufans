import { request, baseURL } from "@/apis/service";
import axios, { AxiosRequestConfig, AxiosResponse } from 'axios'
import { Buffer } from 'buffer'

export default {
  getMasterUrl,
  parseResponse,
  getThumnailsVttUrl,
  getThumnailsImgUrl,
}

export const SubtitleTypes : SubtitleType[] = ['VTT', 'SRT', 'ASS']

function getMasterUrl(partId: string) {
  return baseURL + '/video/resource/download/' + partId + '/master'
}

function getThumnailsVttUrl(partId: string) {
  return `${baseURL}/video/resource/download/${partId}/thumbnails.vtt`
}

function getThumnailsImgUrl(partId: string) {
  return `${baseURL}/video/resource/download/${partId}/thumbnails.jpg`
}

/**
 * 解析服务器响应的分片数据
 *  如果返回结果是text则代表链接列表
 *  每个链接的下载内容为base64的分片
 *  需要按顺序下载后合并再解码
 */
function parseResponse(response: XMLHttpRequest | AxiosResponse) {
  const isText = (contentType: string)=>
    contentType.includes('text') || contentType.includes('json')
  if (response instanceof XMLHttpRequest) {
    const contentType = response.getResponseHeader('content-type')
    if (isText(contentType)) {
      let responseText = ''
      if (response.responseType==='arraybuffer') {
        responseText= Buffer.from(response.response).toString('utf-8')
      } else {
        responseText = response.responseText
      }
      return downloadUrl(responseText)
    } else {
      return response.response as ArrayBuffer
    }
  } else {
    const contentType = response.headers['content-type']
    const data : Blob = response.data
    if (isText(contentType)) {
      return data.text().then(downloadUrl)
    } else {
      return data.arrayBuffer()
    }
  }
}

type ResponseObject = {
  mode: TransferMode
  links: string[]
}

function downloadUrl(text: string) {
  const { mode, links } : ResponseObject = JSON.parse(text)
  if (mode === 'CLOUD') {
    const tasks = <Promise<AxiosResponse>[]>[]
    for (const url of links) {
      const task = axios({
        url,
        method: 'get',
        timeout: 30_000,
        responseType: 'text',
      })
      tasks.push(task)
    }
    //当所有下载任务成功后按顺序合并
    return Promise.all(tasks).then(res => {
      const merge = res.map(a => a.data).join('')
      return Buffer.from(merge, 'base64').buffer
    })
  }
}