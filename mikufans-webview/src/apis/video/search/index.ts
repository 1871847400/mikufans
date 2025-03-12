import { request } from "@/apis/service";

export default {
  getOptions,
}

/**
 * 搜索相关的过滤选项
 */
function getOptions() {
  return request<QueryOption[]>({
    url: '/video/search/options',
    method: 'get',
  })
}