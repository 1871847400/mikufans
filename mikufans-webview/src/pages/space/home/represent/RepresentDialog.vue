<template>
  <el-dialog v-model="visible" title="选择一个视频作为代表作" top="15px" center width="650px" :close-on-click-modal="false">
    <search-box style="width: 100%;" v-model="keyword" :stretch="false" :debounce="600"/>
    <el-scrollbar height="400px">
      <Async always :loading="loading" :empty="list.length==0" :error="error" min-h="340px">
        <div class="list-view" v-infinite-scroll="next" :infinite-scroll-disabled="loading">
          <template v-for="i in list" :key="i.id">
            <div class="list-item" :class="{select:form.videoId===i.id}" @click="select(i)">
              <div class="relative">
                <miku-image class="w-[120px]" video :res-id="i.bannerId"/>
                <span class="duration">{{ displayDuration(i.duration) }}</span>
              </div>
              <div>
                <div class="pb-5">{{ i.title }}</div>
                <div class="flex items-center gap-8 text-xs grey1">
                  <div>
                    <i class="iconfont icon-24gl-playCircle mr-2"></i>
                    <span>{{ i.plays }}</span>
                  </div>
                  <div>
                    <i class="iconfont icon-lishi mr-2"></i>
                    <span>{{ i.dynamic.publishTimeStr }}</span>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </Async>
    </el-scrollbar>
    <template #footer>
      <el-input 
        v-visible="form.videoId" 
        v-model="form.reason" 
        type="textarea" 
        placeholder="请输入理由" 
        resize="none" 
        maxlength="50" 
        show-word-limit
      />
      <el-button type="primary" class="w-[150px] h-[40px] mt-4" :disabled="!form.videoId" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { usePage } from '@/hooks/usePage';
import videoApi from '@/apis/video';
import { displayDuration } from '@/utils/common';
import videoRepresentApi from '@/apis/video/represent';
const props = defineProps<{ data?: VideoRepresent }>()
const emits = defineEmits<{ submit: [] }>()
const userStore = useUserStore()
const visible = ref(true)
const keyword = ref('')
const form = reactive<VideoRepresentDto>({
  videoId: props.data?.videoId,
  reason: props.data?.reason ?? '',
})
const { list, loading, run, next, error } = usePage(search)
watch(keyword, ()=>{
  list.value = []
  run(1)
})
function select(video: Video) {
  if (form.videoId === video.id) {
    form.videoId = null
  } else {
    form.videoId = video.id
  }
}
function search(pageNum: number) {
  return videoApi.search({
    keyword: keyword.value,
    userId: userStore.id,
    sort: 'TIME',
    pageNum,
    pageSize: 10,
  })
}
async function submit() {
  await videoRepresentApi.saveDto(toRaw(form))
  message.success('更新成功')
  visible.value = false
  emits('submit')
}
next()
</script>

<style scoped lang="scss">
.list-view {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin: 16px 0;
  padding-right: 18px;
}
.list-item {
  position: relative;
  display: flex;
  gap: 16px;
  padding: 6px;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  * {
    pointer-events: none;
  }
  &.select {
    color: #fff;
    background-color: var(--blue2);
  }
  .duration {
    position: absolute;
    right: 8px;
    bottom: 8px;
    color: #fff;
    border-radius: 6px;
    padding: 1px 4px;
    background-color: #0000008c;
    font-size: 12px;
    pointer-events: none;
    user-select: none;
  }
}
</style>