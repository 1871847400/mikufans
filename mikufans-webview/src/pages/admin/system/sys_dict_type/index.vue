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
        <el-table-column label="字典名称" prop="dictName" width="150" v-slot="{ row }">
          <router-link class="hoverable" :to="'sys_dict_data?type='+row.dictType">{{ row.dictName }}</router-link>
        </el-table-column>
        <el-table-column label="字典类型" prop="dictType" width="200"/>
        <el-table-column label="是否可变" width="200" v-slot="{ row }">
          <dict-tag type="is_status" :value="row.mutable" />
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

<script setup lang="ts" generic="T extends SysDictType">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { pageModel, removeModel } from '@/apis/admin/model';
import { openDialog } from '@/utils/dialog';
import { usePage } from '@/hooks/usePage';
import { toInteger } from 'lodash';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const table = 'sys_dict_type'
const selections = ref<T[]>([])
const pageNum = useRouteQuery('page', 1, { mode: 'push', transform: toInteger })
const { page, list, loading, run } = usePage((pageNum)=>pageModel<T>(table, { pageNum }))
watchImmediate(pageNum, run)
function edit(model?: T) {
  openDialog(AppFormDialog, {
    model,
    name: '字典类型',
    table,
    onSubmit: ()=>run(pageNum.value) 
  })
}
async function remove(...row: T[]) {
  await message.confirm(`确认删除${row.map(a=>a.dictName)}吗`)
  await removeModel(table, row.map(a=>a.id))
  run(pageNum.value)
}
</script>

<style scoped lang="scss">
</style>