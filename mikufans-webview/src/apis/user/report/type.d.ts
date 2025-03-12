interface UserReport extends BaseEntity {
  targetId: string
  reportType: BusinessType
  behaviorId: string
  reason: string
  audit: AuditStatus
  behaviorName?: string
}

interface UserReportDto {
  targetId: string
  reportType: BusinessType
  behaviorId: string
  reason: string
}

interface ReportBehavior extends BaseEntity {
  category: string
  behavior: string
  intro: string
}