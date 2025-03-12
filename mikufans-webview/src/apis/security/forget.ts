import { request } from "../service";

export default {
  sendEmail, 
  verifyEmail, 
  changePassword,
}

/**
 * 找回密码
 * 步骤1：发送邮件
 */
function sendEmail(data: {
  email: string, 
  puzzleId: string
}) {
  return request<EmailVerify>({
    url: '/auth/forget/email/send',
    method: 'post',
    data
  })
}

/**
 * 步骤2：验证邮箱
 */
function verifyEmail(data: { uuid: string, code: string, email: string }) {
  return request<void>({
    url: '/auth/forget/email/verify',
    method: 'post',
    data
  })
}

/**
 * 步骤3: 更改密码
 */
function changePassword(data: {uuid: string, password: string}) {
  return request<void>({
    url: '/auth/forget/email/pwd',
    method: 'put',
    data
  })
}