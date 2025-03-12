interface SearchHistory extends BaseEntity {
  id: string
  content: string
  searchCount: number
  userId: string
}

interface SearchParamsCombine {
  video: VideoSearchParams
  anime: VideoSearchParams
  movie: VideoSearchParams
  user: UserSearchParams
}

interface SearchResultCombine {
  video: Page<Video>
  anime: Page<Video>
  movie: Page<Video>
  user: Page<User>
}