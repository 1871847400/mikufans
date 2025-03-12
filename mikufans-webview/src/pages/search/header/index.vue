<template>
  <div class="search-header">
    <div class="w-[40%] z-10">
      <search-form>
        <template #prefix-icon>
          <button class="iconfont icon-24gl-search submit-ico" type="submit" tabindex="-1"></button>
        </template>
        <template #submit>
          <button class="submit" type="submit" tabindex="-1">搜索</button>
        </template>
      </search-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import SearchForm from '@/components/NavBar/search/SearchForm.vue'
</script>

<style lang="scss">
.search-header {
  display: flex;
  justify-content: center;
  padding-top: 32px;
  padding-bottom: 16px;
  .search-form {
    input.search-input:focus {
      /* border-bottom: 1px solid var(--bg2); */
    }
    .search-border {
      border-color: var(--bg2);
      .search-box {
        transition: background .3s;
        background-color: var(--bg1);
      }
      &:focus-within {
        border-color: var(--blue1);
        .search-content {
          background-color: unset;
        }
      }
      &:focus-within, &:hover {
        .search-box {
          background-color: unset;
        }
      }
    }
    button.submit {
      width: 100px;
      height: 36px;
      border-radius: 8px;
      background-color: var(--blue1);
      color: #fff;
      font-size: 16px;
      transition: all .3s;
      cursor: pointer;
      user-select: none;
      &:hover {
        background-color: var(--blue1-hover);
      }
    }
    .submit-ico {
      background: none;
      color: var(--blue1);
      font-weight: 800;
      padding-left: 4px;
      padding-right: 12px;
      cursor: pointer;
    }
  }
}
</style>