<template>
  <el-dialog v-model="visible" title="添加到收藏夹" width="450px" center>
    <div class="body">
      <el-scrollbar ref="scrollRef" always height="250px">
        <div v-loading="isLoading">
          <template v-for="i in showList" :key="i.id">
            <el-checkbox 
              class="list-item"
              v-model="i.check"
              :disabled="videoStar && i._check"
              size="large" 
              :count="i.starCount+'/200'" 
              :style="`--tip:'${i.visible==0?'[私密]':''}'`"
            >
              {{ i.starName }}
            </el-checkbox>
          </template>
        </div>
      </el-scrollbar>
      <create-star @submit="onSubmit"/>
    </div>
    <template #footer>
      <el-button class="submit" :type="hasChanged?'primary':'info'" @click="save">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import userStarApi from '@/apis/user/star'
import CreateStar from './CreateStar.vue';
import videoStarApi from '@/apis/video/star';
import { ElScrollbar } from 'element-plus';
const props = defineProps({ 
  videoId: string().isRequired, //收藏哪个视频
  videoStar: object<VideoStar>(), //如果要复制或移动到其它收藏夹
})
const emits = defineEmits<{ submit: [boolean] }>()
const scrollRef = ref<InstanceType<typeof ElScrollbar>>(null)
const visible = ref(true)
type Item = UserStar & { check: boolean, _check: boolean }
const showList = ref<Item[]>([])
const { isLoading } = useAsyncState(userStarApi.search({ videoId: props.videoId }), [], {
  //将获取结果重新封装
  onSuccess(data) {
    for(const star of data) {
      showList.value.push({
        ...star, 
        check: !!star.videoStar,
        _check: !!star.videoStar
      })
    }
  },
})
//添加收藏夹时,放在默认收藏夹后一个
function onSubmit(data: UserStar) {
  showList.value = [
    showList.value[0], //默认收藏夹
    {
      ...data,
      starCount: 0, 
      check: false,
      _check: false,
    },
    ...showList.value.slice(1)
  ]
  scrollRef.value.scrollTo(0, 0)
}
//收藏信息是否改变
const hasChanged = computed(()=>{
  return showList.value.filter(a=>a.check!==a._check).length > 0
})
async function save() {
  if (hasChanged.value) {
    const status = await videoStarApi.save({
      videoId: props.videoId,
      addList: showList.value.filter(a=>a.check).map(a=>a.id),
      delList: showList.value.filter(a=>!a.check).map(a=>a.id),
    })
    message.success('已更新收藏')
    emits('submit', status)
  }
  visible.value = false
}
</script>

<style scoped lang="scss">
.el-dialog {
  --el-dialog-title-font-size: 14px;
  .el-scrollbar {
    padding: 0 28px;
  }
  .body {
    padding: 16px 0;
    border-top: 1px solid #6a6a6a30;
    border-bottom: 1px solid #6a6a6a30;
  }
}
.submit {
  width: 155px;
  height: 42px;
  font-size: 16px;
}
.list-item {
  height: 50px;
  flex: 1 0 0;
  padding-left: 4px;
  display: flex;
  // 清除checkbox自带margin
  margin-right: 0;
  //选中框
  :deep(.el-checkbox__input) {
    transform: scale(1.25);
    margin-right: 8px;
  }
  //内容
  :deep(.el-checkbox__label) {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    line-height: 1.2;
    flex: 1 0 0;
    &::after {
      content: var(--tip);
      font-style: italic;
      color: #999;
    }
  }
  &::after {
    content: attr(count);
    font-size: 12px;
    padding-left: 4px;
  }
}
</style>