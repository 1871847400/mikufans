<template>
  <el-dialog v-model="dialog" width="600px" :title="title" center>
    <el-form label-position="right" label-suffix=":" label-width="100px">
      <el-form-item label="角色标识" required>
        <el-input v-model="form.roleKey" placeholder="请输入角色标识" />
      </el-form-item>
      <el-form-item label="角色名称" required>
        <el-input v-model="form.roleName" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="权限列表">
        <el-scrollbar style="width: 100%;" max-height="300px">
          <el-tree 
            ref="treeRef" 
            :data="data" 
            :default-checked-keys="form.permIds" 
            node-key="id" 
            :props="{ label:'permName' }" 
            show-checkbox
          />
        </el-scrollbar>
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
import { usePermsTree } from '../sys_perm/hook';
import { TreeKey } from 'element-plus';
import { uniq } from 'lodash';
const dialog = ref(true)
const { data } = usePermsTree()
const { sysRole } = defineProps<{ sysRole: SysRole }>()
const emits = defineEmits<{ submit: [] }>()
const form = reactive({
  ...sysRole
})
const title = sysRole ? '编辑角色' : '新增角色'
const treeRef = useTemplateRef('treeRef')
async function submit() {
  //父权限也会被分配给用户,方便前端判断是否有整个页面的权限
  //子权限用于按钮等更细节的权限
  form.permIds = uniq([
    ...treeRef.value.getCheckedKeys(false),
    ...treeRef.value.getHalfCheckedKeys()
  ]).map(a=>a+'')
  if (sysRole) {
    await updateModel('sys_role', form)
    message.success('更新成功')
  } else {
    await createModel('sys_role', form)
    message.success('创建成功')
  }
  emits('submit')
  dialog.value = false
}
</script>

<style scoped lang="scss">
</style>