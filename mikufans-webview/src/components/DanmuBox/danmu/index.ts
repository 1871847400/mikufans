import { cloneDeep, random, repeat } from 'lodash';
import { measureText } from '@/utils/text/measure';
import { parseTransform } from '@/utils/html/transform';
import { convertToHtml } from '@/utils/emoji';

class Track {
  danmuBox: DanmuBox
  top: number //距离顶部的距离 px
  playing: TrackDanmu[] = [] //正在播放的弹幕
  constructor(top: number, danmuBox: DanmuBox) {
    this.top = top
    this.danmuBox = danmuBox
  }
  //防遮挡
  canplay(danmu: Danmu) {
    const { text, type } = danmu
    const cw = this.danmuBox.container.clientWidth
    const lastDanmu = this.playing.findLast(a=>a.type==type)
    if (!lastDanmu) {
      return true
    }
    if (lastDanmu.type!='ROLL') {
      return false
    }
    const { font, fontSize, duration } = this.danmuBox.options
    const w1 = measureText(font, fontSize, lastDanmu.text).width
    const w2 = measureText(font, fontSize, text).width
    const tx = Math.abs(parseTransform(lastDanmu.el).tx)
    //上一条弹幕还没完全显示出来
    if (tx < w1) {
      return false
    }
    //如果旧弹幕宽度>=新弹幕,则代表上条弹幕速度比新弹幕快
    if (w1 >= w2) {
      return true
    }
    //每秒移动多少像素
    const speed1 = (cw + w1) / (duration / 1000)
    const speed2 = (cw + w2) / (duration / 1000)
    const cost = (tx - w1) / (speed2 - speed1) //追上弹幕还有多久
    const wait = (cw + w1 - tx) / speed1 //上一条弹幕还有多久完成
    return cost > wait
  }
  appendDanmu(danmu: Danmu) {
    const container = this.danmuBox.container
    const el = document.createElement('div')
    el.innerHTML = convertToHtml(danmu.text)
    el.className = 'danmu-item'
    container.append(el)
    const trackDanmu : TrackDanmu = { ...danmu, el }
    this.playing.push(trackDanmu)
    const { font, lineHeight, duration } = this.danmuBox.options
    //移动距离等于=容器宽度+弹幕宽度
    const offset = container.clientWidth + el.clientWidth
    if (danmu.type === 'ROLL') {
      el.style.setProperty('--offset', -offset + 'px')
    } else {
      el.style.setProperty('--offset', -offset/2 + 'px')
    }
    //判断为彩色弹幕
    if (danmu.color.toLowerCase() != '#fff' && danmu.color.toLowerCase()!='#ffffff') {
      el.dataset.color = 'true'
    }
    el.dataset.self = danmu.self+''
    el.dataset.type = danmu.type.toLowerCase()
    el.style.setProperty('--top', this.top + 'px')
    el.style.setProperty('--color', danmu.color)
    el.style.setProperty('--font', font)
    el.style.setProperty('--fontSize', this.danmuBox.fontSize+'px')
    el.style.setProperty('--lineHeight', lineHeight+'')
    el.style.setProperty('--duration', duration+'ms')
    const onEnded = ()=>{
      el.remove()
      this.playing = this.playing.filter(a=>a.id!==danmu.id)
    }
    el.onanimationend = onEnded
    el.onanimationcancel = onEnded
    if (this.danmuBox.options.syncVideo) {
      el.getAnimations()[0].playbackRate = this.danmuBox.video.playbackRate
    }
    el.onclick =e=>e.stopPropagation()
    // const anim = el.animate([{
    //   transform: `translateX(-${offset}px)`
    // }], {
    //   duration: this.danmuBox.options.duration,
    //   easing: 'linear',
    //   fill: 'forwards',
    // })
    // anim.onfinish = ()=>{
    //   trackDanmu.status = 'finish'
    //   //动画播放完成后删除弹幕
    //   el.remove()
    //   this.playing = this.playing.filter(a=>a.id!==danmu.id)
    // }
    return el
  }
  removeById(id: string) {
    const index = this.playing.findIndex(a=>a.id===id)
    if (index !== -1) {
      this.playing[index].el?.remove()
      this.playing.splice(index, 1)
    }
  }
  clear() {
    this.playing.forEach(e=>e.el.remove())
    this.playing = []
  }
}

export class DanmuBox {
  public container: HTMLDivElement
  public video: HTMLVideoElement
  public options: DanmuBoxOptions
  private tracks : Track[] = []
  constructor(container: HTMLDivElement, video: HTMLVideoElement, options: DanmuBoxOptions) {
    this.container = container
    this.video = video
    this.options = options
    this.updateTracks()
  }
  updateTracks() {
    const minTop = this.options.paddingY
    const maxTop = (this.container.clientHeight - this.trackHeight - this.options.paddingY) * this.options.showArea
    let i = 0;
    for (let top = minTop; top < maxTop; top += this.trackHeight) {
      if (this.tracks[i]?.top != top) {
        this.tracks[i] = new Track(top, this)
      }
      i++
    }
    this.tracks.splice(i)
    // console.log(minTop, this.tracks.map(a=>a.top));
  }
  updateSpeed() {
    const rate = this.options.syncVideo ? this.video.playbackRate : 1
    for (const danmu of this.currentDanmus) {
      //如果还在dom树内
      if (danmu.el.isConnected) {
        const anim = danmu.el.getAnimations()[0]
        if (anim?.playState !== 'finished') {
          anim.playbackRate = rate
        }
      }
    }
  }
  //当前正在播放的所有弹幕
  get currentDanmus() {
    const danmus : TrackDanmu[] = []
    for(const track of this.tracks) {
      for(const danmu of track.playing) {
        danmus.push(danmu)
      }
    }
    return danmus
  }
  removeById(id: string) {
    for(const track of this.tracks) {
      track.removeById(id)
    }
  }
  clearAll() {
    for(const track of this.tracks) {
      track.clear()
    }
  }
  get trackHeight() {
    //文字高度=fontSize*lineHeight
    return this.fontSize * this.options.lineHeight
  }
  get fontSize() {
    let fs = this.options.fontSize
    //全屏下自动调大字体
    if (document.fullscreenElement) {
      fs *= 1.5
    }
    return fs
  }
  render(danmu: Danmu) {
    //防重复
    if (this.currentDanmus.some(a=>a.id===danmu.id)) {
      return
    }
    //让配置可以动态生效
    this.updateTracks()
    let tracks = this.tracks
    if (danmu.type == 'FIXED_BOTTOM') {
      tracks = tracks.toReversed()
    }
    //寻找合适的轨道插入弹幕
    for(const track of tracks) {
      if (track.canplay(danmu)) {
        return track.appendDanmu(danmu)
      }
    }
    //如果没有一条轨道能放进去,但允许重叠则随机放一条轨道
    if (this.options.density==='overlap') {
      const index = random(0, this.tracks.length - 1, false)
      return this.tracks[index]?.appendDanmu(danmu)
    }
  }
}