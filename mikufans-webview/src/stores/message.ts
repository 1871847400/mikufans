import { defineStore } from "pinia";
import msgApi from '@/apis/user/message'
import userDynamicApi from '@/apis/user/dynamic';
import { useSharedSocket } from "@/hooks/useSocket";

export const useMsgStore = defineStore('msg', ()=>{
  const route = useRoute()
  const router = useRouter()
  //当前的聊天对象
  const targetId = computed({
    get() {
      return route.hash?.replace('#', '')
    },
    set(__targetId) {
      const hash = __targetId ? '#' + __targetId : null
      if (route.hash !== hash) {
        router.replace({ hash })
      }
    }
  })
  const msgUnread = reactive<MsgUnread>({
    whisper: 0,
    reply: 0,
    atuser: 0,
    likes: 0,
    systems: 0,
    total: 0,
  })
  async function update() {
    const userStore = useUserStore()
    if (userStore.isLogin) {
      const data = await msgApi.getMsgUnread()
      Object.assign(msgUnread, data)
    }
  }
  const { useSocketEvent } = useSharedSocket('/msg')
  useSocketEvent('connect', ()=>{
    logger.debug('socketio 成功连接');
  })
  useSocketEvent('disconnect', ()=>{
    logger.debug('socketio 断开连接');
  })
  useSocketEvent('msg_unread', (res: MsgUnread)=>{
    Object.assign(msgUnread, res)
  })
  const unreadDynamic = useAsyncState(()=>userDynamicApi.getUnread('VIDEO'), 0, {resetOnExecute:false})
  update()
  return {
    update,
    targetId,
    msgUnread,
    unreadDynamic,
    useSocketEvent,
  }
})