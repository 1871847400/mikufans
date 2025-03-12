import { request, service } from "../service";

export default {
  fetch,
  search,
  update,
  getBackground,
  getAutoComplete,
  getSearchOptions,
}

function fetch(userId?: string, cache = false) {
  let url = userId ? '/user/' + userId : '/user/'
  return request<User>({
    url,
    method: 'get',
    __cache__: cache
  })
}
/**
 * 搜索用户列表
 */
function search(params?: UserSearchParams) {
  return request<Page<User>>({
    url: '/user/search',
    method: 'get',
    params
  })
}
/**
 * 获取用户昵称补全
 */
function getAutoComplete(keyword: string) {
  return request<User[]>({
    url: '/user/auto_complete',
    method: 'get',
    params: {
      keyword
    }
  })
}
/**
 * 修改自己的用户信息
 */
function update(data: UserDto & { id: string }) {
  return request<void>({
    url: '/user',
    method: 'put',
    data,
  })
}
/**
 * 获取所有可用背景
 * 背景名称 : 图片地址
 */
function getBackground() {
  return service<Record<string, string>>({
    url: '/static/background/metadata.json',
    method: 'get',
  }).then(res=>res.data)
}
function getSearchOptions() {
  return request<QueryOption[]>({
    url: '/user/search/options',
    method: 'get',
  })
}