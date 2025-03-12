<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="editParam"
        :update="editParam"
        :remove="removeParam"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="id" prop="id" width="80"/>
        <el-table-column label="参数键" prop="paramKey" width="240"/>
        <el-table-column label="参数值" prop="paramValue" width="300"/>
        <el-table-column label="备注" prop="remark" width="200"/>
        <el-table-column label="创建人" prop="createByName" width="100"/>
        <el-table-column label="创建时间" prop="createTime" width="180"/>
        <el-table-column label="操作" width="150">
          <template v-slot="{ row }">
            <el-button icon="edit" type="success" @click="editParam(row)"/>
            <el-button icon="delete" type="danger" @click="removeParam(row)"/>
          </template>
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
const selections = ref<SysParam[]>([])
const route = useRoute()
const { queries, commit, reset } = useRouteQueries({
  pageNum: 1
}, { autoCommit: ['pageNum'] })
const { page, loading, run } = usePage(()=>pageModel('sys_param', queries))
watch(()=>route.query, ()=>run(queries.pageNum), { immediate: true, deep: true })
async function editParam(sysParam?: SysParam) {
  await openDialog(AppFormDialog, { 
    model: sysParam,
    name: '系统参数',
    table: 'sys_param',
    onSubmit: ()=>run(queries.pageNum) 
  })
}
async function removeParam(...sysParam: SysParam[]) {
  await message.confirm(`确认删除${sysParam.map(a=>a.paramKey)}吗`)
  await removeModel('sys_param', sysParam.map(a=>a.id))
  run(queries.pageNum)
}
</script>

<style scoped lang="scss">
</style>