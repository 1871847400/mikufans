import axios, { Axios, AxiosError, AxiosRequestConfig } from 'axios'
import logger from '@/utils/logger'
import { Code, getErrorMsg } from '@/utils/code'
import { getAccessToken, refreshToken, setAccessToken, setRefreshToken, refreshTokenHeader, adminTokenHeader, setAdminToken, getAdminToken } from '@/utils/token'
import { md5 } from '@/utils/file'
import { LRUCache } from '@/utils/cache'
import router from '@/router'

export const baseURL = import.meta.env.VITE_REQUEST_PATH

export const service = axios.create({
  baseURL,
  timeout: 30_000,
  headers: {
    'content-type': 'application/json;charset=utf-8',
  },
  responseType: 'json', //设置响应类型,默认json
})

/**
 * 请求拦截器
 *  注意不应该在访问其它网站时带上token,例如出现302时
 *  也不应该在header带上其它任何标记,以免被其它网站forbid
 */
service.interceptors.request.use(async (config) => {
  const accessToken = config.url.startsWith('/admin') ? getAdminToken() : getAccessToken()
  if (accessToken) {
    config.headers.authorization = 'Bearer ' + accessToken
  }
  return config
})

/**
 * 响应拦截器,支持返回promise
 * 第一个参数为执行成功的回调(Http Code==2XX)
 * 第二个参数为执行失败的回调(Http Code!=2XX)
 */
service.interceptors.response.use(response=>{
  //如果响应头带有token则更新token
  const accessToken = response.headers.authorization
  if (accessToken) {
    logger.debug('accessToken', accessToken)
    setAccessToken((accessToken+'').replace('Bearer ', ''))
  }
  const refreshToken = response.headers[refreshTokenHeader]
  if (refreshToken) {
    logger.debug('refreshToken', refreshToken)
    setRefreshToken(refreshToken)
  }
  const adminToken = response.headers[adminTokenHeader]
  if (adminToken) {
    logger.debug('adminToken', adminToken)
    setAdminToken(adminToken)
  }
  return response
}, (err) => {
  //不会处理到上面抛出的异常
  //处理HTTP发生的异常如404,500
  let msg = err+''
  if (err instanceof AxiosError) {
    const status = err.response?.status || err.code
    msg = getErrorMsg(status)
  }
  if (msg) {
    message.error(msg)
  }
  //这里需要再次抛出异常,否则调用该请求的方法会进入then
  //不要改变原本的err,方便catch判断err类型
  throw err
})

/**
 * 缓存GET请求的结果 (会将参数作为key)
 * 应该优先使用cache响应头
 * 存储promise,而不是响应结果,防止两次请求间隔太低,造成重复请求
 */
const requestCache = new LRUCache<string, Promise<any>>(1000)

interface RequestConfig extends AxiosRequestConfig {
  __cache__?: boolean //是否缓存
}
/**
 * 将响应结果进行解析后再返回实际数据
 * 如果响应结果非标准响应,应该使用service发送请求
 */
export function request<T>(config: RequestConfig) : Promise<T> {
  let cacheKey = ''
  if (config.method?.toLowerCase() === 'get') {
    //GET请求时将数组型参数转为字符串 [1,2,3] => 1,2,3
    //默认: param[]=1&param[]=2
    if (typeof config.params === 'object') {
      for (const key in config.params) {
        const val = config.params[key]
        if (Array.isArray(val)) {
          config.params[key] = val.join(',')
        }
      }
    }
    
    if (config.__cache__) {
      cacheKey = md5(config.url + JSON.stringify(config.params || ''))
      const response = requestCache.get(cacheKey)
      if (response) {
        return response
      }
    }
  }

  const response = service.request(config).then(async response=>{
    //规定获取数据后转换成什么类型，如果不为json则不使用包装
    const responseType = response.request?.responseType
    const contentType = response.headers['content-type']
    //如果响应类型不是json则不覆盖默认的返回类型
    if (!contentType || !contentType.includes('application/json') || (responseType && responseType !== 'json')) {
      return response
    }
    const isAdmin = config.url.startsWith('/admin')

    const code = response.data.code as number
    const msg = response.data.msg as string
    const data = response.data.data as any
  
    //当token无效或不存在时,删除双token
    //包括要求登录后才能用的操作
    if (code === Code.NOT_LOGIN) {
      logger.debug('token无效或不存在,未登录！')
      if (isAdmin) {
        setAdminToken(null)
      } else {
        setAccessToken(null)
        setRefreshToken(null)
      }
    }
  
    //accessToken到期,尝试用refreshToken刷新后再次请求
    if (code === Code.TOKEN_EXPIRED) {
      //如果是管理员请求,直接去登录页
      if (isAdmin) {
        setAdminToken(null)
        await router.push('/admin/login')
        message.error('token已到期,请重新登录！')
        return
      }
      logger.debug('token已到期,尝试刷新...')
      try {
        await refreshToken()
      } catch {
        //如果刷新不成功,清除双token
        setAccessToken(null)
        setRefreshToken(null)
        //删除请求配置中的token,以免死循环
        delete response.config.headers.authorization
      }
      //无论当前是否携带token都重新发送请求
      return request(response.config)
    }

    //被禁止登录
    if (code === Code.AUTH_DISABLED) {
      if (isAdmin) {
        setAdminToken(null)
      } else {
        setAccessToken(null)
        setRefreshToken(null)
      }
    }
    
    // 手动重定向,在使用HTTP的sendDirect(403)有跨域问题时才使用此办法
    if (code === Code.REDIRECT) {
      return request({ url: data })
    }
    if (code !== Code.OK) {
      //提示错误内容
      message.error(msg || '未指定错误内容,code:'+code)
      logger.error(response, code)
      // 抛出自定义异常,调用的方法会进入catch
      throw { code, msg, data }
    }

    if (config.__cache__ && cacheKey) {
      requestCache.set(cacheKey, data)
    }

    //响应是成功的，所以直接返回数据
    return data
  })
  if (cacheKey) {
    requestCache.set(cacheKey, response)
  }
  return response
}

//方便在浏览器中进行调试
if (import.meta.env.DEV && window) {
  window['request'] = request
}