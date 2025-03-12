interface SysAuditDto {
  id: string
  auditStatus: AuditStatus
  auditReason: string
}

interface VideoAudit extends Video {
  parts: VideoPart[]
  auditCount: number
}