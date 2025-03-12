import { request } from "@/apis/service"

export default {
  listStyles
}

export function listStyles(type: VideoType, size?: number) {
  return request<BangumiStyle[]>({
    url: '/bangumi/style/list',
    method: 'get',
    params: { type, size }
  })
}