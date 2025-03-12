<template>
  <!-- <div class="pt-2 pb-2" v-if="bangumi">
    <el-segmented v-model="showPartType" :options="partTypes">
      <template #default="{ item }">
        <div class="flex flex-col items-center gap-2 p-3">
          <div>{{ item['label'] }}</div>
        </div>
      </template>
    </el-segmented>
  </div> -->
</template>

<script setup lang="ts">
const videoStore = useVideoStore()

</script>

<style scoped lang="scss">
.el-segmented {
  --el-segmented-padding: 0;
  html.dark & {
    --el-fill-color-light: transparent;
  }
  --el-fill-color-light: #fff;
}
</style>