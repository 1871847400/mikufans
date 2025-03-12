<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="editUser"
        :update="editUser"
        :remove="removeUser"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="id" prop="id" width="100"/>
        <el-table-column label="用户名" prop="username" width="200"/>
        <el-table-column label="创建人" prop="createByName" width="120"/>
        <el-table-column label="创建时间" prop="createTime" width="200"/>
        <el-table-column label="备注" prop="remark" width="120"/>
        <el-table-column label="操作" width="240" v-slot="{ row }">
          <el-button icon="edit" type="success" @click="editUser(row)">编辑</el-button>
          <el-button icon="edit" type="danger" @click="removeUser(row)">删除</el-button>
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
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const selections = ref<SysUser[]>([])
const table = 'sys_user'
const route = useRoute()
const { queries, commit, reset } = useRouteQueries({
  pageNum: 1
}, { autoCommit: ['pageNum'] })
const { page, loading, run } = usePage(()=>pageModel(table, queries))
watch(()=>route.query, ()=>run(queries.pageNum), { immediate: true, deep: true })
function editUser(sysUser?: SysUser) {
  openDialog(AppFormDialog, {
    table, 
    model: sysUser,
    onSubmit() {
      run(queries.pageNum)
    }
  })
}
async function removeUser(...sysUser: SysUser[]) {
  if (sysUser.find(a=>a.id=='1')) {
   return message.error('不允许删除最高管理员')
  }
  await message.confirm(`确认删除${sysUser.map(a=>a.username)}吗`)
  await removeModel(table, sysUser.map(a=>a.id))
  run(queries.pageNum)
}
</script>

<style scoped lang="scss">
</style>