interface UserCommentParams extends SearchParams {
  areaId: string //评论区id
  order?: number //0热度排序 1时间排序
  afterTime?: string //这个时间之后发送的评论
  hots?: number //附带热门子评论的数量
  replyId?: string
  replyUserId?: string
  userId?: string //限定发送评论的用户id
}

interface UserCommnetStats {
  unreadReply: number
  unreadAtUser: number
}

interface UserComment extends BaseEntity {
  areaId: string //评论区id
  uid: number //楼层
  pid: string //父评论id
  replyId: string //回复的评论id（和pid不同）
  replyUserId: string //回复目标的用户id
  imageIds: string[] //图片id列表
  atUsers: Record<string, string>
  childCount: number //子评论数量
  content: string //评论内容
  selected: number //是否为精选评论
  top: number //是否置顶
  isChild: boolean //是否为子评论
  publishTime: string //实际发布时间
  replyUser: BaseEntity['user']
  hots: UserComment[] //热评
  likeStatus: LikeStatus
}

interface UserCommentDto {
  areaId: string
  pid?: string
  replyId?: string
  imageIds?: string[]
  atUsers: Record<string, string>
  content: string
}

type CommentFlag = 'NORMAL'|'CHOICE'|'DISABLED'

interface UserCommentArea extends BaseEntity {
  commentFlag: CommentFlag
  userLevel: number
  comments: number
  newCommentId: string
  hotCommentId: string
  fstCommentId: string
  topCommentId: string
  busiId: string
  busiType: BusinessType
}

interface CommentAreaDto {
  id?: string
  commentFlag: CommentFlag
  userLevel: number
}

type CommentMsgType = 'REPLY'|'AT_USER'

interface CommentMsgParams extends SearchParams {
  msgType: CommentMsgType
}

interface UserCommentMsg extends BaseEntity {
  commentId: string
  msgType: CommentMsgType
  readFlag: number
  comment: UserComment
  commentArea: UserCommentArea
  source: MessageModel
  publishTime: string
  uri: string
}