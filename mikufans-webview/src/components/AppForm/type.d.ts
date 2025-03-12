type FormItemType = 'TEXT'|'TEXTAREA'|'PASSWORD'|'NUMBER'|'SELECT'|'IMAGE'|'DATE'|'RADIO'|'COLOR'|'ICON'

type Scope = 'NONE'|'CREATE'|'UPDATE'|'BOTH'

interface FormItem {
  type: FormItemType
  isText: boolean
  label: string
  placeholder: string
  required: Scope
  maxlength: number
  timeFormat: string
  rows: number
  min: number
  max: number
  disabled: Scope
  readonly: Scope
  value: string
  dict: string
}

interface FormSelect {
  filterable: boolean
  allowCreate: boolean
  defaultFirstOption: boolean
  empty: string[]
  multiple: boolean
  multipleLimit: number
}

interface FormImage {
  width: string
  height: string
  radius: string
}

interface AppForm {
  entityName: string
  tableName: string
  form: {
    name: string
    labelWidth: string
    labelPosition: "top" | "left" | "right"
    labelSuffix: string
  }
  formItems: Record<string, FormItem>
  selects: Record<string, FormSelect>
  options: Record<string, Option[]>
  images: Record<string, FormImage>
}