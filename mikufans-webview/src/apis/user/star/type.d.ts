interface UserStar extends BaseEntity {
  starName: string
  coverId: string
  intro: string
  visible: number
  sort: number
  starCount: number
  defFlag: number //0其它1默认
  starDesc: string //私密,公开
  likeDataId: string
  likeStatus: LikeStatus
  videoStar?: VideoStar
}

interface UserStarDto {
  id?: string
  starName: string
  visible: number
  coverId?: string
  intro?: string
}

interface UserStarParams {
  userId?: string
  videoId?: string
}