import { get, post } from "./common";

export function getCaptcha() {
  return get<{ uuid:string, image: string}>('/admin/login/captcha/120_38')
}

export function login(data: {}) {
  return post('/admin/login', data)
}