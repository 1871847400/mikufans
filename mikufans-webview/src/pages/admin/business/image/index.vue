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
        :create="createImage"
        :update="editImage"
        :remove="removeImage"
      />
      <el-table v-loading="loading" :fit="false" :data="page?.records" @selection-change="selections=$event" stripe highlight-current-row>
        <el-table-column type="selection" width="50"/>
        <el-table-column label="id" prop="id" width="120"/>
        <el-table-column label="md5" prop="md5" width="200"/>
        <el-table-column label="本地位置" prop="localPath" width="340"/>
        <el-table-column label="原始类型" prop="mediaType" width="120"/>
        <el-table-column label="预览" width="100" v-slot="{ row }">
          <miku-image class="size-16 object-cover" :res-id="row.id" preview />
        </el-table-column>
        <el-table-column label="主色调" width="100" v-slot="{ row }">
          <el-color-picker v-model="row.mainColor" size="large" disabled />
        </el-table-column>
        <el-table-column prop="user.nickname" label="上传用户" width="120" />
        <el-table-column label="操作" width="150">
          <template v-slot="{ row }">
            <el-button icon="edit" type="success" @click="editImage(row)"/>
            <el-button icon="delete" type="danger" @click="removeImage(row)"/>
          </template>
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
import { openDialog, openFileSelect } from '@/utils/dialog';
import ImageCropDialog from '@/components/ImageCropDialog/index.vue';
import { uploadImage } from '@/apis/image';
import AppFormDialog from '@/dialogs/AppFormDialog.vue';
const selections = ref<ImageResource[]>([])
const route = useRoute()
const { queries, commit, reset } = useRouteQueries({
  id: null,
  md5: null,
  pageNum: 1
}, { autoCommit: ['pageNum'] })
const { page, loading, run } = usePage(()=>pageModel('image_resource', queries))
watch(()=>route.query, ()=>run(queries.pageNum), { immediate: true, deep: true })
function createImage() {
  openFileSelect({
    accept: 'image/*',
    callback(file) {
      openDialog(ImageCropDialog, {
        src: file,
        async onSubmit(_file) {
          await uploadImage(_file);
          message.success('上传成功');
          run(queries.pageNum);
        },
      })
    },
  })
}
function editImage(row: ImageResource) {
  openDialog(AppFormDialog, {
    table: 'image_resource',
    model: row,
    onSubmit() {
      run(queries.pageNum)
    }
  })
}
async function removeImage(...image: ImageResource[]) {
  await message.confirm(`确认删除${image.map(a=>a.id)}吗`)
  await removeModel('image_resource', image.map(a=>a.id))
  run(queries.pageNum)
}
</script>

<style scoped lang="scss">
.el-input {
  width: 280px;
}
</style>