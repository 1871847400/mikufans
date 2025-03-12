import { listModel } from "@/apis/admin/model"
import { findDeep } from "@/utils/common"

export interface SysPermWithChildren extends SysPerm {
  children?: SysPermWithChildren[]
}
export function usePermsTree() {
  const { state, execute } = useAsyncState(()=>listModel<SysPerm>('sys_perm'), [])
  const data = computed(()=>{
    let result : SysPermWithChildren[] = [...state.value]
    const records = state.value
    for (const record of records) {
      for(const r of records) {
        if (record.pid === r.id) {
          const target = findDeep(result, 'children', a=>a.id===r.id)
          if (Array.isArray(target.children)) {
            target.children.push(record)
          } else {
            target.children = [record]
          }
          result = result.filter(a=>a.id!==record.id)
          break
        }
      }
    }
    return result
  })
  return { data, refresh: execute }
}