<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="editRole"
        :update="editRole"
        :remove="removeRole"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="id" prop="id" width="100"/>
        <el-table-column label="角色标识" prop="roleKey" width="200"/>
        <el-table-column label="角色名称" prop="roleName" width="200"/>
        <el-table-column label="创建人" prop="createByName" width="120"/>
        <el-table-column label="创建时间" prop="createTime" width="200"/>
        <el-table-column label="备注" prop="remark" width="120"/>
        <el-table-column label="操作" width="240" v-slot="{ row }">
          <el-button icon="edit" type="success" @click="editRole(row)">编辑</el-button>
          <el-button icon="delete" type="danger" @click="removeRole(row)">删除</el-button>
        </el-table-column>
      </el-table>
      <miku-pagination v-model="queries.pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { pageModel, removeModel, updateModel } from '@/apis/admin/model';
import { usePage } from '@/hooks/usePage';
import { useRouteQueries } from '@/utils/route';
import { openDialog } from '@/utils/dialog';
import SysRoleDialog from './SysRoleDialog.vue';
const selections = ref<SysRole[]>([])
const route = useRoute()
const { queries, commit, reset } = useRouteQueries({
  pageNum: 1
}, { autoCommit: ['pageNum'] })
const { page, loading, run } = usePage(()=>pageModel('sys_role', queries))
watch(()=>route.query, ()=>run(queries.pageNum), { immediate: true, deep: true })
async function editRole(sysRole?: SysRole) {
  await openDialog(SysRoleDialog, { sysRole, onSubmit: ()=>run(queries.pageNum) })
}
async function removeRole(...sysRole: SysRole[]) {
  await message.confirm(`确认删除${sysRole.map(a=>a.roleName)}吗`)
  await removeModel('sys_role', sysRole.map(a=>a.id))
  run(queries.pageNum)
}
</script>

<style scoped lang="scss">
</style>