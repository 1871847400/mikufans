interface SysCarousel extends BaseEntity {
  position: 'HOME'|'ANIME'|'MOVIE'|'CHANNEL'
  channelId: string
  bannerId: string
  mainColor: string
  thumbnailId: string
  title: string
  subtitle: string
  url: string
  disabled: number
  videoId: string
  startDate: string
  endDate?: string
  sort: number
  video?: Video
  bangumi?: Bangumi
}

interface SysRegion extends BaseEntity {
  regionName: string
  regionCode: string
  langName: string
  langCode: string
  langEnName: string
}

type AuditStatus = 'UNKNOWN'|'SUCCESS'|'FAIL'
type AuditType = 'VIDEO_PART'

interface SysAudit extends BaseEntity {
  targetId: string
  auditType: AuditType
  auditStatus: AuditStatus
  auditReason: string
}

interface CmdLog extends BaseEntity {
  cmd: string
  output: string
  errorMsg: string
  exitCode: number
}

interface UserOperLog extends BaseEntity {
  title: string
  username: string
  ipaddr: string
  clientType: number //0 1 2
  userAgent: string
  operStatus: number
  operType: number
  reqUri: string
  reqParams: string
  reqMethod: string
  methodPath: string
  costTime: number
  userType: number
}

interface SysExtDict extends BaseEntity {
  term: string
  illegal: number
}