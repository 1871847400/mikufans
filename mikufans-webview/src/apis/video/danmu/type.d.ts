interface VideoDanmu extends BaseEntity {
  vid: string
  partId: string
  content: string //长度1-32
  fontColor: string //颜色代码
  fontType: 1 //字体大小
  sendTime: number //发送时间 秒
  danmuType: DanmuType //弹幕类型: 滚动,固定
  createDate: string
  likeStatus: LikeStatus
}

interface VideoDanmuDto {
  partId: string
  content: string
  fontColor: string
  fontType: 1
  sendTime: number
  danmuType: DanmuType
}

interface VideoDanmuColor extends BaseEntity {
  colorName: string
  colorCode: string
}