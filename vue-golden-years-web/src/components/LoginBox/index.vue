<template>
  <div>
    <div class="box loginBox" v-if="showLogin === true">
      <div class="title">
        <span class="t1">
          登录
        </span>
        <div class="t2" @click="closeLogin()">
          X
        </div>
      </div>
      <!-- 分割线 -->
      <el-divider></el-divider>

      <el-form :label-position="labelPosition" :rules="loginRules" :model="loginForm" ref="loginForm">

        <div class="passwordLogin" v-if="showPasswordLogin">
          <el-form-item label="用户名" prop="authName">
            <el-input v-model="loginForm.authName" placeholder="请输入用户名或邮箱或手机号"
                      :disabled="loginType.password"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"
                      :disabled="loginType.password"></el-input>
          </el-form-item>
          <el-row class="btn">
            <el-button class="loginBtn" type="primary" @click="startLogin" :loading="loading"
                       :disabled="loginType.password">登录
            </el-button>
            <el-button class="registerBtn" type="info" @click="goRegister" :disabled="loginType.password">注册</el-button>
          </el-row>
        </div>


        <el-row class="elRow">
          <el-tooltip content="Github" placement="bottom">
            <el-button type="info" circle @click="goAuth('github')" :disabled="loginType.github">
              <span class="iconfont">&#xe64a;</span>
            </el-button>
          </el-tooltip>

          <el-tooltip content="QQ" placement="bottom">
            <el-button type="primary" circle @click="goAuth('qq')" :disabled="loginType.qq">
              <span class="iconfont">&#xe601;</span>
            </el-button>
          </el-tooltip>

          <el-tooltip content="微信" placement="bottom">
            <el-button type="success" circle @click="goAuth('wechat')" :disabled="loginType.wechat">
              <span class="iconfont">&#xe66f;</span>
            </el-button>
          </el-tooltip>
          <el-tooltip content="微博" placement="bottom">
            <el-button type="danger" circle @click="goAuth('weibo')" :disabled="loginType.weibo">
              <span class="iconfont">&#xe6f5;</span>
            </el-button>
          </el-tooltip>


        </el-row>
        <div class="loginTip">目前登录方式支持
          <span v-if="!loginType.password"> 账号密码 </span>
          <span v-if="!loginType.weibo"> 微博 </span>
          <span v-if="!loginType.github"> Github </span>
          <span v-if="!loginType.qq"> QQ </span>
          <span v-if="!loginType.wechat"> 微信 </span>
        </div>
      </el-form>

    </div>

    <div class="box registerBox" v-if="showLogin === false">
      <div class="title">
        <span class="t1">
          注册
        </span>
        <div class="t2" @click="closeLogin()">
          X
        </div>
      </div>
      <el-divider></el-divider>
    </div>

    <div class="mask"></div>

  </div>
</template>

<script>
import {Loading} from 'element-ui';
import router from "@/router";
import {localLogin} from "@/api/user";
import {setCookie} from "@/utils/cookie";

export default {
  name: "share",
  data() {
    return {
      loading: false,
      option: {
        fullscreen: true,
        lock: true
      },
      showPasswordLogin: true,
      // 显示登录页面
      showLogin: true,
      isLogin: false,
      table: false,
      dialog: false,
      labelPosition: "right",
      loginForm: {
        authName: "",
        password: ""
      },
      // 登录类别
      loginType: {
        password: false,
        weibo: true,
        github: true,
        qq: true,
        wechat: true
      },
      loginRules: {
        authName: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 2, message: "用户名长度大于等于 2 个字符", trigger: "blur"},
          {max: 20, message: "用户名长度不能大于 20 个字符", trigger: "blur"}
        ],
        password: [
          {required: true, message: "请输入密码", trigger: "blur"},
          {min: 6, message: "密码长度需要大于等于 6 个字符", trigger: "blur"},
          {max: 18, message: "密码长度不能大于 18 个字符", trigger: "blur"}
        ]
      },
    };
  },
  components: {},
  created() {
  },
  methods: {
    startLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          let params = {};
          params.auth = this.loginForm.authName;
          params.password = this.loginForm.password;
          localLogin(params).then(response => {
            this.loading = false;
            // 跳转到首页
            let token = response.data;
            setCookie("token", token, 7);
            this.$message.success(response.msg);
            this.$router.replace('/');
            window.location.reload();
            this.loading = false;
          }, () => {
            this.loading = false;
          });
        }
      });
    },
    goRegister () {
      router.replace("/login");
    },

    goAuth() {
      this.loading = Loading.service({
        lock: true,
        text: '加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    closeLogin() {
      this.$emit("closeLoginBox", "");
    }
  }
};
</script>
<style>
.box {
  width: 400px;
  height: 420px;
  background: white;
  position: fixed;
  margin: auto;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 101;
}

.registerBox {
  height: 660px;
}

.box .title {
  height: 48px;
  font-size: 22px;
  font-weight: bold;
  text-align: center;
  line-height: 48px;
}

.box .title .t2 {
  font-size: 16px;
  float: right;
  margin-right: 6px;
  margin-top: -6px;
  cursor: pointer;
}

.box .el-divider--horizontal {
  margin: 12px 0;
}

.box .el-form-item__label {
  margin-left: 10px;
  font-size: 16px;
}

.box .el-input__inner {
  margin-left: 10px;
  width: 90%;
}

.box .btn {
  text-align: center;
}

.box .loginBtn {
  width: 40%;
}

.box .registerBtn {
  width: 40%;
}

.elRow {
  margin-top: 15px;
  text-align: center;
}

.loginTip {
  margin-top: 10px;
  font-size: 14px;
  text-align: center;
  color: #bababa;
}

.remarksBox {
  position: fixed;
  left: 50%;
  margin-left: -100px;
  top: 50%;
  margin-top: -50px;
  border: 1px solid red;
  width: 200px;
  height: 100px;
  text-align: center;
  z-index: 101;
}

/* 遮罩层 */
.mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
}
</style>
