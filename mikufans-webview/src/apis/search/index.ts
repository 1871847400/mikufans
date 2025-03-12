import { request } from "../service";

export default {
  search,
  create,
  remove, 
  removeAll, 
  getHotkeys,
}

/**
 * 搜索历史记录
 */
function search(params: SearchParams) {
  return request<Page<SearchHistory>>({
    url: '/search/history',
    method: 'get',
    params,
  })
}
/**
 * 添加搜索记录
 */
function create(content: string) {
  return request<void>({
    url: '/search/history',
    method: 'post',
    params: {
      content
    }
  })
}
/**
 * 删除搜索记录
 */
function remove(content: string) {
  return request<void>({
    url: '/search/history',
    method: 'delete',
    params: {
      content
    }
  })
}

/**
 * 删除所有搜索记录
 */
function removeAll() {
  return request<void>({
    url: '/search/history/all',
    method: 'delete',
  })
}
/**
 * 获取热搜
 */
function getHotkeys() {
  return request<string[]>({
    url: '/search/history/hotkeys',
    method: 'get',
    __cache__: true,
  })
}
// export function searchVideo(params: VideoSearchParams) {
//   return request<Record<SearchType, Page<Video>>>({
//     url: '/search/video',
//     method: 'get',
//     params
//   })
// }
// export function searchUser(params: UserSearch) {
//   return request<Record<SearchType, Page<User>>>({
//     url: '/search/user',
//     method: 'get',
//     params
//   })
// }
// export function searchTotal(kw?: string) {
//   return request<Record<string, number>>({
//     url: '/search/total',
//     method: 'get',
//     params: { kw }
//   })
// }

// export function searchAll(params: SearchParamsCombine) {
//   return request<SearchResultCombine>({
//     url: '/search',
//     method: 'get',
//     params
//   })
// }

export function searchAll(kw: string) {
  return request<SearchResultCombine>({
    url: '/search',
    method: 'get',
    params: { kw }
  })
}