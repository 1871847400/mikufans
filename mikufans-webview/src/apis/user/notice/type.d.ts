interface UserNotice extends BaseEntity {
  noticeType: string
  noticeId: string
  readFlag: number
  hidden: number
  title: string
  content: string
  uri: string
  publishTime: string
}