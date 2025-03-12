<template>
  <section>
    <a class="flex font-bold text-lg w-fit text-black" href="/index" target="_blank">
      <span class="mr-1">{{ title }}</span>
      <i class="iconfont icon-youjiantou"></i>
    </a>
    <div class="view-links">
      <template v-for="{ label, value } in options" :key="url">
        <a :href="`/index?${query}=${value}`" target="_blank">{{ label }}</a>
      </template>
    </div>
  </section>
</template>

<script setup lang="ts">
const props = defineProps<{
  title: string
  options: Option[]
  color: string
  query: string
}>()
</script>

<style scoped lang="scss">
section {
  border-radius: 8px;
  background-color: v-bind(color);
  padding: 16px;
  overflow: hidden;
  flex: 1 0 0;
  max-width: 25%; //防止数量少而挤满屏幕
  .view-links {
    padding: 8px 0;
    display: flex;
    flex-direction: column;
    flex-wrap: wrap;
    align-content: flex-start;
    row-gap: 10px;
    column-gap: 20px;
    color: var(--grey1);
    html.dark & {
      color: #333;
    }
    height: 50px;
    a {
      width: fit-content;
      transition: color .3s;
      &:hover {
        color: var(--blue1);
      }
    }
  }
}
</style>