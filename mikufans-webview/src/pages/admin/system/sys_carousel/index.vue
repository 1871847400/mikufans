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
        <el-table-column label="id" prop="id" width="68"/>
        <el-table-column label="位置" prop="position" width="100"/>
        <el-table-column label="封面" width="100" v-slot="{ row }">
          <miku-image class="size-16 object-contain" preview :res-id="row.bannerId"/>
        </el-table-column>
        <el-table-column label="缩略图" width="100" v-slot="{ row }">
          <miku-image class="size-16 object-contain" preview video :res-id="row.thumbnailId"/>
        </el-table-column>
        <el-table-column label="标题/副标题" width="200" v-slot="{ row }">
          <div>
            <p class="maxline-1">{{ row.title }}</p>
            <p class="maxline-1 grey2 text-xs">{{ row.subtitle }}</p>
          </div>
      </el-table-column>
        <el-table-column label="链接" prop="url" width="150"/>
        <el-table-column label="状态" width="80" v-slot="{ row }">
          <el-tag :type="row.disabled ? 'danger':'success'">{{ row.disabled ? '禁用':'正常' }}</el-tag>
        </el-table-column>
        <el-table-column label="创建人" prop="createByName" width="100"/>
        <el-table-column label="创建时间" prop="createTime" width="180"/>
        <el-table-column label="操作" width="140">
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

<script setup lang="ts" generic="T extends SysCarousel">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { pageModel, removeModel } from '@/apis/admin/model';
import { openDialog } from '@/utils/dialog';
import { usePage } from '@/hooks/usePage';
import { toInteger } from 'lodash';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const selections = shallowRef<T[]>([])
const table = 'sys_carousel'
const pageNum = useRouteQuery('page', 1, { mode: 'push', transform: toInteger })
const { page, list, loading, run } = usePage((pageNum)=>pageModel<T>(table, { pageNum }))
watchImmediate(pageNum, run)
function edit(row?: T) {
  openDialog(AppFormDialog, { 
    model: row,
    table,
    onSubmit() {
      run(pageNum.value)
    }
  })
}
async function remove(...row: T[]) {
  await message.confirm(`确认删除${row.map(a=>a.title)}吗`)
  await removeModel(table, row.map(a=>a.id))
  run(pageNum.value)
}
</script>

<style scoped lang="scss">
</style>