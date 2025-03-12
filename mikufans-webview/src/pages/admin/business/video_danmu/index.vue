<template>
  <div>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :create="open"
        :update="open"
        :remove="remove"
      >
        <template #append>
          <el-button icon="TrendCharts" type="warning" @click="openImport">导入</el-button>
        </template>
      </action-bar>
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="视频id" prop="videoId" width="120"/>
        <el-table-column label="分集id" prop="partId" width="120"/>
        <el-table-column label="内容" prop="content" width="300"/>
        <el-table-column label="颜色" prop="fontColor" width="80"/>
        <el-table-column label="字体类型" prop="fontType" width="80"/>
        <el-table-column label="显示时间" prop="sendTime" width="80"/>
        <el-table-column label="弹幕类型" prop="danmuType" width="100"/>
        <el-table-column label="发送者" prop="user.nickname" width="100"/>
        <el-table-column label="操作" width="300" v-slot="{ row }">
          <el-button icon="edit" type="success" @click="open(row)"/>
          <el-button icon="delete" type="danger" @click="remove(row)"/>
        </el-table-column>
      </el-table>
      <miku-pagination v-model="pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts" generic="T extends VideoDanmu">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { openDialog } from '@/utils/dialog';
import { usePage } from '@/hooks/usePage';
import { toInteger } from 'lodash';
import { pageModel, removeModel } from '@/apis/admin/model';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
import { importDanmus } from '@/apis/admin/video';
const selections = ref<T[]>([])
const table = 'video_danmu'
const pageNum = useRouteQuery('pageNum', 1, { mode: 'push', transform: toInteger })
const { page, loading, run } = usePage((pageNum)=>pageModel(table, { pageNum }))
function open(model?: T) {
  openDialog(AppFormDialog, {
    table,
    model,
    onSubmit() {
      run(1)
    }
  })
}
function openImport() {
  openDialog(AppFormDialog, {
    table: 'danmu_import_dto',
    async create(formData) {
      const data = await importDanmus(formData as any)
      message.success(data)
    }
  })
}
async function remove(...rows: T[]) {
  await message.confirm(`确认删除${rows.map(a=>a.content)}吗`)
  await removeModel(table, rows.map(a=>a.id))
  run(pageNum.value)
}
watchImmediate(pageNum, run)
</script>

<style scoped lang="scss">
</style>