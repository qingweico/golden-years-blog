<template>
  <div class="main">
    <a-form-model class="user-layout-login" @keyup.enter.native="handleSubmit">
      <a-tabs :activeKey="customActiveKey" :tabBarStyle="{ textAlign: 'center', borderBottom: 'unset' }"
              @change="handleTabClick">
        <a-tab-pane key="tab1" tab="账号密码登录">
          <login-account ref="acc_login" @validateFail="validateFail" @success="requestSuccess"
                         @fail="requestFailed"></login-account>
        </a-tab-pane>

        <a-tab-pane key="tab2" tab="手机号登录/注册">
          <login-phone ref="phone_login" @validateFail="validateFail" @success="requestSuccess"
                       @fail="requestFailed"></login-phone>
        </a-tab-pane>
      </a-tabs>

      <a-form-model-item>
        <a-checkbox @change="handleRememberMeChange" default-checked>记住我</a-checkbox>
        <router-link to="/" class="forge-password" style="float: right;margin-right: 10px">
          返回主页
        </router-link>
      </a-form-model-item>

      <a-form-item style="margin-top:24px">
        <a-button size="large" type="primary" htmlType="submit" class="login-button" :loading="loginBtn"
                  @click.stop.prevent="handleSubmit" :disabled="loginBtn">确定
        </a-button>
      </a-form-item>

    </a-form-model>
  </div>
</template>

<script>
import LoginAccount from './LoginAccount'
import LoginPhone from './LoginPhone'
import {timeFix} from '@/utils/util'
import {Message} from "element-ui";

export default {
  components: {
    LoginAccount,
    LoginPhone,
  },
  data() {
    return {
      customActiveKey: 'tab1',
      loginBtn: false,
    }
  },
  created() {
    this.rememberMe = true
  },
  methods: {
    handleTabClick(key) {
      this.customActiveKey = key;
      this.loginBtn = false;
    },
    handleRememberMeChange(e) {
      this.rememberMe = e.target.checked
    },

    // 登录
    handleSubmit() {
      this.loginBtn = true;
      if (this.customActiveKey === 'tab1') {
        // 使用账户密码登录
        this.$refs.acc_login.handleLogin()
      } else {
        // 手机号码登录
        this.$refs.phone_login.handleLogin()
      }
    },
    // 校验失败
    validateFail() {
      this.loginBtn = false;
    },
    // 登录后台成功
    requestSuccess(loginResult) {
      this.$message.success(loginResult.msg);
      location.replace("/#/?token=" + loginResult.data);
    },
    // 登录后台失败
    requestFailed(msg) {
      this.$notification['error']({
        message: '登录失败',
        description: msg,
        duration: 4,
      });
      this.loginBtn = false;
    },

  }

}
</script>
<style lang="less" scoped>
.user-layout-login {
  label {
    font-size: 14px;
  }

  .getCaptcha {
    display: block;
    width: 100%;
    height: 40px;
  }

  .forge-password {
    font-size: 14px;
  }

  button.login-button {
    padding: 0 15px;
    font-size: 16px;
    height: 40px;
    width: 100%;
  }

  .user-login-other {
    text-align: left;
    margin-top: 24px;
    line-height: 22px;

    .item-icon {
      font-size: 24px;
      color: rgba(0, 0, 0, .2);
      margin-left: 16px;
      vertical-align: middle;
      cursor: pointer;
      transition: color .3s;

      &:hover {
        color: #1890ff;
      }
    }

    .register {
      float: right;
    }
  }
}
</style>