<template>
  <el-dialog v-model="dialog" width="600px" title="视频编辑" center>
    <el-form label-position="right" label-suffix=":" label-width="100px">
      <el-tabs type="border-card">
        <el-tab-pane label="视频">
          <el-form-item label="视频标题">
            <el-input v-model="form.title" placeholder="请输入视频标题" />
          </el-form-item>
          <el-form-item label="视频介绍">
            <el-input v-model="form.intro" placeholder="请输入视频简介" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="视频封面">
            <div @click="selectBanner" class="cursor-pointer">
              <miku-image :res-id="form.bannerId" video class="w-[200px]" />
            </div>
          </el-form-item>
          <el-form-item label="搜索类型">
            <el-select
              clearable
              v-model.trim="form.search"
              placeholder="请选择搜索类型"
            >
              <el-option label="可搜索" :value="1" />
              <el-option label="不可搜索" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="搜索类型">
            <channel-select v-model="form.channelId" />
          </el-form-item>
          <!-- <el-form-item label="可见性">
            <el-select v-model="form.dynamic.visible">
              <el-option label="公开可见" :value="1" />
              <el-option label="仅自己可见" :value="0" />
            </el-select>
          </el-form-item> -->
          <el-form-item label="标签">
            <input-tag class="flex-1" v-model="form.tags" :max="10"/>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model.trim="form.remark" type="textarea" :rows="3" placeholder="请输入备注" :maxlength="64" />
          </el-form-item>
        </el-tab-pane>
        <el-tab-pane label="节目" v-if="ready">
          <el-form-item label="海报图">
            <div @click="selectPoster" class="cursor-pointer">
              <miku-image :res-id="bangumi.posterId" poster class="w-[120px]" />
            </div>
          </el-form-item>
        </el-tab-pane>
      </el-tabs>
    </el-form>
    <template #footer>
      <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      <el-button type="default" @click="dialog=false">取消</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { listModel, updateModel } from '@/apis/admin/model'
import { uploadImage } from '@/apis/image';
import { useReactive } from '@/hooks/useReactive';
import { openFileSelect } from '@/utils/dialog';
import message from '@/utils/message/message'
const dialog = ref(true)
const { video } = defineProps<{ video: Video }>()
const emits = defineEmits<{ submit: [] }>()
const form = reactive({
  ...video,
})
const { data: bangumi, hasChanged, ready } = useReactive(async ()=>{
  if (video.bangumiId && video.bangumiId != '0') {
    const models = await listModel<Bangumi>('bangumi', { id: video.bangumiId })
    return models[0]
  }
})
const submitting = ref(false)
async function submit() {
  try {
    submitting.value = true
    await updateModel('video', form)
    if (hasChanged.value) {
      await updateModel('bangumi', bangumi)
    }
    dialog.value = false
    emits('submit')
    message.success('更新成功')
  } finally {
    submitting.value = false
  }
}
function selectBanner() {
  openFileSelect({
    accept: 'image/*',
    async callback(file) {
      form.bannerId = await uploadImage(file)
    },
  })
}
function selectPoster() {
  openFileSelect({
    accept: 'image/*',
    async callback(file) {
      bangumi.posterId = await uploadImage(file)
    },
  })
}
</script>

<style scoped lang="scss">
.el-select {
  width: 160px;
}
</style>