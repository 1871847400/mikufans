type VideoQuality = 'FHD'|'HD'|'SD'

type SubtitleType = 'VTT'|'SRT'|'ASS'

type TransferMode = 'NONE'|'CLOUD'|'OSS'

interface VideoResource extends BaseEntity {
  id: string
  md5: string
  mediaType: string
  fileSize: number
  duration: number
  localPath?: string
  transferPath?: string
  transferMode: TransferMode
  pending: number
  qualityLevel: number //最高画质等级
  durationFormat: string
}

interface VideoSubtitle extends BaseEntity {
  rid: string
  vid: string
  type: SubtitleType
  regionId: string
  filename: string
  saveName: string
  region?: SysRegion
}