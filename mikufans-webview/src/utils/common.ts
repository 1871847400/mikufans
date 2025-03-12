import { clone, isEqual, isNull, isUndefined, toLower } from "lodash"
import dayjs, { duration } from 'dayjs'

/**
 * 深度重置对象里的string为空串和number为0,其它为null
 */
export function resetObject(...objs: object[]) {
  for (const obj of objs) {
    for (let k in obj) {
      let v = obj[k]
      if (typeof v == 'string') {
        obj[k] = ''
      } else if (typeof v == 'number') {
        obj[k] = 0
      } else if (typeof v == 'object') {
        resetObject(v)
      } else {
        obj[k] = null
      }
    }
  }
}

/**
 * 清除对象所有属性
 * @param reflect 如果启用反射,则代理类监听不了
 */
export function cleanObject(obj: object, reflect = false) {
  for (const key of Reflect.ownKeys(obj)) {
    if (reflect) {
      Reflect.deleteProperty(obj, key)
    } else {
      delete obj[key]
    }
  }
}

/**
 * 对一个对象的所有子对象操作，包括自身
 */
export function deepForObj(obj: object, consumer: (o: object) => void, ...ignoreKeys: string[]) {
  if (!obj) return
  consumer(obj)
  for (let k in obj) {
    let v = obj[k]
    if (typeof v == 'object' && !ignoreKeys.includes(k)) {
      deepForObj(v, consumer, ...ignoreKeys)
    }
  }
}

/**
 * 操作对象的指定key
 */
export function deepForKey(obj: object, keys: string[], func: (o: object, k: string) => any) {
  for (let k in obj) {
    let v = obj[k]
    if (keys.includes(k)) {
      func(obj, k)
    } else if (typeof v == 'object') {
      deepForKey(v, keys, func)
    }
  }
}

/**
 * 去除数组中满足条件的项
 */
export function exclude<T>(arr: T[], predicate: (t: T)=>boolean) {
  let length = arr.length
  for (let i = 0; i < length; i++) {
    if (predicate(arr[i])) {
      arr.splice(i, 1)
      length--
      i--
    }
  }
}

if (import.meta.env.DEV) {
  console.log(navigator.userAgent, 'isMobile: ' + isMobile());
}

/**
 * 根据userAgent判断设备是否是手机
 */
export function isMobile() {
  const userAgentInfo = navigator?.userAgent;
  if (!userAgentInfo) return null
  const mobileAgents = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];
   for (let v = 0; v < mobileAgents.length; v++) {
      if (userAgentInfo.indexOf(mobileAgents[v]) > 0) {
        return true
      }
   }
   return false
}

/**
 * 判断当前设备是否为电视
 */
export function isTelevison() {
  return isMobile() && navigator.userAgent.includes('tv_x86')
}

export function setSuffix(fileName: string, suffix = '') {
  let idx = fileName.lastIndexOf('.')
  if (idx != -1) {
    fileName = fileName.substring(0, idx) + suffix
  }
  return fileName
}

/**
 * 根据数组中指定属性重复的项
 */
export function distinct<T>(arr: Array<T>, attr: keyof T) {
  let record = []
  let copy = clone(arr)
  for (let o of copy) {
    let u = o[attr]
    if(record.includes(u)) {
      arr.splice(arr.findLastIndex(v=>v[attr]===u),1)
    } else {
      record.push(u)
    }
  }
}

/**
 * 清除对象里面有无效值的key
 * @param obj 
 */
export function removeUnknown(obj: object) {
  for (let key of Object.keys(obj)) {
    if (isUndefined(obj[key]) || isNull(obj[key])) {
      delete obj[key]
    }
  }
}

/**
 * 高亮关键词(不区分大小写)
 */
export function highlight(str: string, keyword: string, color: string = 'red') {
  let idx = toLower(str).indexOf(toLower(keyword))
  if (idx != -1) {
    let end = idx + keyword.length
    let tag = `<font color="${color}">` + str.substring(idx, end) + '</font>'
    str = str.substring(0, idx) + tag + str.substring(end)
  }
  return str
}

/**
 * 对强调的内容加一组标签,如: hel<em>oo</em>
 */
export function emphasize(str: string, keyword: string, tagName: string = 'em') {
  let idx = toLower(str).indexOf(toLower(keyword))
  if (idx != -1) {
    let end = idx + keyword.length
    let tag = `<${tagName}>` + str.substring(idx, end) + `</${tagName}>`
    str = str.substring(0, idx) + tag + str.substring(end)
  }
  return str
}

/**
 * 深度克隆(支持循环依赖)
 * @param obj 
 */
export function deepClone(obj: any) {
  const objMap = new Map()
  const _deepClone = (val: any) => {
    const type = typeof val
    if (type !== 'object' || type === null) {
      return val
    }
    if (objMap.has(val)) {
      return objMap.get(val)
    }
    const result = Array.isArray(val) ? [] : {}
    objMap.set(val, result)
    for (const key in val) {
      result[key] = _deepClone(val[key])
    }
    return result
  }
  return _deepClone(obj)
}

