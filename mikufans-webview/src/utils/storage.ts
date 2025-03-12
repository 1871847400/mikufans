/**
 * 浏览器LocalStorage
 * 注意：1.只能存string 2.手机端不支持
 */
export default {
  getItem<T>(key: string, defaultValue?: T) : T {
    if (localStorage) {
      const result = localStorage.getItem(key)
      if (result) {
        try {
          return JSON.parse(result)
        } catch(err) {
          //如果解析的不是json格式则会异常,返回原本的内容
          return result as T
        }
      }
    }
    return defaultValue
  },
  setItem(key: string, value: any) {
    if (value === null) {
      localStorage?.removeItem(key)
    } else {
      localStorage?.setItem(key, JSON.stringify(value))
    }
  },
  removeItem(key: string) {
    localStorage?.removeItem(key)
  },
  clear() {
    localStorage?.clear()
  }
}