type MessageType = '' | 'success' | 'warning' | 'info' | 'error'

// 将指定类型的部分属性设置为“可选”
type Optional<T, K extends keyof T> = Omit<T, K> & Partial<Pick<T, K>>

//通用更改排序参数
interface OrderDto {
  ids: string[]
}

//获取枚举的值(数字)
type EnumValues<T extends string | number> = T extends string ? `${T}` | T : T;

//限制为数组内的元素
type ArrayValue<T = Readonly<Array<number & string>>> = T[number & string]

//将{a:()=>T}这种装方法对象=>{a:T}直接对应返回值
type MethodToField<T extends Record<string, (...args) => any>> = {
  [K in keyof T]: ReturnType<T[K]>
}

//只提取对象中的特定类型
//例如: PickType<User, number> user.age user.level
type PickType<T, R> = Pick<T, {
  [K in keyof T]: T[K] extends R ? K : never
}[keyof T]>