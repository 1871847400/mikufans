interface VideoStar extends BaseEntity {
  videoId: string
  starId: string
  starDate: string
  video?: Video
  highlighted?: string
}

interface VideoStarDto {
  videoId: string 
  addList?: string[]
  delList?: string[]
}

interface VideoStarParams extends SearchParams {
  starId: string
  title?: string
  dir?: 'DESC'|'ASC'
}