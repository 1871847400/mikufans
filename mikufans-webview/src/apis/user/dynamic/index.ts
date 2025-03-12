import { request } from "@/apis/service";

export default {
  search,
  getById,
  getUnread,
  removeById,
  createShare,
}
/**
 * 搜索指定用户动态或所有关注用户的动态
 */
function search(params: UserDynamicParams) {
  return request<Page<UserDynamic>>({
    url: '/user/dynamic',
    method: 'get',
    params
  })
}
/**
 * 搜索指定动态
 */
function getById(id: string) {
  return request<UserDynamic>({
    url: '/user/dynamic/' + id,
    method: 'get',
  })
}
/**
 * 获取未读动态数
 */
function getUnread(type?: BusinessType) {
  return request<number>({
    url: '/user/dynamic/unread',
    method: 'get',
    params: {
      type
    }
  })
}
/**
 * 删除动态,会删除关联业务
 */
function removeById(id: string) {
  return request<boolean>({
    url: '/user/dynamic/' + id,
    method: 'delete',
  })
}
/**
 * 转发动态
 */
function createShare(data: UserDynamicShareDto) {
  return request<UserDynamic>({
    url: '/user/dynamic/share',
    method: 'post',
    data
  })
}