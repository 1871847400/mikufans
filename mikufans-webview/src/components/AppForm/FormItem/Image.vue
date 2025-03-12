<template>
  <div @click="selectImage" class="cursor-pointer">
    <miku-image 
      v-if="modelValue&&modelValue!=='0'" 
      :res-id="modelValue" 
      draggable="false"
    />
    <el-button v-else>上传图片</el-button>
  </div>
</template>

<script setup lang="ts">
import { uploadImage } from '@/apis/image';
import { openDialog, openFileSelect } from '@/utils/dialog';
import ImageCropDialog from '@/components/ImageCropDialog/index.vue';
const props = defineProps<{ data: FormItem, image: FormImage }>()
const modelValue = defineModel<string>()
function selectImage() {
  openFileSelect({
    accept: 'image/*',
    callback(file) {
      openDialog(ImageCropDialog, {
        src: file,
        async onSubmit(cropFile) {
          modelValue.value = await uploadImage(cropFile)
        }
      })
    },
  })
}
</script>

<style scoped lang="scss">
.miku-image {
  width: v-bind('image?.width');
  height: v-bind('image?.height');
  border-radius: v-bind('image?.radius');
}
</style>