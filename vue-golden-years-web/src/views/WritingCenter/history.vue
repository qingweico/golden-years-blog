<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">浏览历史</span>
      </div>
    </div>
    <div class="history-list-wrapper" v-if="historyList.length">
    </div>
    <el-empty description="你暂时还没有阅读任何文章, 快去找一篇吧" v-else style=" background-color: white;"></el-empty>
  </div>
</template>

<script>
export default {
  data() {
    return {
      historyList: []
    }
  }, methods: {}
}
</script>

<style scoped>
.history-list-wrapper {
  background-color: white;
  margin-top: 20px;
}
</style>