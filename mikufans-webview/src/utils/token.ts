import { request } from "@/apis/service";
import storage from "./storage";

export const refreshTokenHeader = 'refresh-token'
export const adminTokenHeader = 'admin-token'

export function getAccessToken() {
  return storage.getItem('access_token', '')
}

export function setAccessToken(token: string) {
  return storage.setItem('access_token', token)
}

export function getRefreshToken()  {
  return storage.getItem('refresh_token', '')
}

export function setRefreshToken(token: string) {
  return storage.setItem('refresh_token', token)
}

export function getAdminToken() {
  return storage.getItem('admin_token', '')
}

export function setAdminToken(token: string) {
  return storage.setItem('admin_token', token)
}

//防止并发请求
let refreshTokenPromise = null
//使用refreshToken,刷新两个token的有效时间
export function refreshToken() {
  if (refreshTokenPromise) {
    return refreshTokenPromise
  }
  refreshTokenPromise = request<boolean>({
    method: 'post',
    url: '/auth/refresh-token',
    headers: { [refreshTokenHeader]: getRefreshToken() },
  })
  refreshTokenPromise.finally(()=>{
    refreshTokenPromise = null
  })
  return refreshTokenPromise
}