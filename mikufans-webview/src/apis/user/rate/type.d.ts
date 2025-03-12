interface UserRate extends BaseEntity {
  targetId: string
  rate: number
  content: string
  publishDate: string
  uri: string
  dynamic: UserDynamic
  bangumi?: Bangumi
  likeStatus: UserDynamic['likeStatus'] //引用的动态
}

interface UserRateDto {
  targetId: string
  rate: number
  content?: string
}