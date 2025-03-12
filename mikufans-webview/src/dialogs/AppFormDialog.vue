<template>
  <el-dialog v-model="dialog" :title="dialogTitle" :width="width" center>
    <app-form ref="formRef" v-if="isReady" v-model="formData" :data="state"></app-form>
    <template #footer>
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button type="" @click="dialog=false">取消</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { getAppForm } from '@/apis/admin/form';
import { createModel, updateModel } from '@/apis/admin/model'
const dialog = ref(true)
const { model, table, name, title, create, update } = defineProps({
  model: object(), //默认数据,不一定代表更新
  table: string().isRequired,
  name: string().def(''),
  title: string(),
  width: string().def('600px'),
  create: func<(formData: object)=>Promise<any>>(), //自定义创建方法
  update: func<(formData: object)=>Promise<any>>(), //自定义更新方法
})
const emits = defineEmits<{ submit: [] }>()
const isUpdate = computed(()=>!!model?.id)
const dialogTitle = computed(()=>{
  return title || (isUpdate.value?'编辑':'新增')+(name || state.value?.form?.name || '')
})
const formRef = useTemplateRef('formRef')
const formData = reactive({ ...model })
const { state, isReady } = useAsyncState(getAppForm(table), null)
async function submit() {
  if (!isReady.value) {
    return
  }
  await formRef.value.formRef.validate()
  if (isUpdate.value) {
    //定义了@FormItem以及发生了变化的属性才更新
    //避免更新多余数据或则多人同时修改造成覆盖
    const updateData = { id: model.id }
    for (const [key, item] of Object.entries(state.value.formItems)) {
      if (formData[key] !== model[key]) {
        updateData[key] = formData[key]
      }
    }
    if (update) {
      await update(updateData)
    } else {
      await updateModel(table, updateData)
    }
    message.success('更新成功')
  } else {
    if (create) {
      await create(formData)
    } else {
      await createModel(table, formData)
    }
    message.success('创建成功')
  }
  dialog.value = false
  emits('submit')
}
</script>

<style scoped lang="scss">
</style>