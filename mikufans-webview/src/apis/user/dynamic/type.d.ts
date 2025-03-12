interface UserDynamicParams extends SearchParams {
  keyword?: string
  userId?: string //搜索指定用户的动态
  shareId?: string //限制转发id
  dynamicType?: BusinessType //限制动态类型
  excludeSelf?: boolean //排除自己的动态
}

interface UserDynamic extends BaseEntity {
  shareId: string
  shareReason: string
  shares: number
  targetId: string
  dynamicType: BusinessType
  top: number
  visible: number
  publishFlag: number
  publishTime: string
  commentArea: UserCommentArea
  likeStatus: LikeStatus
  publishTimeStr: string
  source?: BaseEntity
}

interface UserDynamicShareDto {
  shareId: string
  shareReason?: string
}

interface UserDynamicDto {
  id?: string
  visible: number
  publishFlag: number
  publishTime: string
  commentArea: CommentAreaDto
}