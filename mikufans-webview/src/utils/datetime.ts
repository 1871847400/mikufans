import dayjs from "dayjs"
import { isNumber } from "lodash"

/**
 * 默认毫秒 => 时分秒 h不超过99 h=0不显示
 * 03:56:09
 * |0 或0能去掉小数
 * secAlwaysShow=是否始终显示秒数，即使为0
 */
export function toHMS(time: number, unit: 'ms' | 's' = 'ms', secAlwaysShow: boolean = false) : string {
  let result = []
  if (unit == 'ms')
    time = time / 1000
  let h = time / 60 / 60 % 100 | 0
  let m = time / 60 % 60 | 0
  let s = time % 60 | 0
  if (h > 0) {
    result.push(pad(h))
  }
  result.push(pad(m))
  if (h > 0 || s > 0 || secAlwaysShow) {
    result.push(pad(s))
  }
  return result.join(':')
}

function pad(num: number) {
  return num.toString().padStart(2, '0')
}


/** 
 * 2016-09-03 13:01:56
*/
export function toDateTimeFormat(date: any) : string {
  if (date instanceof Date) {
    let y = date.getFullYear()
    let M = pad(date.getMonth())
    let d = pad(date.getDate())
    let H = pad(date.getHours())
    let m = pad(date.getMinutes())
    let s = pad(date.getSeconds())
    return `${y}-${M}-${d} ${H}:${m}:${s}`
  }
  return '0000-00-00 00:00:00'
}

export const DayOfWeek = [
  '星期一',
  '星期二',
  '星期三',
  '星期四' ,
  '星期五',
  '星期六',
  '星期天',
]

/**
 * 将存有星期的数组转为用一个十进制的数字表示
 * 例如数组 [2, 5] 表示星期三和星期六
 */
export function convertDaysToNum(days: number[]) {
  const data = [1, 0, 0, 0, 0, 0, 0, 0]
  for (const val of days) {
    data[val + 1] = 1
  }
  return parseInt(data.join(''), 2)
}
export function convertNumToDays(num: number) {
  const days = []
  if (isNumber(num) && num !== 0) {
    let i = 0
    for (const j of num.toString(2)) {
      if (i++===0) continue
      if (j==='1') {
        days.push(i-2)
      }
    } 
  }
  return days
}

export function sleep(ms: number) {
  return new Promise<void>((resolve)=>{
    setTimeout(() => {
      resolve()
    }, ms);
  })
}
/**
 * 时间戳 => 今天,昨天,一周内
 */
const schedule : [number, string][] = [
  [0,'今天'],
  [1,'昨天'],
  [7,'一周内'],
  [30,'一个月内'],
  [90,'三个月内'],
  [180,'半年内'],
  [365,'一年内']
]
export function displayDateRange(datetime: string) {
  const time = dayjs(datetime).format('YYYY-MM-DD')
  const now = dayjs(dayjs().format('YYYY-MM-DD'))
  const day = Math.abs(now.diff(time, 'day'))
  for (const entry of schedule) {
    if (day <= entry[0]) {
      return entry[1]
    }
  }
  return '一年前'
}