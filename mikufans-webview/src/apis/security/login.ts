import { request } from "../service";

export default {
  sendEmailCode,
  emailLogin,
  login,
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
 * 邮箱验证码登录
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