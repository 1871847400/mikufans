import DynamicList from './DynamicList.vue'
import MsgNav from './MsgNav.vue'
import PlayHistory from './PlayHistory.vue'
import UserStar from './UserStar.vue'

export function getNavIconOptions() {
  const userStore = useUserStore()
  const { msgUnread, unreadDynamic } = toRefs(useMsgStore())
  return [
    {
      label: "消息",
      icon: "message",
      path: "/msg/whisper",
      count: toRef(()=>msgUnread.value.total),
      width: '140px',
      offset: [0, 12],
      component: MsgNav,
    },
    {
      label: "动态", 
      icon: "person-active", 
      path: "/dynamic", 
      count: toRef(()=>unreadDynamic.value.state),
      width: '300px',
      offset: [0, 12],
      component: DynamicList,
    },
    { 
      label: "收藏", 
      icon: "favorite", 
      path: `/space/${userStore.id}/star`, 
      count: 0,
      width: '550px',
      offset: [-120, 12],
      component: UserStar,
    },
    { 
      label: "历史", 
      icon: "clock",
      path: "/history", 
      count: 0,
      width: '350px',
      offset: [-60, 12],
      component: PlayHistory,
    },
    { 
      label: "创作", 
      icon: "creator",
      path: "/creator", 
      count: 0,
      width: '0px',
      offset: [0, 0],
    },
  ]
}