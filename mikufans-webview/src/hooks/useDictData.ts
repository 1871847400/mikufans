import dictApi from '@/apis/admin/dict';

/**
 * 获取对应的字典数据
 * @param dictType 字典类型
 * @param dictValue 数据值
 * @returns 
 */
export function useDictData(dictType: string, dictValue: MaybeRefOrGetter<string | number>) {
  const { state } = useAsyncState(dictApi.getDictData(dictType), [])
  return computed(()=>state.value.find(a=>a.dictValue==toValue(dictValue)))
}