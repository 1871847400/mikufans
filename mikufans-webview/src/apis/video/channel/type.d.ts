interface VideoChannel extends BaseEntity {
  pid: string
  channelName: string
  channelDesc: string
  iconName: string
  url: string
  sort: number
  disabled?: number
  children?: VideoChannel[]
}