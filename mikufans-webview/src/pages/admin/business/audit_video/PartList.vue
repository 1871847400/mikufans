<template>
  <section class="part-section">
    <ul class="part-list">
      <template v-for="i,j in list" :key="i.id">
        <li class="part-item" :data-active="playIndex===j" :data-audit="i.sysAudit.auditStatus" @click="playIndex=j">
          <miku-image :res-id="i.bannerId" video class="w-[68px]" />
          <div>{{ i.partName }}</div>
          <div class="sort">{{ i.sort }}</div>
        </li>
      </template>
    </ul>
  </section>
</template>

<script setup lang="ts">
const props = defineProps<{ list: VideoPart[] }>()
const playIndex = defineModel<number>()
</script>

<style scoped lang="scss">
.part-section {
  /* background-color: var(--bg2); */
  /* padding: 12px; */
  border-radius: 4px;
  width: 100%;
}
.part-list {
  display: flex;
  flex-direction: column;
  max-height: 200px;
  overflow-y: auto;
  gap: 10px;
  .part-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
    padding-left: 20px;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    position: relative;
    &:hover {
      background-color: var(--bg3);
    }
    &[data-active='true'] {
      background-color: var(--bg1);
    }
    &:not([data-audit='UNKNOWN'])::before {
      content: '已审核';
      color: rgb(161, 232, 55);
      font-weight: 550;
      position: absolute;
      top: 50%;
      right: 10px;
      transform: translateY(-50%);
      z-index: 1;
    }
    .sort {
      position: absolute;
      left: 0;
      top: 0;
      padding: 0px 4px;
      font-size: 12px;
      line-height: 2;
      color: #fff;
      background-color: #f97d7d;
    }
  }
}
</style>