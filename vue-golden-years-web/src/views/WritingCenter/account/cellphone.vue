<template>
  <div class="main-page">
    <div class="title-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/center/account' }">账户设置</el-breadcrumb-item>
        <el-breadcrumb-item><a>手机号变更</a></el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-form ref="form" :model="form" :rules="rules" label-width="80px" style="width: 40%;">
      <el-form-item>
        验证码将发送到手机177****6221
      </el-form-item>
      <el-form-item label="验证码" prop="smsCode">
        <el-input v-model="form.smsCode" placeholder="6位手机验证码"
                  oninput="value = value.replace(/[^\d]/g,'')"
                  maxlength="6"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" @click="sendSmsCode">发送手机验证码</el-button>
        <el-button type="primary" size="mini" @click="submit" :disabled="form.smsCode === ''">下一步</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        smsCode: ""
      },
      // 表单校验
      rules: {
        smsCode: [
          {required: true, message: "请输入验证码", trigger: "blur"},
        ]
      }
    }
  },
  methods: {
    sendSmsCode() {
    },
    submit() {
    },
    close() {
      this.$router.push("/center/cellphone");
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

.el-breadcrumb-item:hover {
  color: #920784;
}

.el-form {
  position: absolute;
  left: 30%;
  top: 30%;

}
</style>