<template>
  <div class="login-container">
    <h3 class="title">流金岁月博客登陆</h3>
    <el-tabs value="acc_login" @tab-click="handleClick" stretch>
      <el-tab-pane label="账户登陆" name="acc_login">
        <el-form
            ref="accountLoginForm"
            :model="accountLoginForm"
            :rules="accountLoginRules"
            auto-complete="on"
            label-position="left">
          <el-form-item prop="username">
            <el-input
                v-model="accountLoginForm.username"
                ref="userNameInput"
                name="username"
                type="text"
                auto-complete="on"
                placeholder="请输入登录名"
                @keyup.enter.native="handleSubmit">
              <i slot="prefix" class="el-icon-user"></i>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
                v-model="accountLoginForm.password"
                name="password"
                auto-complete="on"
                placeholder="请输入密码"
                show-password
                @keyup.enter.native="handleSubmit">
              <i slot="prefix" class="el-icon-lock"></i>
            </el-input>
          </el-form-item>
          <el-checkbox v-model="accountLoginForm.isRememberMe" style="margin:0 0 25px 0;"><span>七天免登录</span>
          </el-checkbox>
          <el-form-item>
            <el-button
                :loading="loading"
                type="primary"
                style="width:100%;"
                @click.native.prevent="handleSubmit">登 录
            </el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="手机号登陆/注册" name="phone_login">
        <el-form
            ref="phoneLoginForm"
            :model="phoneLoginForm"
            :rules="phoneLoginRules"
            auto-complete="on"
            label-position="left">
          <el-form-item prop="phone">
            <el-input placeholder="请输入手机号" name="phone" v-model="phoneLoginForm.phone">
              <i slot="prefix" class="el-icon-phone"></i>
            </el-input>
          </el-form-item>
          <el-form-item prop="smsCode">
            <el-row :gutter="24">
              <el-col :span="16">
                <el-input placeholder="6位数字验证码" oninput="value = value.replace(/[^\d]/g,'')"
                          maxlength="6" name="smsCode" v-model="phoneLoginForm.smsCode">
                  <i slot="prefix" class="el-icon-tickets"></i>
                </el-input>
              </el-col>
              <el-col :span="8">
                <el-button @click="onSendSms" :disabled="sendDisabled" class="primary">{{ sendCodeBtn }}</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item>
            <el-button
                :loading="loading"
                type="primary"
                style="width:100%;"
                @click.native.prevent="handleSubmit">登 录
            </el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script>


import {getSmsCode, localLogin, phoneLogin} from "@/api/user";
import {setCookie} from "@/utils/cookie";

export default {
  components: {},
  name: 'login',
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
    // 校验手机号
    const validateMobile = (rule, value, callback) => {
      if (!value || new RegExp(/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/).test(value)) {
        callback();
      } else {
        callback(new Error("您的手机号码格式不正确!"));
      }
    };
    return {
      activeName: 'acc_login',
      accountLoginForm: {
        username: "",
        password: "",
        isRememberMe: false,
      },
      phoneLoginForm: {
        phone: '',
        smsCode: "",
      },
      accountLoginRules: {
        username: [
          {required: true, trigger: "blur", validator: validateUsername}
        ],
        password: [{required: true, trigger: "blur", validator: validatePass}]
      },
      phoneLoginRules: {
        phone: [
          {required: true, message: '请输入手机号码!', trigger: "blur"},
          {validator: validateMobile}
        ],
        smsCode: [{
          required: true, message: '请输入验证码!', trigger: "blur", maxLength: 6
        }]
      },
      loading: false,
      sending: false,
      sendDisabled: false,
      time: 60,
      sendCodeBtn: "发送验证码"

    }
  },
  beforeDestroy() {

  },
  created() {
    this.rememberMe = true;
  },
  methods: {
    handleClick(tab) {
      this.activeName = tab.name;
    },
    handleRememberMeChange(e) {
      this.rememberMe = e.target.checked
    },
    // 发送验证码
    onSendSms() {
      // 校验手机号码
      this.$refs.phoneLoginForm.validateField('phone', errorMessage => {
        if (errorMessage) {
          this.$message.error(errorMessage);
        } else {
          let mobile = this.phoneLoginForm.phone;
          getSmsCode(mobile).then((response) => {
            let timer = setInterval(() => {
              this.time--;
              this.sendCodeBtn = `${this.time}s后重发`;
              this.sendDisabled = true;
              if (this.time === 0) {
                this.sendDisabled = false;
                this.sendCodeBtn = "发送验证码";
                this.time = 60;
                clearTimeout(timer);
              }
            }, 1000);
            this.$message.success(response.msg);
            this.$notify.success({
              title: '验证码',
              message: response.data,
              offset: 100
            });
          })
        }
      })
    },

    // 登录
    handleSubmit() {
      if (this.activeName === 'acc_login') {
        this.accountLogin();
      } else if (this.activeName === 'phone_login') {
        this.phoneLogin();
      } else {
        this.$message.error("error");
      }
    },
    accountLogin() {
      this.$refs.accountLoginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          let params = {};
          params.auth = this.accountLoginForm.username;
          params.password = this.accountLoginForm.password;
          localLogin(params).then(response => {
            // 跳转到首页
            let token = response.data;
            this.$message.success(response.msg);
            setCookie("token", token, 7);
            this.$router.push('/');
            this.loading = false;
          }, () => {
            this.loading = false;
          });
        }
      })
    },
    phoneLogin() {
      this.$refs.phoneLoginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          let params = {};
          params.mobile = this.phoneLoginForm.phone;
          params.smsCode = this.phoneLoginForm.smsCode;
          phoneLogin(params).then(response => {
            let userStatus = response.data.userStatus;
            let token = response.data.token;
            if (userStatus === 0) {
              // 用户未激活
              this.$confirm('是否完善您的个人信息', '确认信息', {
                distinguishCancelAndClose: true,
                confirmButtonText: '等会再说',
                cancelButtonText: '完善个人信息'
              }).then(() => {
                // 直接去向首页
                setCookie("token", token, 7);
                this.$router.push('/');
              }).catch(() => {
                // 储存token
                setCookie("token", token, 7);
                // 去个人中心完善信息
                this.$router.push('/center/account');
              });
            } else if (userStatus === 1) {
              // 用户已激活
              this.$message.success(response.msg);
              setCookie("token", token, 7);
              this.$router.push('/');
            }
            this.loading = false;
          }, () => {
            this.loading = false;
          });
        }
      })
    }
  },
  mounted() {
    this.$refs.userNameInput.focus()
  },

}
</script>
<style rel="stylesheet/scss" lang="scss">
$dark_gray: #889aa4;
$light_gray: #eee;
.login-container {
  background-image: url("../../assets/background.svg");
  position: absolute;
  left: 0;
  right: 0;
  height: 100%;
  width: 100%;

  .el-tabs {
    width: 385px;
    max-width: 100%;
    margin: 60px auto;
  }

  .title {
    font-size: 26px;
    color: $dark_gray;
    margin: 60px auto 20px;
    text-align: center;
    font-weight: bold;
  }


}
</style>
