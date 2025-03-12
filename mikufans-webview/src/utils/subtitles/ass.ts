import { toInteger, uniqueId } from "lodash"

export interface SectionHandler {
  readLine(line: string, data: AssSubtitles) : void
}
export interface AssText {
  id: string
  layer?: number 
  start?: string 
  end?: string 
  name?: string
  marginLeft?: number
  marginRight?: number
  marginVertical?: number
  effect?: string
  text: string
}
export interface AssSubtitles {
  playResX?: number,
  playResY?: number
  events: {
    formats: string[]
    comments: AssText[]
    dialogues: AssText[]
    texts: AssText[] //全部
  }
}
const Section = {
  '[Events]': {
    readLine(line: string, data: AssSubtitles) {
      let prefix = line.substring(0, line.indexOf(':'))
      let content = line.substring(line.indexOf(':') + 1).trim()
      let contents = content.split(',').map(s=>s.trim())
      if (prefix == 'Format') {
        data.events.formats = contents
        return
      }
      let text = {
        id: uniqueId('subtitle-ass'),
        text: contents.slice(data.events.formats.length - 1).join(),
      } as AssText
      
      for (let i = 0; i < data.events.formats.length - 1; i++) {
        let str = contents[i]
        if (!str) continue
        let format = data.events.formats[i].toLowerCase()
        if (format == 'layer') {
          text.layer = toInteger(str)
        }
        if (format == 'start') {
          text.start = str
        }
        if (format == 'end') {
          text.end = str
        }
      }
      if (prefix == 'Comment') {
        data.events.comments.push(text)
      } else if (prefix == 'Dialogue') {
        data.events.dialogues.push(text)
      }
      data.events.texts.push(text)
    }
  },
  '[Script Info]': {
    readLine(line, data) {
      let param = line.split(/: /)[0]
      let content = line.split(/: /)[1]
      if (param == 'PlayResX') {
        data.playResX = toInteger(content)
      } else if (param == 'PlayResY') {
        data.playResY = toInteger(content)
      }
    },
  }
} as Record<string, SectionHandler>

export function parse(str: string) {
  let data : AssSubtitles = {
    events: {
      formats: [],
      comments: [],
      dialogues: [],
      texts: [],
    }
  }
  let handler : SectionHandler | undefined
  for (let line of str.split('\n')) {
    line = line.trim()
    // 匹配 [这里至少一个字符] []前后不允许有其他字符
    if (/^\[.+\]$/.test(line)) {
      handler = Section[line]
      continue
    }
    if (handler) {
      handler.readLine(line, data)
    }
  }
  return data
}