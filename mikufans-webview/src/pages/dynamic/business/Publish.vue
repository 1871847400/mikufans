<template>
  <div v-if="publish">
    <div class="font-bold mb-2" v-if="publish.title">{{ publish.title }}</div>
    <rich-text class="text-sm mb-3" :rows="3" :content="publish.content"/>
    <image-list :img-ids="publish.imgIds"/>
  </div>
</template>

<script setup lang="ts">
import ImageList from '@/components/CommentArea/ImageList.vue';
const props = defineProps<{ data: UserDynamic }>()
const publish = toRef(()=>props.data.source as UserPublish)
</script>