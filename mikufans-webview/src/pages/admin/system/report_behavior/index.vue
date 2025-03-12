<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="edit"
        :update="edit"
        :remove="remove"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="id" prop="id" width="80"/>
        <el-table-column label="分类" prop="category" width="150"/>
        <el-table-column label="行为" prop="behavior" width="200"/>
        <el-table-column label="介绍" prop="intro" width="200"/>
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
import { pageModel, removeModel } from '@/apis/admin/model';
import { openDialog } from '@/utils/dialog';
import { usePage } from '@/hooks/usePage';
import { toInteger } from 'lodash';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const selections = ref<ReportBehavior[]>([])
const pageNum = useRouteQuery('page', 1, { mode: 'push', transform: toInteger })
const { page, list, loading, run } = usePage((pageNum)=>pageModel<ReportBehavior>('report_behavior', { pageNum }))
watchImmediate(pageNum, run)
function edit(model?: ReportBehavior) {
  openDialog(AppFormDialog, {
    model,
    name: '举报行为',
    table: 'report_behavior',
    onSubmit: ()=>run(pageNum.value) 
  })
}
async function remove(...row: ReportBehavior[]) {
  await message.confirm(`确认删除${row.map(a=>a.behavior)}吗`)
  await removeModel('report_behavior', row.map(a=>a.id))
  run(pageNum.value)
}
</script>

<style scoped lang="scss">
</style>