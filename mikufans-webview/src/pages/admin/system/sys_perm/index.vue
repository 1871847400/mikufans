<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="editSysPerm"
        :update="editSysPerm"
        :remove="removeSysPerm"
      />
      <el-table row-key="id" :tree-props="{ children: 'children', checkStrictly: true }" :fit="false" :data="data" @selection-change="selections=$event" stripe highlight-current-row>
        <el-table-column type="selection" width="50"/>
        <!-- <el-table-column label="id" prop="id" width="100"/> -->
        <el-table-column label="权限名称" prop="permName" width="200"/>
        <el-table-column label="权限标识" prop="permKey" width="300"/>
        <el-table-column label="创建人" prop="createByName" width="200"/>
        <el-table-column label="备注" prop="remark" width="120"/>
        <el-table-column label="操作" width="320">
          <template v-slot="{ row }">
            <el-button icon="edit" type="success" @click="editSysPerm(row)">编辑</el-button>
            <el-button icon="plus" type="primary" @click="editSysPerm(null, row)">新增</el-button>
            <el-button icon="delete" type="danger" @click="removeSysPerm(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { listModel, pageModel, removeModel, updateModel } from '@/apis/admin/model';
import { openDialog } from '@/utils/dialog';
import SysPermDialog from './SysPermDialog.vue';
import { usePermsTree } from './hook';
const selections = ref<SysPerm[]>([])
const { data, refresh } = usePermsTree()
function editSysPerm(sysPerm?: SysPerm, parent?: SysPerm) {
  openDialog(SysPermDialog, { 
    sysPerm,
    parent,
    options: data.value,
    onSubmit: refresh,
  })
}
async function removeSysPerm(...sysPerm: SysPerm[]) {
  await message.confirm(`确认删除${sysPerm.map(a=>a.permName)}吗`)
  await removeModel('sys_perm', sysPerm.map(a=>a.id))
  message.success('删除成功')
  refresh()
}
</script>

<style scoped lang="scss">
</style>