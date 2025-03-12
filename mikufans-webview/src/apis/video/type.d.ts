type VideoType = 'VIDEO'|'ANIME'|'MOVIE'

interface Video extends BaseEntity {
  sid: string
  uid: number
  type: VideoType//视频类型
  channelId: string //分区
  title: string //标题
  intro: string //简介
  tags: string[] //标签列表
  republish: number //是否转载
  sourceUrl: string //转载地址
  search: number //是否可被搜索
  userLevel: number //最低观看等级
  bannerId: string //PC端封面 16:9
  mBannerId: string //手机端封面 4:3
  duration: number //所有分集的总时长(ms)
  bangumiId: string
  score: number
  plays: number
  coins: number
  stars: number
  danmus: number
  shares: number
  disabled: number
  reason: string
  apply: number
  uri: string
  typeTag: string
  user: User
  parts: VideoPart[]
  canplayCount: number //能够播放的分集数量
  seriesList?: Video[] //关联的所有视频(第一季关联第二季)
  history?: VideoWatchHistory //如果包含代表看过
  flags: Record<string, string> //特殊标志
  userCoin: number //当前用户投币数量
  userStar: boolean //当前用户是否收藏
  bangumi?: Bangumi
  highlightTitle?: string
  statusCountMap?: Record<SysAudit['auditStatus'], number>
  dynamic: UserDynamic
  likeStatus: UserDynamic['likeStatus'] //这里直接引用的动态的点赞
}

//创建时的DTO，更新为Partial<VideoDto> + id
interface VideoDto {
  type: VideoType
  channelId: string
  title: string
  intro?: string
  bannerId: string
  mBannerId?: string
  republish: number
  sourceUrl?: string
  tags: string[]
  userLevel?: number
  createParts?: VideoPartDto[]
  updateParts?: VideoPartDto[]
  bangumi?: BangumiDto
  dynamic?: UserDynamicDto
}

interface UploadVideo {
  video: Video
  part: VideoPart
}

interface VideoCoin extends BaseEntity {
  videoId: string
  coinCount: number
}

interface VideoSearchParams extends SearchParams {
  keyword?: string
  type?: VideoType //限制分类
  sort?: VideoSearchSort //排序字段
  userId?: string //限定作者
  searchFollow?: boolean //限制为关注用户发布的视频
  tag?: string
  channelId?: string //限制分区
  [string: key]: string
}

interface UserVideoStats {
  uploadTotal: number
  uploadCountMap: Record<string, number>
}

type VideoSearchSort =
  'SCORE' |
  'TIME' |
  'PLAY' |
  'COIN' |
  'STAR' |
  'DANMU'

interface HightlightText { 
  rawText: string
  highlightText: string 
}

type SearchAllResult = Page<{
  type: VideoType,
  page: Page<Video>
}>

interface UploadVideoSearchParams extends SearchParams {
  status?: 'ANY'|'DOING'|'SUCCESS'|'FAIL'
  videoType?: VideoType
  sort?: VideoSearchSort
  asc?: boolean
}