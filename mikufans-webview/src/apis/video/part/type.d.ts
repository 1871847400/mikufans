type VideoPartType = 'TV'

interface VideoPart extends BaseEntity {
  videoId: string
  videoSid: string
  resId: string
  partType: VideoPartType
  partName: string
  sort: number
  bannerId: string
  disabled: number
  danmus: number
  canplay: number
  publishTime: string
  videoSid: string 
  uri?: string
  resource?: VideoResource
  progress?: number
  processFail?: boolean
  subtitles?: VideoSubtitle[]
  flags: Record<string, string>
  sysAudit?: SysAudit
  masterUrl: string
  thumbnailsVttUrl: string
  thumbnailsImgUrl: string
}

interface VideoPartDto {
  id?: string //更新需要
  partName: string
  videoId: string
  taskId: string
  bannerId?: string
  sort?: number //更新时才有用
  subtitles?: SubtitleFile[]
  preprocess?: {
    segments?: {
      offset: number
      length: number
    }[]
    watermark?: Watermark
    subtitle?: string //内嵌字幕
  }
}

interface Watermark {
  data: string //base64
  x: number //根据分辨率
  y: number
}

interface SubtitleFile {
  data: string //原始文本
  type: SubtitleType
  regionId: string //语言地区
  filename: string //文件原始名称
}