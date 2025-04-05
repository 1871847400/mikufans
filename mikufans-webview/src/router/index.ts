import { createRouter,createWebHistory,RouteLocationNormalized,RouteRecordRaw, useRoute } from 'vue-router'
import { isMobile } from '@/utils/common'
import nProgress from 'nprogress'
import { generate } from './generator'

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/pages/home/index.vue'),
    meta: {
      title: '首页'
    },
  },
  {
    name: 'login',
    path: '/login',
    component: () => import('@/pages/login/index.vue'),
    meta: {
      title: '登录'
    }
  },
  {
    name: 'register',
    path: '/register',
    component: () => import('@/pages/login/index.vue'),
    meta: {
      title: '注册'
    }
  },
  {
    name: 'oauth-redirect',
    path: '/oauth/redirect/:type',
    component: () => import('@/pages/oauth/index.vue'),
    meta: {
      title: '第三方登录中...'
    }
  },
  {
    path: '/protocol/privacy',
    component: () => import('@/pages/protocal/index.vue'),
    meta: {
      title: '隐私政策'
    }
  },
  {
    path: '/protocol/licence',
    component: () => import('@/pages/protocal/index.vue'),
    meta: {
      title: '用户协议'
    }
  },
  {
    path: '/channel/:uri+',
    component: ()=>import('@/pages/channel/index.vue'),
  },
  {
    path: '/anime',
    component: () => import('@/pages/anime/index.vue'),
    meta: {
      title: '番剧'
    }
  },
  {
    path: '/movie',
    component: () => import('@/pages/movie/index.vue'),
    meta: {
      title: '电影'
    }
  },
  {
    name: 'space',
    path: '/space/:uid(\\d+)?',
    component: () => import('@/pages/space/index.vue'),
    meta: {
      title: '个人空间',
    },
    children: [
      {
        name: 'space-home',
        path: '',
        component: ()=>import('@/pages/space/home/index.vue'),
        meta: {
          title: '主页 - 个人空间'
        }
      },
      {
        path: 'dynamic',
        component: ()=>import('@/pages/space/dynamic/index.vue'),
        meta: {
          title: '动态 - 个人空间'
        }
      },
      {
        path: 'work',
        component: ()=>import('@/pages/space/work/index.vue'),
        meta: {
          title: '投稿 - 个人空间',
          scroll: 0,
        }
      },
      {
        path: 'album',
        component: ()=>import('@/pages/space/album/index.vue'),
        meta: {
          title: '相册 - 个人空间'
        }
      },
      {
        path: 'star',
        component: ()=>import('@/pages/space/star/index.vue'),
        meta: {
          title: '收藏 - 个人空间',
        }
      },
      {
        path: 'subscribe',
        component: ()=>import('@/pages/space/subscribe/index.vue'),
        meta: {
          title: '订阅 - 个人空间',
        }
      },
      {
        path: 'follow',
        component: ()=>import('@/pages/space/follow/index.vue'),
        meta: {
          title: '关注 - 个人空间',
        }
      },
      {
        path: 'fans',
        component: ()=>import('@/pages/space/follow/index.vue'),
        meta: {
          title: '粉丝 - 个人空间',
        }
      },
      {
        path: 'account',
        component: ()=>import('@/pages/space/account/index.vue'),
        meta: {
          title: '设置 - 个人空间'
        }
      },
    ],
    beforeEnter(to, from, next) {
      if (to.params['uid']) {
        return next()
      }
      const userStore = useUserStore()
      if (userStore.isLogin) {
        logger.debug('自动拼接用户id:',userStore.id, to)
        next({ path: to.path + '/' + userStore.id })
      } else {
        message.warning('请先登录！')
        next('/login')
      }
    },
  },
  {
    name: 'search',
    path: '/search/:type?',
    component: () => import('@/pages/search/index.vue'),
    beforeEnter(to, from, next) {
      if (to.query.kw) {
        document.title = to.query.kw + ' - Mikufans'
      }
      next()
    },
  },
  {
    name: 'upload',
    path: '/upload',
    redirect: '/creator/upload'
  },
  {
    name: 'creator',
    path: '/creator',
    component: () => import('@/pages/creator/index.vue'),
    meta: {
      title: '创作中心',
      login: true
    },
    children: [
      {
        name: 'creator-home',
        path: '',
        component: ()=>import('@/pages/creator/home/index.vue'),
      },
      {
        path: 'upload',
        // component: ()=>import('@/pages/creator/upload/video/index.vue'),
        redirect: '/creator/upload/video',
        children: [
          {
            path: 'video',
            component: ()=>import('@/pages/creator/upload/video/index.vue')
          },
          {
            path: 'article',
            component: ()=>import('@/pages/creator/upload/article/index.vue')
          },
        ]
      },
      {
        path: 'content',
        component: ()=>import('@/pages/creator/content/index.vue'),
        redirect: '/creator/content/video',
        children: [
          {
            path: 'video',
            component: ()=>import('@/pages/creator/content/video/index.vue'),
          },
          {
            path: 'article',
            component: ()=>import('@/pages/creator/content/article/index.vue'),
          },
        ]
      },
      {
        path: 'data',
        component: ()=>import('@/pages/creator/data/index.vue'),
      },
    ]
  },
  {
    name: 'history',
    path: '/history',
    component: () => import('@/pages/history/index.vue'),
    meta: {
      title: '历史记录',
      login: true
    }
  },
  {
    name: 'message',
    path: '/msg',
    component: () => import('@/pages/message/index.vue'),
    meta: {
      title: '消息中心',
      login: true,
    },
    children: [
      {
        path: 'whisper',
        component: ()=>import('@/pages/message/whisper/index.vue'),
        meta: {
          title: '我的消息'
        }
      },
      {
        path: 'reply',
        component: ()=>import('@/pages/message/reply/index.vue'),
        meta: {
          title: '回复我的'
        }
      },
      {
        path: 'at',
        component: ()=>import('@/pages/message/reply/index.vue'),
        props: {
          at: true
        },
        meta: {
          title: '@我的'
        }
      },
      {
        path: 'like',
        meta: {
          title: '收到的赞'
        },
        children: [
          {
            path: '',
            component: ()=>import('@/pages/message/like/index.vue'),
          },
          {
            path: ':id',
            component: ()=>import('@/pages/message/like/LikeList.vue'),
            props(to) {
              return {
                id: to.params.id,
              }
            },
            meta: {
              title: '点赞详情',
            }
          }
        ]
      },
      {
        path: 'system',
        component: ()=>import('@/pages/message/system/index.vue'),
        meta: {
          title: '系统通知'
        }
      },
    ],
  },
  {
    name: 'bangumi',
    path: '/bangumi/:id',
    component: () => import('@/pages/bangumi/index.vue'),
    children: [
      {
        name: 'bangumi-detail',
        path: '',
        component: ()=>import('@/pages/bangumi/detail/index.vue')
      },
      {
        path: 'comment',
        component: ()=>import('@/pages/bangumi/comment/index.vue')
      },
      {
        path: 'refer',
        component: ()=>import('@/pages/bangumi/refer/index.vue')
      }
    ]
  },
  {
    name: 'video',
    path: '/video/:sid/:part?', //例如: /video/abc123/1
    component: () => import('@/pages/video/index.vue'),
  },
  {
    name: 'dynamic',
    path: '/dynamic/:id?',
    component: () => import('@/pages/dynamic/index.vue'),
    meta: {
      login: true,
      title: '动态',
    }
  },
  {
    name: 'index',
    path: '/index',
    component: () => import('@/pages/index/index.vue'),
    meta: {
      title: '目录索引'
    }
  },
  {
    name: 'mobile',
    path: '/mobile',
    component: ()=>import('@/pages/mobile/index.vue'),
  },
  {
    name: '404',
    path: '/:pathMatch(.*)', //最终匹配,放在最后来重定向到404
    component: ()=>import('@/pages/common/404.vue'),
    meta: {
      title: '页面走丢了'
    },
    beforeEnter: (to, from, next) => {
      logger.debug('404', from, to)
      next()
    }
  },
]
//在开发模式下快速访问test目录的页面,无需单独添加映射,但需要以test开头
if (import.meta.env.DEV) {
  routes.push({
    path: '/:test(test.*)',
    component() {
      const name = location.pathname
        .replace('/', '')
      return import(/*@vite-ignore*/'../pages/test/'+name+'.vue') 
    },
  })
}

