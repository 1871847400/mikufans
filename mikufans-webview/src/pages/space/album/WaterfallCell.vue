<template>
  <div class="waterfall-cell" v-visible="isLoad" :index="index">
    <miku-image :res-id="album.imgId" class="w-full" @load="onLoad" preview />
    <!-- <form v-if="addRemarkForm.id === i.id" class="px-1" @submit.prevent="blur">
      <input
        class="w-full"
        v-focus
        type="text"
        v-model.trim="addRemarkForm.remark"
        placeholder="请在这里输入描述..."
        @blur="update(i)"
        maxlength="16"
      />
    </form>
    <div v-else-if="i.remark" class="grey1 text-sm p-1 italic maxline-1">
      "{{ i.remark }}"
    </div> -->
    <!-- <div
      class="grey2 text-sm flex items-center justify-between p-1"
      draggable="false"
    >
      <span
        class="flex items-center gap-1 cursor-pointer"
        :class="{ blue0: favorValue === 1 }"
        @click="giveFavor"
      >
        <i class="iconfont icon-dianzan_kuai"></i>
        <span>{{ displayNumber(i.favor) }}</span>
      </span>
      <span
        v-if="!addRemarkForm.id && isSelf"
        class="add-remark hidden cursor-pointer"
        @click="addRemark(i)"
        >{{ i.remark ? "修改描述" : "添加描述" }}</span
      >
      <span>{{ album.createDate }}</span>
    </div> -->
    <i
      v-if="isSelf"
      class="iconfont icon-cuowu absolute grey2 cursor-pointer right-1 top-1 hidden"
      title="删除"
      @click="emits('remove', album)"
    ></i>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{ album: UserAlbum, index: number }>()
const emits = defineEmits<{ remove: [album: UserAlbum], load: [] }>()
const { isSelf } = toRefs(useSpaceStore())
// const { giveFavor } = useFavor('ALBUM', 
//   props.album.id, 
//   toRef(props.album, 'favor'), 
//   null, 
//   toRef(props.album, 'userFavor')
// )
//等到图片加载完成后才显示,以免无法确定图片比例
const isLoad = ref(false)
function onLoad() {
  isLoad.value = true
  emits('load')
}
const previewList = []
</script>

<style scoped lang="scss">
.waterfall-cell {
  border: 1px solid #ccc;
  border-radius: 6px;
  /* transition: all .2s; */
  position: relative;
  &::before {
    content: attr(index);
    position: absolute;
    top: 4px;
    left: 4px;
    color: #eee;
  }
  &:hover {
    .icon-cuowu {
      display: block;
    }
  }
  input::placeholder {
    font-size: 12px;
  }
}
</style>