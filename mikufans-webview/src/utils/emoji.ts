import emojiMap from '@/assets/emojis.json'
import xss from 'xss'

/**
 * 转换包含表情的文本为HTML
 */
export function convertToHtml(str: string) {
  str = xss(str)
  return str.replaceAll(/\[[^\]|[]+\]/g, (match)=>{
    // match = [微笑]
    return convertEmoji(match)
  })
}
/**
 * 转换例如 [微笑] => <img ...>
 * 如果不是表情返回原来的内容
 */
const emojis = emojiMap.flatMap(e=>e.emojis)
export function convertEmoji(val: string, id='0') : string {
  const i = emojis.find(e=>e.name===val)
  if (i) {
    const group = emojiMap.find(a=>a.emojis.some(b=>b.id===i.id))
    return `<img src="${i.url}" alt="${i.name}" draggable="false" class="emoji" data-id="${id}" data-size="${group.size}">`
  } else {
    return val
  }
}

export function isEmojiItem(obj: any) : obj is EmojiItem {
  return typeof obj === 'object' && emojis.some(a=>a.id === obj['id'])
}

export type EmojiItem = typeof emojis[number]
export {
  emojis,
  emojiMap,
}