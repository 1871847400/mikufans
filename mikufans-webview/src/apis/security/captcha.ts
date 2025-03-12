import { request } from "../service";

export default {
  generate, validate
}

/**
 * 生成拼图验证码
 */
function generate() {
  return request<PuzzleResult>({
    url: '/auth/captcha/generate',
    method: 'get',
  })
}

/**
 * 校验验证码
 */
function validate(puzzleId: string, value: number) {
  return request<void>({
    url: '/auth/captcha/validate',
    method: 'post',
    data: {
      puzzleId, 
      value
    }
  })
}