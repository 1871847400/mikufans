interface UserAlbum extends BaseEntity {
  pid: string
  category: number
  title: string
  imgId: string
  sort: number
  favor: number
  userFavor: number //用户评价值
  createDate: string //创建日期
}

interface UserAlbumDto {
  id?: string //更新需要
  pid?: string
  category: number
  title: string
  imgId: string
}

interface UserAlbumSearch extends SearchParams {
  pid: string
  userId?: string
  category?: number
  title?: string
}