const router = createRouter({
  history: createWebHistory(),
  routes,
  // 每次切换路由后滚动到顶部
  // 在按下 后退/前进 按钮时才会有savedPosition
  scrollBehavior(to, from, savedPosition) {
    logger.debug(to, savedPosition)
    if (savedPosition) {
      return savedPosition
    } else {
      // if (typeof to.meta.scroll === 'number') {
      //   return { top: to.meta.scroll }
      // }
      //切换分页时才到顶部
      if (to.query.page !== from.query.page || to.query.pageNum !== from.query.pageNum) {
        return { top: 0 }
      }
    }
  }
})

nProgress.configure({
  showSpinner: false,
})

/**
 * 进入每个路由的前置方法
 *  meta支持bool或函数
 *  注意router加载会先于store
 */
router.beforeEach(async (to, from, next) => {
  nProgress.start()
  if (isMobile() && to.name !== 'mobile') {
    logger.info('正在从手机端访问主页,重定向到APP下载页面')
    return next({ path: '/mobile' })
  }
  const userStore = useUserStore()
  //进入路由前必须初始化用户信息,否则无法判断是否登录
  try {
    await userStore.getUserInfo()
  } catch (err) {

  }
  //检测是否为必须登录后才能进的页面
  if (to.meta.login && !userStore.isLogin) {
    message.error('请先登录')
    return next('/login')
  }
  //更改网站标题
  const title = to.meta.title
  if (typeof title === 'string') {
    document.title = title + ' - Mikufans'
  } else {
    document.title = 'Mikufans'
  }
  next()
})

/**
 * 解析守卫会在导航被确认之前、所有组件内守卫和异步路由组件被解析之后调用
 */
router.beforeResolve((to, from, next)=>{
  next()
})

router.afterEach((to, from, failure)=>{
  nProgress.done()
  if (failure) {
    return logger.debug('failure', failure)
  }
})

/**
 * 管理员页面自动生成路由
 */
generate(router)
/**
 * 单独设置登录页面,相对其它管理页面独立显示
 */
router.addRoute({
  path: '/admin/login',
  component: ()=>import('@/pages/admin/login/index.vue'),
  meta: {
    title: '用户登录',
    icon: 'Stamp',
    sort: Number.MAX_VALUE,
  }
})

// console.log(router.getRoutes().filter(a=>a.path.startsWith('/admin')));

export default router