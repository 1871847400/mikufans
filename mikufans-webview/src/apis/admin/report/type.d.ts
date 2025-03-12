interface UserReportAudit {
  targetId: string
  reportType: BusinessType
  audit: AuditStatus
  message: string
}

interface UserReportVo {
  targetId: string
  reportType: BusinessType
  target: MessageModel
  reportCount: number
}