<template>
  <div class="profile-section">
    <el-form 
      label-width="auto" 
      label-suffix=":" 
      :disabled="!userStore.isSelf(data.id)" 
      @submit.prevent="updateUser"
    >
      <el-form-item label="昵称" required>
        <el-input v-model="data.nickname" maxlength="20" show-word-limit/>
      </el-form-item>
      <el-form-item label="账号">
        <span>{{ data.username }}</span>
      </el-form-item>
      <el-form-item label="签名">
        <el-input 
          v-model="data.sign" 
          placeholder="在这里输入个性签名..." 
          type="textarea" 
          :rows="3" 
          maxlength="60" 
          show-word-limit 
          resize="none"
        />
      </el-form-item>
      <el-form-item label="性别" required>
        <el-radio-group v-model="data.gender">
          <el-radio-button :value="1">男</el-radio-button>
          <el-radio-button :value="2">女</el-radio-button>
          <el-radio-button :value="0">保密</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="出生日期">
        <el-date-picker
          v-model="data.birthday"
          type="date"
          placeholder="选择日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <template v-if="userStore.isSelf(data.id)">
        <el-divider/>
        <el-button class="save-btn" type="primary" native-type="submit">保存</el-button>
      </template>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import userApi from '@/apis/user';
import { cloneDeep } from 'lodash';
const userStore = useUserStore()
const { isSelf, user } = toRefs(useSpaceStore())
const data = reactive(cloneDeep(userStore.$state))
async function updateUser() {
  const dto : UserDto = {
    nickname: data.nickname,
    birthday: data.birthday,
    gender: data.gender,
    sign: data.sign,
  }
  await userApi.update({
    ...dto,
    id: userStore.id,
  })
  await userStore.getUserInfo(true)
  if (isSelf.value) {
    Object.assign(user.value, dto)
  }
  message.success('更新信息成功！')
}
</script>

<style scoped lang="scss">
.profile-section {
}
.el-form {
  width: 550px;
  margin: 0 auto;
}
.save-btn {
  font-size: 16px;
  width: 100px;
  height: 40px;
  margin-left: 50%;
  transform: translateX(-50%);
}
</style>