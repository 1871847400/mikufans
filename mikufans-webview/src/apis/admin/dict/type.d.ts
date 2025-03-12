interface SysDictType extends BaseEntity {
  dictName: string
  dictType: string
  mutable: number
}

interface SysDictData extends BaseEntity {
  dictType: string
  dictLabel: string
  dictValue: string
  digital: number
  dictSort: number
  defFlag: number
  disabled: number
  tagType: string
  iconName: string
  iconColor: string
}