interface UserLikeData extends BaseEntity {
  likeType: BusinessType
  busiId: string
  likes: number
  dislikes: number
  likeUserIds: string[]
  readFlag: number
  hidden: number
  likeTime: string
  likeTimeStr: string
  likeVal: number //当前用户点赞值
  likeLabel: string
  likeUsers: BaseEntity['user'][]
  source: MessageModel
}

interface UserLike extends BaseEntity {
  likeDataId: string
  likeVal: number
  likeTime: string
}

interface LikeStatus {
  id: string
  likeVal: number
  likes: number
  dislikes: number
}