/**
 * 利用消息通道(支持循环依赖)
 */
export function deepClone2(obj: any) {
  return new Promise((resolve)=>{
    const { port1, port2 } = new MessageChannel()
    port1.postMessage(obj)
    port2.onmessage = (msg) => {
      resolve(msg.data)
    }
  })
}

/**
 * 判断 value 是否为空 null or undefined
 * 如果是字符串判断trim后的长度===0
 * 如果是数组判断大小===0
 * 如果是对象判断keys.length
 */
export function isBlank(value: any) {
  if (value === undefined || value === null) {
    return true
  }
  if (typeof value === 'string') {
    return value.trim().length === 0
  }
  if (Array.isArray(value)) {
    return value.length === 0
  }
  if (typeof value === 'object') {
    return Object.keys(value).length === 0
  }
  return false
}

/**
 * 0-9999 => 0-9999
 * 10000+ => 1万
 * 10999 => 1万
 * 11000+ => 1.1万
 * 1亿以上: 1.2亿
 */
export function displayNumber(num: number) {
  if (isNaN(num) || Math.abs(num) < 10000) {
    return num
  }
  let radix = num / 100_000_000
  if (Math.abs(radix)>=1) {
    return radix.toFixed(1) + '亿'
  }
  radix = num / 10_000
  return radix.toFixed(Math.abs(radix) >= 100 ? 0 : 1) + '万'
}
/**
 * 24小时内 => xx小时前 xx分钟前
 * 今年内 => 11-15
 * 不是今年的内容 => 2022-11-15
 * @deprecated
 */
export function displayDate(time: string) {
  let now = dayjs()
  let date = dayjs(time)
  if (date.year() !== now.year()) {
    return date.format('YYYY-MM-DD')
  }
  let diff = dayjs.duration(now.diff(date));
  if (diff.asHours() < 24) {
    return date.fromNow()
  }
  return date.format('MM-DD')
}

/**
 * 将毫秒转为持续时间: 02:33  03:22:45
 * @param second 是否显示到秒
 */
export function displayDuration(time: number, second = true) : string {
  if (!time) time = 0
  const result = []
  function pad(num: number) {
    return num.toString().padStart(2, '0')
  }
  time = time / 1000
  let h = time / 60 / 60 % 100 | 0
  let m = time / 60 % 60 | 0
  let s = time % 60 | 0
  if (h > 0) {
    result.push(pad(h))
  }
  result.push(pad(m))
  if (second) {
    result.push(pad(s))
  }
  return result.join(':')
}

/**
 * 解决浮点数比较问题
 * var a = 0.1 + 0.2;
 * var b = 0.3
 * numberEquals( a, b );  // true 
 * numberEquals( 0.000001, 0.000002 );  // false
 */
export function numberEquals(n1: number, n2: number) {
  //Number.EPSILON极小值
  const epsilon = Number.EPSILON ?? Math.pow( 2, -52 )
  return Math.abs( n1 - n2 ) < epsilon;
}

/**
 * func('abcdefg', 3) => ['abc', 'def, 'g']
 */
export function split(str: string, length: number) {
  const result = []
  let s = ''
  for (const c of str.trim()) {
    if (c) {
      s += c
      if (s.length >= length) {
        result.push(s)
        s = ''
      }
    }
  }
  if (s) {
    result.push(s)
  }
  return result
}

export function findIndex<T>(arr: Array<T>, target: any | ((t: T)=>boolean)) {
  for (let i = 0; i < arr.length; i++) {
    if (typeof target === 'function') {
      if (target[arr[i]]) {
        return i
      }
    } else if (target === arr[i]) {
      return i
    }
  }
  return -1
}

/**
 * 将超过长度的字符串末尾转为省略号
 * abcdefghijklmn => abcdefghijk...
 */
export function ellipsis(value: string, maxlength: number) {
  if (value.length > maxlength) {
    return value.substring(0, maxlength) + '...'
  }
  return value
}

/**
 * 验证邮箱格式
 */
const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
export function validateEmail(email: string) {
  return emailRegex.test(email);
}

/**
 * 比较出两个对象中不同的key,使用 lodash.isEqual 进行深度比较
 * { a: 1, b: 2 } { a: 1, b: 3 } => ['b']
 */
export function diff<T1 extends object, T2 extends object>(obj1: T1, obj2: T2) {
  obj1 ??= {} as T1
  obj2 ??= {} as T2
  const result = new Set<keyof T1 | keyof T2>()
  for(const key of [...Object.keys(obj1), ...Object.keys(obj2)]) {
    if (!isEqual(obj1[key], obj2[key])) {
      result.add(key as any)
    }
  }
  return [...result]
}

/**
 * 深度查找符合条件的子项
 */
export function findDeep<T extends {}>(arr: T[], children: keyof T, predicate: (t: T)=>boolean) {
  const find = (_arr: T[])=>{
    for (const value of _arr) {
      if (predicate(value)) {
        return value
      }
      if (Array.isArray(value[children])) {
        const result = find(value[children])
        if (result) {
          return result
        }
      }
    }
  }
  return find(arr) as T
}