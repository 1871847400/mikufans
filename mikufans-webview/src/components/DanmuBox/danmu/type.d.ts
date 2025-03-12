type DanmuType = 'ROLL'|'FIXED_TOP'|'FIXED_BOTTOM'

interface Danmu {
  id: string
  text: string
  color: string
  time: number
  type: DanmuType
  self: boolean //是否自己发送的弹幕,加描边
}

interface TrackDanmu extends Danmu {
  el: HTMLDivElement
}

interface DanmuBoxOptions {
  enable: boolean //是否显示弹幕
  showArea: number //弹幕显示区域 0-1
  fontSize: number //文字大小px
  lineHeight: number //行高
  opacity: number //字幕不透明度 0-1
  density: 'pretty'|'overlap' //弹幕密度: 防重叠|重叠
  syncVideo?: boolean //同步视频速度
  duration: number //弹幕速度(ms)
  font: string //字体
  paddingY: number //弹幕可视区域 上下收缩
  ban: { //屏蔽类型
    roll: boolean //滚动弹幕
    fixed: boolean //固定弹幕
    color: boolean //彩色弹幕
  } 
}