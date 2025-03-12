interface UserPublish extends BaseEntity {
  title: string
  content: string
  imgIds: string[]
}

interface UserPublishDto {
  title: string
  content: string
  imgIds?: string[]
  dynamic: UserDynamicDto
}