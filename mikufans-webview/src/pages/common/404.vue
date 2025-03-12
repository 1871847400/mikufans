<template>
  <div class="nodata">
    <nav-bar/>
    <span>404！页面走丢了？</span>
  </div>
</template>

<style scoped lang="scss">
.nodata {
  background-image: url('@/assets/images/nodata2.png');
  background-position: center;
  background-repeat: no-repeat;
  background-size: 500px;
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  span {
    margin: 0 auto;
    margin-top: 280px;
    font-size: 25px;
  }
}
</style>