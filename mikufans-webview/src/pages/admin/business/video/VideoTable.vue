<template>
  <div class="bg0 p-4 rounded-lg shadow">
    <action-bar 
      :selections="selections" 
      :update="editVideo"
      :remove="removeVideo" 
    />
    <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row show-overflow-tooltip>
      <el-table-column type="selection" label="#" width="40"/>
      <el-table-column prop="uid" label="#" width="80"/>
      <el-table-column prop="id" label="id" width="200" />
      <el-table-column prop="sid" label="sid" width="90" />
      <el-table-column prop="title" label="视频标题" width="240" />
      <el-table-column prop="title" label="视频封面" width="90">
        <template v-slot="{ row }">
          <miku-image :res-id="row.bannerId" video class="w-16" preview />
        </template>
      </el-table-column>
      <el-table-column prop="user.nickname" label="上传用户" width="120" />
      <el-table-column prop="publishTime" label="发布时间" width="180" />
      <el-table-column prop="remark" label="备注" width="100" />
      <el-table-column  label="操作" v-slot="{ row, $index }" width="130">
        <el-button icon="edit" type="primary" @click="editVideo(row)" title="编辑"/>
        <el-button icon="delete" type="danger" @click="removeVideo(row)" title="删除"/>
      </el-table-column>
    </el-table>
    <miku-pagination v-model="pageNum" :page="page" />
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import MikuPagination from '@/components/MikuPagination/index.vue';
import { pageModel, removeModel } from '@/apis/admin/model';
import { usePage } from '@/hooks/usePage';
import { openDialog } from '@/utils/dialog'; 
import VideoDialog from './VideoDialog.vue';
const route = useRoute()
const { page, loading, run } = usePage(()=>pageModel('video', route.query))
const pageNum = useRouteQuery<number>('pageNum', 1)
const selections = ref<Video[]>([])
async function removeVideo(...videos: Video[]) {
  await message.confirm(`确认删除${videos.map(a=>a.title)}吗?`)
  await removeModel('video', videos.map(a=>a.id))
  run(1)
}
function editVideo(video: Video) {
  openDialog(VideoDialog, { 
    video,
    onSubmit() {
      run(1)
    }
  })
}
watch(()=>route.query, ()=>run(1), { deep: true, immediate: true })
</script>

<style scoped lang="scss">
</style>