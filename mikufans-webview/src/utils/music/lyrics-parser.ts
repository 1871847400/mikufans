import Buffer from 'buffer'

export interface LyricsWord {
  word: string, 
  pronun?: string, 
  start: number, 
  time: number
}

export interface LyricsLine {
  //原始文本
  raw: string
  //翻译文本
  translation?: string
  //起始时间(ms),如: 0
  start: number
  //持续时间(ms),如: 1017
  time: number
  //记录每一个字 pronun=发音,比如日语假名
  words: LyricsWord[]
}

export function parseKrc(str: string) : LyricsLine[] {
  let list = str.split(/\n/)
  // console.log(list[0]);
  // console.log(list[1]);
  var translation : any
  let language = list.find(s=>s.startsWith('[language'))
  if (language) {
    let reg = language.match(/^\[language:(\S*)\]/)
    let b64 = reg?.[1]
    if (b64) {
      //base64解码
      let plain = Buffer.Buffer.from(b64, 'base64').toString()
      //将所有unicode替换为中文
      plain = plain.replace(/\\u(\w{4})/gi, (match, group1)=>
        String.fromCharCode(parseInt(group1, 16)))
      translation = JSON.parse(plain)
    }
  }
  const lyrics = <LyricsLine[]>[]
  //正在解析的歌词行数
  let row = 0
  for (let s of list) {
    //如果这行是歌词 [1017,6466]<0,708,0>強<708,1060,0>く<1768,457,0>...
    let reg = s.match(/^\[(\d+),(\d+)\]/)
    if(reg) {
      let raw = ''
      let transContent : Array<any> = translation?.content
      let words = <LyricsWord[]>[]
      let reg2 = s.matchAll(/<(\d+),(\d+),\d+>([^<]+)/gi)
      //正在解析的那句歌词的列数
      let col = 0
      for (let reg3 of reg2) {
        // console.log(reg3[1], reg3[2], reg3[3]);
        let wordStr = reg3[3].replace('\r', '')
        let word = {
          word: wordStr,
          start: parseInt(reg3[1]),
          time: parseInt(reg3[2]), 
        } as LyricsWord
        if (transContent && transContent.length >= 2) {
          word.pronun = transContent[1].lyricContent[row][col]
        }
        words.push(word)
        raw += wordStr
        col++
      }
      let line = {
        raw,
        start: parseInt(reg[1]),
        time: parseInt(reg[2]),
        words,
      } as LyricsLine
      if (transContent && transContent.length >= 1) {
        line.translation = transContent[0].lyricContent[row][0]
      }
      lyrics.push(line)
      row++
    }
  }
  return lyrics
}

/**
  [00:00.00] 作词 : XXX
  [00:01.00] 作曲 : XXX
  ...
  [00:19.44]爱到尽头 覆水难收

  text和translation格式应向上面这样保持一致
  最后一行歌词的时间需要总时长才能知道
*/
export function parseLrc(text: string, translation?: string, duration?: number) : LyricsLine[] {
  const lyrics = <LyricsLine[]>[]
  let lines = text.split('\n')
  let lines2 = translation?.split('\n')
  // console.log(lines, lines2);
  for (let l of lines) {
    let reg = l.match(/\[(\d+):(\d+.\d+)\](.+)/)
    if (!reg) continue
    let line = {
      raw: reg[3] || '',
      start: (parseInt(reg[1])*60+parseFloat(reg[2])) * 1000,
      time: 0,
      words: [],
    } as LyricsLine
    // console.log(l, line);
    for (let s of line.raw) {
      line.words.push({
        word: s,
        start: 0,
        time: 0,
      })
    }
    if (lyrics.length) {
      let last = lyrics[lyrics.length-1]
      last.time = line.start - last.start
    }
    if (lines2) {
      line.translation = lines2[lyrics.length]
    }
    lyrics.push(line)
  }
  if (lyrics.length) {
    let last = lyrics[lyrics.length-1]
    last.time = (duration || 999999999999) - last.start
  }
  return lyrics
}