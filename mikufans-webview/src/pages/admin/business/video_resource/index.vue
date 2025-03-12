<template>
  <div>
    <el-form class="search-bar shadow" label-suffix=":" @submit.prevent="commit">
      <el-form-item label="id">
        <el-input
          clearable
          v-model.trim="queries.id"
          placeholder="请输入资源id"
          @keyup.enter="commit"
        />
      </el-form-item>
      <el-form-item label="md5">
        <el-input
          clearable
          v-model.trim="queries.md5"
          placeholder="请输入资源md5"
          @keyup.enter="commit"
        />
      </el-form-item>
      <el-form-item>
        <el-button icon="search" native-type="submit" type="primary">搜索</el-button>
      </el-form-item>
      <el-form-item>
        <el-button icon="refresh" @click="reset(true)">重置</el-button>
      </el-form-item>
    </el-form>
    <div class="bg0 p-4 rounded-lg shadow" >
      <action-bar 
        :selections="selections"
        :remove="removeResource"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="ID" prop="id" width="110"/>
        <el-table-column label="MD5" prop="md5" width="100"/>
        <el-table-column label="时长" prop="durationFormat" width="100"/>
        <el-table-column label="可用性" width="100" v-slot="{ row }">
          <el-tag :type="row.pending ? 'danger' : 'success'">{{ row.pending ? '不可用' : '可用' }}</el-tag>
        </el-table-column>
        <el-table-column label="本地路径" prop="localPath" width="200"/>
        <el-table-column label="转储模式" prop="transferMode" width="100"/>
        <el-table-column label="转储路径" prop="transferPath" width="200"/>
        <el-table-column label="媒体类型" prop="mediaType" width="120"/>
        <el-table-column label="上传人" prop="user.nickname" width="100"/>
        <el-table-column label="操作" v-slot="{ row }">
          <el-button icon="delete" type="danger" @click="removeResource(row)"/>
        </el-table-column>
      </el-table>
      <miku-pagination v-model="queries.pageNum" :page="page" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ActionBar from '@/pages/admin/actionbar/index.vue'
import { pageModel, removeModel } from '@/apis/admin/model';
import { usePage } from '@/hooks/usePage';
import { useRouteQueries } from '@/utils/route';
import { displayDuration } from '@/utils/common';
const selections = ref<VideoResource[]>([])
const route = useRoute()
const { queries, commit, reset } = useRouteQueries({
  id: null,
  md5: null,
  pageNum: 1
}, { autoCommit: ['pageNum'] })
const { page, loading, run } = usePage(()=>pageModel('video_resource', queries))
watch(()=>route.query, ()=>run(queries.pageNum), { immediate: true, deep: true })
async function removeResource(...resource: VideoResource[]) {
  await message.confirm(`确认删除${resource.map(a=>a.id)}吗`)
  await removeModel('video_resource', resource.map(a=>a.id))
  run(queries.pageNum)
}
</script>

<style scoped lang="scss">
.el-input {
  width: 280px;
}
</style>