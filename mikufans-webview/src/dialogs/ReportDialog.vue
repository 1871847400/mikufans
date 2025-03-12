<template>
  <el-dialog v-model="visible" title="请选择举报理由" center width="500px" :close-on-click-modal="false">
    <el-form label-position="top" v-loading="isLoading">
      <template v-for="list,key in groups">
        <el-form-item :label="key+''">
          <el-radio-group v-model="form.behaviorId">
            <template v-for="{ id, behavior } in list">
              <el-radio :value="id" :label="behavior" />
            </template>
          </el-radio-group>
        </el-form-item>
      </template>
      <el-form-item label="详细理由">
        <el-input
          v-model="form.reason"
          type="textarea" 
          :rows="3" 
          resize="none" 
          :maxlength="500"
          show-word-limit
          placeholder="理由充分方便更快审核哦！"
        />
      </el-form-item>
      <el-form-item>
        <el-button class="ml-auto" @click="visible=false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">提交</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts">
import reportApi from '@/apis/user/report'
import { groupBy } from 'lodash';
const { reportType, targetId } = defineProps<{
  reportType: BusinessType,
  targetId: string
}>()
const visible = ref(true)
const { state, isLoading } = useAsyncState(init, [])
const groups = computed(()=>groupBy(state.value, 'category'))
const form = reactive<UserReportDto>({
  behaviorId: '',
  reason: '',
  reportType,
  targetId
})
async function init() {
  const data = await reportApi.getOne(targetId)
  if (data) {
    form.behaviorId = data.behaviorId
    form.reason = data.reason
  }
  return reportApi.getBehaviors()
}
const submitting = ref(false)
async function onSubmit() {
  try {
    submitting.value = true
    await reportApi.create(form)
    visible.value = false
    message.success('提交成功,请耐心等待反馈!')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
</style>