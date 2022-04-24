<template>
  <div class="main-page">
    <div class="title-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/center/account' }">账户设置</el-breadcrumb-item>
        <el-breadcrumb-item><a>注销账户</a></el-breadcrumb-item>
      </el-breadcrumb>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      active: 0
    };
  },

  methods: {
    next() {
      if (this.active++ > 2) this.active = 0;
    }
  }
}
</script>

<style scoped>
.main-page {
  width: 980px;
  height: 400px;
  margin-left: 20px;
  background-color: white;
  position: relative;
  padding-bottom: 40px;
}

.title-box {
  padding: 20px 0 10px 30px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

</style>