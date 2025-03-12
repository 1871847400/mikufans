<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow">
      <action-bar 
        :selections="selections"
        :create="edit"
        :update="edit"
        :remove="remove"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="id" prop="id" width="80"/>
        <el-table-column label="字典类型" prop="dictType" width="120"/>
        <el-table-column label="字典标签" prop="dictLabel" width="120"/>
        <el-table-column label="字典键值" prop="dictValue" width="120"/>
        <el-table-column label="排序" prop="dictSort" width="70"/>
        <el-table-column label="是否默认" prop="defFlag" width="100" v-slot="{ row }">
          <dict-tag type="is_status" :value="row.defFlag" />
        </el-table-column>
        <el-table-column label="是否禁用" prop="disabled" width="100" v-slot="{ row }">
          <dict-tag type="is_status" :value="row.defFlag" />
        </el-table-column>
        <el-table-column label="创建人" prop="createByName" width="100"/>
        <el-table-column label="创建时间" prop="createTime" width="180"/>
        <el-table-column label="操作" width="150" v-slot="{ row }">
          <el-button icon="edit" type="success" @click="edit(row)"/>
          <el-button icon="delete" type="danger" @click="remove(row)"/>
        </el-table-column>
      </el-table>
      <miku-pagination v-model="pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts" generic="T extends SysDictData">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { pageModel, removeModel } from '@/apis/admin/model';
import { openDialog } from '@/utils/dialog';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
import { usePage } from '@/hooks/usePage';
import { toInteger } from 'lodash';
const table = 'sys_dict_data'
const dictType = useRouteQuery<string>('type', '')
const selections = ref<T[]>([])
const pageNum = useRouteQuery('page', 1, { mode: 'push', transform: toInteger })
const { page, loading, run } = usePage((pageNum)=>pageModel<T>(table, { dictType: dictType.value, pageNum }))
watchImmediate(pageNum, run)
function edit(model?: T) {
  openDialog(AppFormDialog, {
    model: {
      ...model,
      dictType: dictType.value, 
    },
    table,
    onSubmit() {
      run(pageNum.value)
    }
  })
}
async function remove(...row: T[]) {
  await message.confirm(`确认删除${row.map(a=>a.dictLabel)}吗`)
  await removeModel(table, row.map(a=>a.id))
  run(pageNum.value)
}
</script>

<style scoped lang="scss">
</style>