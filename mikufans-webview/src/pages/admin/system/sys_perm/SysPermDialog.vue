<template>
  <el-dialog v-model="dialog" width="600px" :title="title" center>
    <el-form label-position="left" label-suffix=":" label-width="80px">
      <el-form-item label="权限组">
        <el-cascader
          style="width: 100%;"
          v-model="form.pid" 
          :options="options" 
          :disabled="!!parent" 
          :props="{label:'permName',value:'id',checkStrictly:true}"
          placeholder="请选择"
        />
      </el-form-item>
      <el-form-item label="权限标识">
        <el-input v-model="form.permKey" placeholder="请输入权限标识" />
      </el-form-item>
      <el-form-item label="权限名称">
        <el-input v-model="form.permName" placeholder="请输入权限名称" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button type="default" @click="dialog=false">取消</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { createModel, listModel, updateModel } from '@/apis/admin/model';
import message from '@/utils/message/message'
const dialog = ref(true)
const { sysPerm, parent, options } = defineProps<{ 
  sysPerm?: SysPerm,
  parent?: SysPerm,
  options: any[]
}>()
const emits = defineEmits<{ submit: [] }>()
const form = reactive({
  ...sysPerm
})
if (parent) {
  form.pid = parent.id
}
const title = sysPerm ? '编辑权限' : '新增权限'
async function submit() {
  if (sysPerm) {
    await updateModel('sys_perm', form)
    message.success('更新成功')
  } else {
    await createModel('sys_perm', form)
    message.success('创建成功')
  }
  emits('submit')
  dialog.value = false
}
</script>

<style scoped lang="scss">
</style>