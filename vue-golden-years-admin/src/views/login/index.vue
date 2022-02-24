<template>
  <div class="login-container">
    <el-form
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        auto-complete="on"
        label-position="left"
    >
      <h3 class="title">流金岁月博客后台管理系统</h3>
      <el-form-item prop="username">
        <span class="svg-container svg-container_login">
          <svg-icon icon-class="user"/>
        </span>
        <el-input
            v-model="loginForm.username"
            ref="userNameInput"
            name="username"
            type="text"
            auto-complete="on"
            placeholder="username"
            @keyup.enter.native="handleLogin"
        />
      </el-form-item>
      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password"/>
        </span>
        <el-input
            :type="pwdType"
            v-model="loginForm.password"
            name="password"
            auto-complete="on"
            placeholder="password"
            @keyup.enter.native="handleLogin"
        />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="iconClass"/>
        </span>
      </el-form-item>

      <el-checkbox v-model="loginForm.isRememberMe" style="margin:0 0 25px 0;"><span style="color: #eee">七天免登录</span>
      </el-checkbox>

      <el-form-item>
        <el-button
            :loading="loading"
            type="primary"
            style="width:100%;"
            @click.native.prevent="handleLogin"
        >登 录
        </el-button>
      </el-form-item>
    </el-form>

    <!--引入粒子特效-->
    <vue-particles
        color="#fff"
        :particleOpacity="0.7"
        :particlesNumber="60"
        shapeType="circle"
        :particleSize="4"
        linesColor="#fff"
        :linesWidth="1"
        :lineLinked="true"
        :lineOpacity="0.4"
        :linesDistance="150"
        :moveSpeed="2"
        :hoverEffect="true"
        hoverMode="grab"
        :clickEffect="true"
        clickMode="push"
        class="lizi"
    >
    </vue-particles>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    const validateUsername = (rule, value, callback) => {
      if (value.length === 0) {
        callback(new Error("请输入用户名"));
      } else {
        callback();
      }
    };
    const validatePass = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error("密码不能小于6位"));
      } else {
        callback();
      }
    };
    return {
      loginForm: {
        username: "",
        password: "",
        isRememberMe: false,
      },
      loginRules: {
        username: [
          {required: true, trigger: "blur", validator: validateUsername}
        ],
        password: [{required: true, trigger: "blur", validator: validatePass}]
      },
      loading: false,
      pwdType: "password",
      iconClass: "eye",
      redirect: undefined,
    };
  },
  watch: {},
  mounted() {
    this.$refs.userNameInput.focus()
  },
  created() {
  console.log(process.env);
  },
  methods: {
    inputFocus: function () {
      this.$nextTick(() => {
        this.$refs.userNameInput.focus()
      })
    },
    showPwd() {
      if (this.pwdType === "password") {
        this.pwdType = "";
        this.iconClass = "eye-open";
      } else {
        this.pwdType = "password";
        this.iconClass = "eye";
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.$store
              .dispatch("Login", this.loginForm)
              .then(response => {
                if (response.code == this.$ECode.SUCCESS) {
                  this.$router.push({path: this.redirect || "/"});
                } else {
                  this.$message.error(response.data);
                }
                this.loading = false;
              }, () => {
                this.$message.error('网络超时');
                this.loading = false;
              })
              .catch(() => {
                this.loading = false;
              });
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
$bg: #2d3a4b;
$light_gray: #eee;

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0;
      -webkit-appearance: none;
      border-radius: 0;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;

      &:-webkit-autofill {
        -webkit-box-shadow: 0 0 0 1000px $bg inset !important;
        -webkit-text-fill-color: #fff !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;
.login-container {
  position: fixed;
  height: 100%;
  width: 100%;
  background-color: $bg;

  .login-form {
    position: absolute;
    left: 0;
    right: 0;
    width: 520px;
    max-width: 100%;
    padding: 35px 35px 15px 35px;
    margin: 120px auto;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;

    &_login {
      font-size: 20px;
    }
  }

  .title {
    font-size: 26px;
    color: $light_gray;
    margin: 0 auto 40px auto;
    text-align: center;
    font-weight: bold;
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
