import { request } from "../service";

export default {
  sendEmailCode,
  emailLogin,
  login,
  oauthLogin,
  listAuth
}

/**
 * 发送验证邮件
 */
function sendEmailCode(data: {
  email: string, 
  puzzleId: string
}) {
  return request<EmailVerify>({
    url: '/auth/email/send',
    method: 'post',
    data
  })
}
/**
 * 邮箱验证码登录/注册
 * 已经注册过的邮箱将会转为登录
 * 没有注册过的邮箱将自动注册
 */
function emailLogin(data: EmailLoginDto) {
  return request<void>({
    url: '/auth/email/login',
    method: 'post',
    data,
  })
}

/**
 * 使用账号和密码登录
 */
function login(data: LoginDto) {
  return request<void>({
    url: '/auth/login',
    method: 'post',
    data,
  })
}
/**
 * 第三方登录
 */
function oauthLogin(data: OAuthLogin) {
  return request({
    url: '/auth/oauth/login',
    method: 'post',
    data
  })
}
/**
 * 第三方登录方式
 */
function listAuth() {
  return request<Record<string, string>>({
    url: '/auth/oauth/list',
    method: 'get',
  })
}