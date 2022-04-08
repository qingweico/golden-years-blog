<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">我的收藏</span>
      </div>
    </div>

    <div class="collect-list-wrapper" v-if="collectList.length">
    </div>
    <el-empty description="你暂时还没有收藏任何文章" v-else style=" background-color: white;"></el-empty>

  </div>

</template>

<script>
export default {
  data() {
    return {
      collectList: []
    }
  }, methods: {

  }

}
</script>

<style scoped>
.collect-list-wrapper {
  background-color: white;
  margin-top: 20px;
}
</style>