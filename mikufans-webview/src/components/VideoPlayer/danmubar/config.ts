export const danmuSetting = useLocalStorage('danmu-setting', {
  enable: true,
  density: 'pretty',
  syncVideo: false,
  fontSize: 16,
  lineHeight: 1.5,
  opacity: 1,
  showArea: 1,
  duration: 6000,
  font: 'Microsoft YaHei,sans-serif',
  paddingY: 4,
  ban: {
    color: false,
    fixed: false,
    roll: false
  }
} as DanmuBoxOptions, {
  deep: true,
  listenToStorageChanges: true,
  shallow: false,
  writeDefaults: true,
})

export const danmuStyle = reactive({
  type: 'ROLL' as DanmuType,
  color: '#FFF'
})

export const danmuInput = ref('')