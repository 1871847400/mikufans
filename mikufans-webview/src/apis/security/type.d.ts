/**
 * 邮箱验证
 */
interface EmailVerify {
  uuid: string //请求id
  timeout: number //多久后失效(秒)
  interval: number //冷却时间(秒)
}
/**
 * 拼图验证码
 */
interface PuzzleResult {
  puzzle: string //base64
  image: string
  offset: number
  puzzleId: string
}
interface LoginDto {
  username: string
  password: string
  puzzleId?: string //如果有,需要先验证通过
}
interface EmailLoginDto extends EmailValidate {
  user?: UserDto
}
interface EmailValidate {
  uuid: string
  email: string
  code: string
}