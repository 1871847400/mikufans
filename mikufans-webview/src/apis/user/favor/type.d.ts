interface UserFavor extends BaseEntity {
  targetType: 'VIDEO'|'COMMENT'|'RATE'|'STAR'|'ALBUM'|'PUBLISH'|'DANMU'
  targetId: string
  targetLabel: string
  targetUserId: string
  targetTitle: string
  targetPath: string
  favorValue: number
  readFlag: ReadFlag
}

type UserFavorDto = Pick<UserFavor, 'targetType'|'targetId'|'favorValue'>