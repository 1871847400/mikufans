<template>
  <Async :loading="isLoading" :empty="state==null" :error="error" min-h="400px">
    <div>
      <div class="flex gap-4 pt-2 pb-4">
        <el-button link icon="back" @click="emits('close')" size="large"></el-button>
        <div>
          <div class="maxline-1 mb-1">{{ state.title }}</div>
          <div class="flex gap-2">
            <span v-for="tag in state.tags" class="video-tag">{{ tag }}</span>
          </div>
        </div>
      </div>
      <div class="flex gap-4">
        <div class="flex-1">
          <video ref="videoRef" controls autoplay loop></video>
        </div>
        <div class="w-[350px] mx-3">
          <el-form v-if="state.auditCount>0" label-width="90px" label-position="top" label-suffix=":">
            <el-form-item label="标题">
              <div class="bg1 px-2 rounded">{{ state.title }}</div>
            </el-form-item>
            <el-form-item label="简介">
              <div class="bg1 px-2 py-1 rounded">
                <rich-text :content="state.intro" html />
              </div>
            </el-form-item>
            <el-form-item label="转载地址" v-if="state.republish">
              <el-link type="primary" :href="state.sourceUrl" target="_blank">{{ state.sourceUrl }}</el-link>
            </el-form-item>
            <el-form-item label="分集列表">
              <part-list v-model="playIndex" :list="state.parts"/>
            </el-form-item>
            <el-form-item label="审核状态" required>
              <el-radio-group v-model="data.auditStatus" @change="data.auditReason=''">
                <el-radio label="通过" value="SUCCESS" />
                <el-radio label="不通过" value="FAIL" />
              </el-radio-group>
              <el-input v-if="data.auditStatus=='FAIL'" v-model="data.auditReason" type="textarea" placeholder="请输入原因" :rows="3" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="submit">提交</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      <action-bar/>
    </div>
  </Async>
</template>

<script setup lang="ts">
import auditApi from '@/apis/admin/audit'
import { createHls } from '@/components/VideoPlayer/functions/hls';
import { useReactive } from '@/hooks/useReactive';
import PartList from './PartList.vue';
import ActionBar from './ActionBar.vue';
import { baseURL } from '@/apis/service';
import video from '@/apis/video';
const props = defineProps<{ videoId: string }>()
const emits = defineEmits<{ close: [] }>()
const { state, isLoading, isReady, error } = useAsyncState(auditApi.getVideoAudit(props.videoId), null, {
  shallow: false
})
const videoRef = useTemplateRef('videoRef')
const playIndex = ref(-1)
const { data, reset } = useReactive<SysAuditDto>({
  id: '',
  auditStatus: 'SUCCESS',
  auditReason: '',
})
onMounted(()=>{
  watchOnce(isReady, ()=>playIndex.value=0, { flush: 'post' })
})
const hls = createHls()
watch(playIndex, (playIndex)=>{
  reset()
  if (playIndex < state.value.auditCount) {
    hls.loadSource(baseURL + '/admin' + state.value.parts[playIndex].masterUrl)
    hls.attachMedia(videoRef.value)
  }
})
const submitting = ref(false)
async function submit() {
  submitting.value = true
  try {
    data.id = state.value.parts[playIndex.value].sysAudit.id
    const result = await auditApi.submitAudit(data)
    state.value.parts[playIndex.value].sysAudit = result
    message.success('提交审核成功')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
video {
  width: 100%;
  height: auto;
  aspect-ratio: 16/9;
}
.video-tag {
  font-size: 12px;
  color: var(--grey2);
  border-radius: 4px;
  border: 1px solid var(--grey2);
  padding: 1px 4px;
}
:deep(.el-form-item__label) {
  font-weight: 550;
}
</style>