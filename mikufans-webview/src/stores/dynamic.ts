import { createEventEmitter } from "@/hooks/useEvent"

export const useDynamicStore = defineStore('user-dynamic', ()=>{
  const fakeUser = {
    id: '0',
    nickname: '全部动态',
  } as UserFollowVo
  //当前查看的用户
  const activeUser = shallowRef<UserFollowVo>(fakeUser)
  const { emitEvent, useEvent } = createEventEmitter<{ 
    newDynamic: UserDynamic,
    delDynamic: string,
  }>()
  return {
    fakeUser,
    activeUser,
    emitEvent,
    useEvent,
  }
})