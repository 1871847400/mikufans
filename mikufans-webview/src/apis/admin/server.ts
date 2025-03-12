import { request } from "../service";

/**
 * 获取服务器信息
 */
export function getServerInfo() {
  return request<ServerInfo>({
    url: '/admin/server/info',
    method: 'get',
  })
}