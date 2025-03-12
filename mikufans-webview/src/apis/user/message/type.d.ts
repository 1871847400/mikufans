interface UserDialog extends BaseEntity {
  targetId: string
  top: number
  target: BaseEntity['user']
  lastId: string
  lastWhisper?: UserWhisper
  unread: number
}

interface UserWhisper extends BaseEntity {
  message: string
  msgType: number
  readFlag: number
  revoked: number
  receiverId: string
  sendTime: string
}

interface UserWhisperDto {
  message: string,
  msgType: number,
  receiverId: string,
}

interface UserWhisperParams extends SearchParams {
  targetId: string,
  read?: number
}

interface MsgUnread {
  whisper: number
  reply: number
  atuser: number
  likes: number
  systems: number
  total: number
}