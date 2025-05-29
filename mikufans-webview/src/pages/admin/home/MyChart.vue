<template>
  <div class="bg0 rounded-lg p-4 shadow">
    <div ref="el" style="width: 100%; height: 100%;"></div>
  </div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts'
const props = defineProps<{ name: string, data: Record<string, number> }>()
const el = useTemplateRef('el')
onMounted(()=>{
  const chart = echarts.init(el.value)
  const option : echarts.EChartsOption = {
    title: {
      text: props.name + '统计',
    },
    tooltip: {},
    xAxis: {
      data: Object.keys(props.data)
    },
    yAxis: {},
    series: [{
      name: props.name,
      type: 'line',
      data: Object.values(props.data)
    }]
  }
  chart.setOption(option)
})
</script>

<style scoped lang="scss">

</style>