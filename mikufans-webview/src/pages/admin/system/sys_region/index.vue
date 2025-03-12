<template>
  <div class="bg0 p-4 shadow rounded-lg">
    <action-bar 
      :selections="selections" 
      :create="editRegion" 
      :update="editRegion" 
      :remove="removeRegion" 
    />
      <el-table v-loading="loading" :fit="false" style="width: 100%;" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="id" width="60" />
        <el-table-column prop="regionName" label="地区名称(中文)" width="120" />
        <el-table-column prop="regionCode" label="地区代码" width="120" />
        <el-table-column prop="langName" label="语言名称(中文)" width="120" />
        <el-table-column prop="langEnName" label="语言名称(英文)" width="120" />
        <el-table-column prop="langCode" label="语言代码(I18N)" width="120" />
        <el-table-column prop="remark" label="备注" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="200" />
        <el-table-column  label="操作" v-slot="{ row, $index }" width="200">
          <el-button icon="edit" type="primary" @click="editRegion(row)">编辑</el-button>
          <el-button icon="delete" type="danger" @click="removeRegion(row)">删除</el-button>
        </el-table-column>
      </el-table>
    <miku-pagination v-model="queries.pageNum" :page="page" />
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { createModel, pageModel, removeModel } from '@/apis/admin/model';
import { usePage } from '@/hooks/usePage';
import { openDialog } from '@/utils/dialog';
import { useRouteQueries } from '@/utils/route';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const route = useRoute()
const selections = ref<SysRegion[]>([])
const { page, run, loading } = usePage(()=>pageModel('sys_region', queries))
const { queries, reset } = useRouteQueries({
  pageNum: 1
})
watchImmediate(()=>route.query, ()=>run(1))
async function editRegion(region?: SysRegion) {
  await openDialog(AppFormDialog, {
    model: region,
    table: 'sys_region',
    title: '地区语言',
    onSubmit() {
      run(1)
    }
  })
}
async function removeRegion(...regions: SysRegion[]) {
  await message.confirm(`确定删除${ regions.map(a=>a.regionName+'/'+a.langName) }吗?`)
  await removeModel('sys_region', regions.map(a=>a.id))
  run(1)
}
</script>

<style scoped lang="scss">
.el-select, .el-input {
  width: 160px;
}
</style>