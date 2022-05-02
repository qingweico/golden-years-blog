<template>
  <div class="main-page">
    <div class="title-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/center/account' }">账户设置</el-breadcrumb-item>
        <el-breadcrumb-item><a>修改密码</a></el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-form ref="form" :model="form" :rules="rules" label-width="80px" style="width: 50%;">
      <span>默认密码为123456</span>
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" placeholder="请输入旧密码" type="password" show-password/>
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" placeholder="请输入新密码" type="password" show-password/>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" placeholder="请确认密码" type="password" show-password/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" size="mini" @click="submit">提交</el-button>
        <el-button type="danger" size="mini" @click="close">关闭</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {alterPwd} from "@/api/center";
import {mapGetters} from "vuex";

export default {
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.form.newPassword !== value) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    return {
      userId: "",
      form: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      },
      loading: false,
      // 表单校验
      rules: {
        oldPassword: [
          {required: true, message: "旧密码不能为空", trigger: "blur"}
        ],
        newPassword: [
          {required: true, message: "新密码不能为空", trigger: "blur"},
          {min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur"}
        ],
        confirmPassword: [
          {required: true, message: "确认密码不能为空", trigger: "blur"},
          {required: true, validator: equalToPassword, trigger: "blur"}
        ]
      }
    }
  },
  created() {
    this.userId = this.getUserInfo.id;
  },
  computed: {
    ...mapGetters(['getUserInfo']),
  },
  methods: {
    submit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.loading = true;
          this.form.userId = this.userId;
          alterPwd(this.form).then((response) => {
            this.loading = false;
            this.$message.success(response.msg);
            this.form = {};
          })
        }
      })
    },
    close() {
      this.$router.push("/center/account");
    }
  }
}
</script>

<style scoped>
.main-page {
  width: 980px;
  margin-left: 20px;
  background-color: white;
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
</style>