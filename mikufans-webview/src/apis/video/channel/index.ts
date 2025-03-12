import { request } from "@/apis/service";

export function listVideoChannels(params?: {
  size?: number //默认全部
  child?: boolean //是否包含子分区
}) {
  return request<VideoChannel[]>({
    url: '/video/channel/list',
    method: 'get',
    params,
    __cache__: true,
  })
}