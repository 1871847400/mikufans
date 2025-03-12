<template>
  <el-dialog v-model="dialog" width="500px" title="审核用户举报" center>
    <el-form label-position="right" label-suffix=":" label-width="100px">
      <el-form-item label="审核状态" required>
        <el-radio-group v-model="form.auidt" :empty-values="['UNKNOWN']" placeholder="请选择审核状态">
          <el-radio-button label="通过" value="SUCCESS" />
          <el-radio-button label="不通过" value="FAIL" />
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核结论" required>
        <el-input 
          v-model="form.message" 
          type="textarea" 
          placeholder="请输入审核结论" 
          :rows="3" 
          :maxlength="500" 
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      <el-button type="default" @click="dialog=false">取消</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import reportApi from '@/apis/admin/report'
const dialog = ref(true)
const { userReport } = defineProps<{ 
  userReport: UserReportVo,
}>()
const emits = defineEmits<{ submit: [] }>()
const form = reactive({
  ...userReport,
  auidt: 'UNKNOWN' as AuditStatus,
  message: '',
})
const submitting = ref(false)
async function submit() {
  try {
    submitting.value = true
    await reportApi.audit({ 
      targetId: form.targetId,
      reportType: form.reportType,
      audit: form.auidt,
      message: form.message,
    })
    emits('submit')
    dialog.value = false
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
</style>