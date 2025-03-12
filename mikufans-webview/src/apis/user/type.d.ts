interface User extends BaseEntity {
  username: string
  nickname: string
  gender: number
  birthday?: string
  sign: string
  avatarId: string
  coin: number
  disabled: number
  registerTime: string
  background: string
  level: number
  exp: number
  likes: number
  dislikes: number
  coins: number
  videos: number
  follows: number
  fans: number
  subscribes: number
  publishes: number
  dynamics: number
  articles: number
  uri: string
  follow?: UserFollowStatus
  lastDynamic?: UserDynamic
  nextExp?: number
  highlightNickname?: string
  flags?: Record<USER_FLAGS, string>
}

interface UserDto {
  nickname?: string,
  password?: string,
  gender?: User['gender'],
  birthday?: string,
  sign?: string,
  avatarId?: string,
  background?: string
}

interface UserSearchParams extends SearchParams {
  nickname?: string
  lastDynamic?: boolean
  sort?: 'FANS_MORE'|'FANS_LESS'|'LEVEL_HIGH'|'LEVEL_LOW'
}

interface MessageModel {
  businessType: BusinessType
  message: string
  uri: string
  business: string
}