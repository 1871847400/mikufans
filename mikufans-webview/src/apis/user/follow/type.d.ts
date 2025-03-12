interface UserFollowSearch extends SearchParams {
  userId?: string //默认自己
  sort?: 'ACCESS'|'FOLLOW_TIME'|'LAST_DYNAMIC'
  keyword?: string
  asc?: boolean
}

interface UserFollow extends BaseEntity {
  targetId: string
  accessCount: number
  accessTime: string
}

type UserFollowStatus = 'UNFOLLOWED'|'FOLLOWED'|'EACH_FOLLOWED'

interface UserFollowVo {
  id: string //用户id
  nickname: string
  sign: string
  avatarId: string
  level: number
  fans?: number //粉丝数量
  highlighted?: string //搜索了关键词才存在
  follow: UserFollowStatus //关注状态
  news: boolean //是否有最新未查看的动态
  uri: string
}