<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="edit"
        :update="edit"
        :remove="remove"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="id" prop="id" width="80"/>
        <el-table-column label="词条" prop="term" width="240"/>
        <el-table-column label="敏感词" width="100" v-slot="{ row }">
          <el-tag v-if="row.illegal" type="danger">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-table-column>
        <el-table-column label="备注" prop="remark" width="200"/>
        <el-table-column label="创建人" prop="createByName" width="100"/>
        <el-table-column label="创建时间" prop="createTime" width="180"/>
        <el-table-column label="操作" width="150">
          <template v-slot="{ row }">
            <el-button icon="edit" type="success" @click="edit(row)"/>
            <el-button icon="delete" type="danger" @click="remove(row)"/>
          </template>
        </el-table-column>
      </el-table>
      <miku-pagination v-model="pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { pageModel, removeModel, updateModel } from '@/apis/admin/model';
import { usePage } from '@/hooks/usePage';
import { openDialog } from '@/utils/dialog';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const selections = ref<SysExtDict[]>([])
const pageNum = useRouteQuery('pageNum', 1)
const { page, loading, run } = usePage((pageNum)=>pageModel('sys_ext_dict', { pageNum }))
watchImmediate(pageNum, ()=>run(pageNum.value))
async function edit(model?: SysExtDict) {
  await openDialog(AppFormDialog, {
    model,
    name: '扩展词库',
    table: 'sys_ext_dict',
    onSubmit: ()=>run(pageNum.value) 
  })
}
async function remove(...models: SysExtDict[]) {
  await message.confirm(`确认删除${models.map(a=>a.term)}吗`)
  await removeModel('sys_ext_dict', models.map(a=>a.id))
  run(pageNum.value)
}
</script>

<style scoped lang="scss">
</style>