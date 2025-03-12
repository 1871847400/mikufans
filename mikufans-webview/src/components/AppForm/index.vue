<template>
  <el-form ref="formRef" :rules="formRules" :model="formData" :label-suffix="data.form.labelSuffix" :label-position="data.form.labelPosition" :label-width="data.form.labelWidth">
    <template v-for="item,property in data.formItems">
      <el-form-item :label="item.label" :required="isInScope(item.required)" :prop="property">
        <component 
          :is="map[item.type]" 
          :data="item"
          :placeholder="item.placeholder"
          :select="data.selects[property]" 
          :options="data.options[property]"
          :image="data.images[property]"
          v-model="formData[property]"
          :disabled="isInScope(item.disabled)"
          :readonly="isInScope(item.readonly)"
        />
      </el-form-item>
    </template>
  </el-form>
</template>

<script setup lang="ts">
import { FormRules } from 'element-plus';
import { isNil } from 'lodash';
const props = defineProps({
  data: object<AppForm>().isRequired,
})
const formRef = useTemplateRef('formRef')
const formData = defineModel('modelValue', { default: {} })
const currentScope : Scope = isNil(formData.value['id']) ? 'CREATE' : 'UPDATE'
function isInScope(scope: Scope) {
  return scope==='BOTH' || scope===currentScope
}
const formRules = computed(()=>{
  const rules : FormRules = {}
  for (const [property, formItem] of Object.entries(props.data.formItems)) {
    if (isInScope(formItem.required)) {
      rules[property] = [{ required: true, message: formItem.placeholder, trigger: 'blur' }]
    }
  }
  return rules
})
const map = {} as Record<FormItemType, Component>
const result = import.meta.glob('./FormItem/*.vue', { eager: true })
for (const [path, data] of Object.entries(result)) {
  //匹配组件名称 /a/b/cc.vue => cc
  const name = /([^\/\\]+)\.vue$/.exec(path)[1]
  map[name.toUpperCase() as FormItemType] = data['default']
}
defineExpose({
  formRef,
  formData
})
//填充默认值
for (const [property, formItem] of Object.entries(props.data.formItems)){
  if (!isNil(formItem.value) && isNil(formData.value[property])) {
    //如果是文本类型,才能设置空字符串
    if (formItem.value != '' || formItem.isText) {
      formData.value[property] = formItem.value
    }
  }
}
</script>

<style scoped lang="scss">
</style>