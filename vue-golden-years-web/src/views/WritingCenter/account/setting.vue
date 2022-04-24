<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">账户设置</span>
      </div>
    </div>
    <div class="operator-box">
      <el-row>
        <el-col :span="12">
          <div class="operator-item">密码
          </div>
        </el-col>
        <el-col :span="12">
          <div class="operator">
            <router-link to="/center/account/password">
              <a href="javascript:void(0);">修改密码</a>
            </router-link>
          </div>
        </el-col>
      </el-row>
      <el-divider></el-divider>
      <el-row>
        <el-col :span="12">
          <div class="operator-item">手机</div>
        </el-col>
        <el-col :span="12">
          <div class="operator">
            <router-link to="/center/account/cellphone">
              <a href="javascript:void(0);">手机号变更</a>
            </router-link>
          </div>
        </el-col>
      </el-row>
      <el-divider></el-divider>
      <el-row>
        <el-col :span="12">
          <div class="operator-item">邮箱</div>
        </el-col>
        <el-col :span="12">
          <div class="operator">修改邮箱</div>
        </el-col>
      </el-row>
      <el-divider></el-divider>
      <el-row>
        <el-col :span="12">
          <div class="operator-item">三方账号</div>
        </el-col>
        <el-col :span="12">
          <div class="operator">绑定/解绑</div>
        </el-col>
      </el-row>
      <el-divider></el-divider>
      <el-row>
        <el-col :span="12">
          <div class="operator-item">登录记录</div>
        </el-col>
        <el-col :span="12">
          <div class="operator">
            <router-link to="/center/loginlog">
              <a href="javascript:void(0);">查看记录</a>
            </router-link>
          </div>
        </el-col>
      </el-row>
      <el-divider></el-divider>
      <el-row>
        <el-col :span="12">
          <div class="operator-item">账号注销</div>
        </el-col>
        <el-col :span="12">
          <div class="operator">
          </div>
          <div class="operator">  <router-link to="/center/account/logout">
            <a href="javascript:void(0);">立即注销</a>
          </router-link></div>
        </el-col>
      </el-row>
      <el-divider></el-divider>
    </div>
  </div>
</template>

<script>
export default {
  name: "setting"
}
</script>

<style scoped>
.main-page {
  width: 980px;
  margin-left: 20px;
  background-color: white;
}

.title-box {
  padding: 20px 0 10px 30px;
  border-bottom: 1px solid #e8e8e8;
}

.title-word {
  color: #920784;
  font-size: 20px;
}

.operator-box {
  padding: 20px 0 0 0;
  min-height: 200px;
  background: #fff;
  font-size: 20px;
}

.operator-item {
  margin-left: 20px;
}

.operator {
  float: right;
  margin-right: 20px;
  cursor: pointer;
}

.operator:hover {
  color: #920784;
}
</style>