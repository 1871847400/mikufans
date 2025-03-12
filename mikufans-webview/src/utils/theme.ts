const LOCAL_KEY = '__theme__'

export type Theme = 'light' | 'dark' | 'os'
const theme = useStorage<Theme>(LOCAL_KEY, 'light')

const prefers = matchMedia('(prefers-color-schmem: dark)')

function followOS() {
  if (prefers.matches) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

watchEffect(()=>{
  if (theme.value === 'os') {
    followOS()
    prefers.addEventListener('change', followOS)
  } else {
    prefers.removeEventListener('change', followOS)
    //注意element-plus使用.dark标记使用暗色主题
    if (theme.value === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }
})

const themes = <{
  value: Theme,
  label: string,
  icon: string,
}[]>[
  {
    value: 'light',
    label: '亮色',
    icon: '&#xe693;',
  },
  {
    value: 'dark',
    label: '暗色',
    icon: '&#xe6a8;',
  },
  // {
  //   value: 'os',
  //   label: '系统',
  //   icon: '&#xe6d2;',
  // },
]

export function useTheme() {
  function change(t?: Theme) {
    if (t) {
      theme.value = t
    } else {
      const index = themes.findIndex(i=>i.value===theme.value)
      theme.value = themes[(index+1)%themes.length].value
    }
  }
  return {
    theme, themes, change
  }
}