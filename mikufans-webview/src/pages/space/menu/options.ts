export function createOptions(isSelf: boolean, isAllowed: (flag: USER_FLAGS)=>boolean) {
  const result = [
    {
      label: '主页',
      value: '',
      icon: {
        class: 'icon-shouyefill',
        color: '#00c091'
      },
    },
    {
      label: '动态',
      value: 'dynamic',
      icon: {
        class: 'icon-dongtai1',
        color: '#00c091'
      },
    },
    // {
    //   name: '相册',
    //   icon: {
    //     class: 'icon-charutupian',
    //     color: '#01b4cf'
    //   }
    // },
  ]
  if (isAllowed('PUBLIC_UPLOAD')) {
    result.push({
      label: '投稿',
      value: 'work',
      icon: {
        class: 'icon-shipin',
        color: '#02b5da'
      }
    })
  }
  if (isAllowed('PUBLIC_STAR')) {
    result.push({
      label: '收藏',
      value: 'star',
      icon: {
        class: 'icon-shoucang5',
        color: '#f3a034'
      }
    })
  }
  if (isAllowed('PUBLIC_SUBSCRIBE')) {
    result.push({
      label: '订阅',
      value: 'subscribe',
      icon: {
        class: 'icon-shoucang4',
        color: '#ff5d47'
      }
    })
  }
  if (isSelf) {
    result.push({
      label: '设置',
      value: 'account',
      icon: {
        class: 'icon-24gf-gear3',
        color: '#23c9ed',
      }
    })
  }
  return result
}