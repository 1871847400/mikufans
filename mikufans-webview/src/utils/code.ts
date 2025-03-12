import { Axios, AxiosError } from "axios"

/**
 * 业务用到的错误代码 与 HTTP错误代码 相互独立！
 * 常量枚举，编译后变成"等号"后面的常量
 */
export const enum Code {
  OK = 0,
  ERROR = 1,
  NOT_LOGIN = 2,
  REDIRECT = 3,
  PRIVACY_DENY = 4,
  TOKEN_EXPIRED = 5,
  CAPTCHA = 6,
  FORBIDDEN = 7,
  LESS_COIN = 8,
  ENTITY_NOT_FOUND = 9,
  RESOURCE_NOT_FOUND = 10,
  LOW_LEVEL = 11,
  SERVER_ERROR = 12,
  REPEAT_SUBMIT = 13,
  DATA_EXISTED = 14,
  AUTH_DISABLED = 15
}

// http定义的错误码
export const errorMsgs = {
  404: '未找到资源',
  500: '服务器异常',
  502: '服务器未启动',
  // 其他异常,axios
  [AxiosError.ECONNABORTED]: '服务器响应超时',
  [AxiosError.ERR_NETWORK]: '网络连接失败',
  [AxiosError.ETIMEDOUT]: '请求超时',
  [AxiosError.ERR_CANCELED]: null,
}

export function getErrorMsg(statusCode: any) : string {
  if (Object.hasOwn(errorMsgs, statusCode)) {
    return errorMsgs[statusCode]
  } else if (typeof statusCode === 'number') {
    if (statusCode >= 500) {
      return '服务器异常'
    }
    if (statusCode >= 400) {
      return '本地异常'
    }
  } else {
    return null
  }
}