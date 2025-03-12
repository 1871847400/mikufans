/**
 * 具有大小限制的缓存
 * 新添加和经常使用的数据在末尾
 * 当超过给定大小时删除前面的数据
 */
export class LRUCache<K = any, V = any> {
  #map: Map<K, V>
  maxSize: number
  constructor(maxSize = 100) {
    this.#map = new Map
    this.maxSize = maxSize
  }
  has(key: K) {
    return this.#map.has(key)
  }
  get(key: K) {
    const value = this.#map.get(key)
    if (typeof value !== 'undefined') {
      this.#map.delete(key)
      this.#map.set(key, value)
    }
    return value
  }
  set(key: K, value: V) {
    this.#map.delete(key)
    this.#map.set(key, value)
    if (this.#map.size > this.maxSize) { 
      this.#map.delete(this.#map.keys().next().value)
    }
  }
  del(key: K) {
    this.#map.delete(key)
  }
  clear() {
    this.#map.clear()
  }
  show() {
    this.#map.forEach((value, key)=>{
      console.log(key + ': ', value);
    })
  }
}