<template>
  <div v-infinite-scroll="next" :infinite-scroll-disabled="loading" class="p-6">
    <div v-if="isSelf">
      <el-button type="primary" @click="appendImage">添加图片</el-button>
    </div>
    <div ref="container" class="waterfall">
      <template v-for="i,k in list" :key="i.id">
        <waterfall-cell 
          :index="k+1" 
          :album="i" 
          @load="updatePositions" 
          @remove="remove"
        />
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import albumApi from '@/apis/user/album';
import { usePage } from '@/hooks/usePage';
import { openFileSelect } from '@/utils/dialog';
import { uploadImage } from '@/apis/image';
import { useSortable } from '@vueuse/integrations/useSortable';
import WaterfallCell from './WaterfallCell.vue';
import { useWaterfall } from '@/hooks/useWaterfall';
const { userId, isSelf } = toRefs(useSpaceStore())
const container = ref<HTMLDivElement>()
const { updatePositions } = useWaterfall(container, {
  columns: computed(()=>5),
  gap: 10,
})
const { list, next, loading } = usePage(search, {
  immediate: [1],
  compare(a, b) {
    return a.id === b.id
  },
})

// useSortable(container, list, {
//   onEnd() {
//     albumApi.changeOrder({
//       ids: imageList.value.map(a=>a.id)
//     })
//     updatePositions()
//   },
// })
function search(pageNum: number) {
  return albumApi.search({
    pid: '0',
    userId: userId.value,
    pageNum,
    pageSize: 20,
  })
}
function appendImage() {
  openFileSelect({
    accept: 'image/*',
    multiple: true,
    async callback(file) {
      const imgId = await uploadImage(file)
      const data = await albumApi.create({
        category: 1,
        title: '',
        imgId,
      })
      list.value = [data, ...list.value]
    },
  })
}

async function remove(data: UserAlbum) {
  await message.confirm('您确定要删除该图片吗?')
  await albumApi.remove(data.id)
  message.success('删除成功')
  list.value = list.value.filter(a=>a.id!==data.id)
  nextTick(updatePositions)
}
function blur() {
  if (document.activeElement instanceof HTMLInputElement) {
    document.activeElement.blur()
  }
}
async function update(data: UserAlbum) {
}
// const previewList = computed(()=>imageList.value.map(a=>a.src))
</script>

<style scoped lang="scss">
.waterfall {
  position: relative;
  margin: 32px 0;
}
</style>