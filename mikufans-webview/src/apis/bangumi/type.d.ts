interface BangumiSubscribe extends BaseEntity {
  bangumiId: string
  subscribeDate: string //订阅日期
}

interface Bangumi extends BaseEntity {
  posterId: string
  releaseStatus: number //播放状态
  releaseWeek: number //播放日期 0-7 0未知 1星期一 7星期天
  releaseTime?: string //播放时间
  releaseSeason: number //播放季度
  styles?: { id: string, styleName: string }[] //风格列表
  styleNames: string // 风格1 / 风格2 ...
  premiere?: string //首播日期
  official: string //官方情报
  staff: string //工作人员
  voice: string //声优
  subscribe: number //订阅人数
  rate: number //综合评分
  rateCount: number //评分人数
  seriesTag: string //系列标签
  seriesIds: string[] //关联的id列表
  regionId: string
  series: BangumiSeries[]
  subscribed?: BangumiSubscribe  //用户订阅数据
  userRate?: UserRate //用户点评数据
  video?: Video
  desc?: string
  watchInfo?: string
  watchProgress?: number //0-1
  uri: string
}

interface BangumiDto {
  id?: string
  posterId?: string //海报id
  releaseWeek?: number //每周更新日期
  releaseTime: string //具体更新时间
  releaseStatus: number //更新状态 0未知1更新中2完结
  releaseSeason?: number//季度
  premiere?: string | number //首播日期
  official?: string
  staff?: string //工作人员表
  voice?: string //声优列表
  styles: string[] //风格id列表
  seriesTag?: string
  seriesIds?: string[]
  regionId?: string
}

interface BangumiParams extends SearchParams {
  title?: string
  videoType?: VideoType
  status?: number //更新状态
  year?: number //限制发布年份
  season?: number //发行季度
  week?: number //1-7
  subscribedUserId?: string //订阅人id,查看其它人的
  sort?: BangumiSort
  asc?: boolean
  style?: string //风格id
  region?: string //地区id
}

type BangumiSort = 'subscribe'|'rate'|'premiere'|'subscribe_time'|'random'|'play'

interface BangumiSeries {
  bid: string
  vid: string
  sid: string
  seriesTag: string
}