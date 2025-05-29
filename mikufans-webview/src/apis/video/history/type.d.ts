interface VideoWatchHistory extends BaseEntity {
  videoId: string
  partId: string
  device: number
  watchPos: number //上次观看位置ms
  watchTime: number //观看总时长ms
  lastWatchTime: string //上次观看日期时间
  lastWatchTimeStr: string //上次观看日期时间
  part: VideoPart
  video: Video
  highlighted?: string
  uri?: string
  playPos: string
}

interface VideoHistoryParams extends SearchParams {
  title?: string
  type?: VideoType
  begin?: Date | string
  end?: Date | string
}