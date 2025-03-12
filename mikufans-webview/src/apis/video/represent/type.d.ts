interface VideoRepresent extends BaseEntity {
  videoId: string
  reason: string
  sort: number
  video: Video
}

interface VideoRepresentDto {
  videoId: string
  reason: string
}