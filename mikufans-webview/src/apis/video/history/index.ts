import { request } from "@/apis/service";

export default {
  search, record, remove, removeAll
}

/**
 * 搜索观看历史
 */
function search(params: VideoHistoryParams) {
  return request<Page<VideoWatchHistory>>({
    url: '/video/history/search',
    method: 'get',
    params,
  })
}

/**
 * 记录观看位置,方便下次续播
 */
function record(data: {
  videoId: string, 
  partId: string, 
  watchPos: number
}) {
  return request<void>({
    url: '/video/history',
    method: 'post',
    data
  })
}

/**
 * 删除观看历史
 */
function remove(id: string) {
  return request<boolean>({
    url: '/video/history/' + id,
    method: 'delete',
  })
}

/**
 * 删除所有观看历史
 */
function removeAll() {
  return request<void>({
    url: '/video/history/clear',
    method: 'delete',
  })
}