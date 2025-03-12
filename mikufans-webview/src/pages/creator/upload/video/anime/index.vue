<template>
  <div>
    <el-form-item label="海报" required>
      <clickable @click="imageSelect">
        <div>
          <img v-if="imgUrl" class="aspect-[3/4] w-[150px]" :src="imgUrl" alt="">
          <miku-image v-else-if="bangumi.posterId" :res-id="bangumi.posterId" class="w-[150px]" poster />
          <el-button v-else>选择文件</el-button>
        </div>
      </clickable>
    </el-form-item>
    <template v-if="videoType=='ANIME'">
      <el-form-item label="更新状态" required>
        <el-radio-group v-model="bangumi.releaseStatus">
          <el-radio :value="1">更新中</el-radio>
          <el-radio :value="2">已完结</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="更新日期">
        <el-radio-group v-model="bangumi.releaseWeek" :disabled="bangumi.releaseStatus!=1">
          <el-radio v-for="day,index in DayOfWeek" :key="day" :label="day" :value="index+1"/>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="更新时间">
        <el-time-picker 
          v-model="bangumi.releaseTime" 
          placeholder="选择更新时间" 
          format="HH:mm" 
          value-format="HH:mm" 
          :disabled="bangumi.releaseStatus!=1" 
        />
      </el-form-item>
      <el-form-item label="所属季度">
        <el-select 
          v-model="bangumi.releaseSeason" 
          placeholder="请选择季度" 
          style="width: 240px;" 
          :empty-values="[0, undefined]"
          :value-on-clear="0"
        >
          <el-option label="1月-冬季番" :value="1" />
          <el-option label="4月-春季番" :value="2" />
          <el-option label="7月-夏季番" :value="3" />
          <el-option label="10月-秋季番" :value="4" />
        </el-select>
      </el-form-item>
    </template>
    <el-form-item :label="videoType==='ANIME'?'首播日期':'上映日期'">
      <el-date-picker
        v-model="bangumi.premiere"
        type="date"
        value-format="YYYY-MM-DD"
        :placeholder="videoType==='ANIME'?'请选择首播日期':'请选择上映日期'"
      />
    </el-form-item>
    <el-form-item label="官方情报">
      <el-input
        v-model="bangumi.official"
        style="width: 320px"
        :rows="5"
        resize="both"
        type="textarea"
        :maxlength="512"
        :placeholder="officialPlaceholder"
      />
    </el-form-item>
    <el-form-item label="Staff列表">
      <el-input
        v-model="bangumi.staff"
        style="width: 320px"
        :rows="5"
        resize="both"
        type="textarea"
        :maxlength="512"
        :placeholder="staffPlaceholder"
      />
    </el-form-item>
    <el-form-item :label="videoType=='ANIME'?'声优列表':'配音列表'">
      <el-input
        v-model="bangumi.voice"
        style="width: 320px"
        :rows="5"
        resize="both"
        type="textarea"
        :maxlength="512"
        :placeholder="voicePlaceholder"
      />
    </el-form-item>
    <el-form-item label="风格列表">
      <el-checkbox-group v-model="bangumi.styles" :max="10">
        <template v-for="style in styles" :key="style.id">
          <el-checkbox :label="style.styleName" :value="style.id"/>
        </template>
      </el-checkbox-group>
    </el-form-item>
    <el-form-item label="系列标签">
      <el-input v-model="bangumi.seriesTag" placeholder="例如:第一季,第一部" :maxlength="32" style="width: 450px;" />
    </el-form-item>
    <el-form-item label="发行地区" required>
      <el-select 
        v-model="bangumi.regionId" 
        placeholder="请选择发行地区"
        style="width: 240px;"
        :empty-values="['0', undefined]"
      >
        <template v-for="i in regions" :key="i.id">
          <el-option :label="i.regionName" :value="i.id" />
        </template>
      </el-select>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { listRegions } from '@/apis/system';
import { DayOfWeek } from '@/utils/datetime';
import { openFileSelect } from '@/utils/dialog';
import bangumiStyleApi from '@/apis/bangumi/style';
const { poster, data, videoType } = toRefs(useUploadStore())
const bangumi = toRef(data.value, 'bangumi')
const imgUrl = useObjectUrl(poster)
function imageSelect() {
  openFileSelect({
    accept: 'image/*',
    callback(file) {
      poster.value = file
    },
  })
}
const { state: regions } = useAsyncState(listRegions, [])
const styles = computedAsync<BangumiStyle[]>(()=>bangumiStyleApi.listStyles(videoType.value), [])
const officialPlaceholder = `\
\原版名称: XXX
\官方网站: XXX
`
const staffPlaceholder = `\
\原作：XXXXX
\导演：XXXXX
\副导演：XXXXX
`
const voicePlaceholder = `\
\卡尔斯特：XXXXX
\希尔薇娅：XXXXX
\米拉莉斯：XXXXX
`
</script>

<style scoped lang="scss">
</style>