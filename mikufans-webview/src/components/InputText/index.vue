<template>
  <el-input v-if="input" ref="el" v-model="model" autofocus @blur="input=false" @keydown.enter="input=false" @keydown.esc="cancel"/>
  <div v-else class="cursor-pointer" @click="input=true">{{ model }}</div>
</template>

<script setup lang="ts">
const model = defineModel({ default: '' })
let oldModel = model.value
const input = ref(false)
const el = ref<HTMLInputElement>()
const emits = defineEmits<{ change: [val: string] }>()
watch(input, ()=>{
  if (input.value) {
    nextTick(()=>{
      el.value.focus()
    })
  } else {
    if (model.value=='') {
      model.value = oldModel
    } else {
      if (oldModel !== model.value) {
        emits('change', model.value)
      }
      oldModel = model.value
    }
  }
})
function cancel() {
  if (oldModel) {
    model.value = oldModel
  }
  input.value = false
}
</script>

<style scoped lang="scss">

</style>