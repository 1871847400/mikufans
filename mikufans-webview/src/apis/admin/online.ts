import { request } from "../service";

export function getOnlineUsers(params: SearchParams) {
  return request<Page<OnlineUser>>({
    url: '/admin/online',
    method: 'get',
    params
  })
}