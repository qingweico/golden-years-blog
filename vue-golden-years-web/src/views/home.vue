<template>
  <html>
  <body>
  <head>
    <meta charset="utf-8">
    <title></title>
    <meta name="keywords">
    <meta name="description">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
  </head>

  <header
      :class="isVisible?'header-navigation slideDown':'header-navigation slideUp'"
      id="header"
  >
    <nav>
      <div class="logo">
        <router-link to="/">
          <a href="javascript:void(0);"></a>
        </router-link>
      </div>

      <h2 id="mnavh" @click="openHead" :class="showHead?'open':''">
        <span class="navicon"></span>
      </h2>

      <ul id="starlist" :style="showHead?'display: block':''">
        <li>
          <router-link to="/">
            <a href="javascript:void(0);" :class="[saveTitle === '/' ? 'title' : '']">首页</a>
          </router-link>
        </li>


        <li>
          <router-link to="/classify">
            <a href="javascript:void(0);" :class="[saveTitle === '/classify' ? 'title' : '']">分类</a>
          </router-link>
        </li>

        <li>
          <router-link to="/rank">
            <a href="javascript:void(0);" :class="[saveTitle === '/tag' ? 'title' : '']">排行</a>
          </router-link>
        </li>

        <li>
          <router-link to="/tag">
            <a href="javascript:void(0);" :class="[saveTitle === '/time' ? 'title' : '']">标签</a>
          </router-link>
        </li>
        <li>
          <router-link to="/ssr">
            <a href="javascript:void(0);" :class="[saveTitle === '/time' ? 'title' : '']">订阅</a>
          </router-link>
        </li>

      </ul>

      <div class="searchbox">
        <div id="search_bar" :class="(showSearch || keyword.length > 0)?'search_bar search_open':'search_bar'">
          <input
              ref="searchInput"
              class="input"
              placeholder="想搜点什么呢.."
              type="text"
              name="keyboard"
              v-model="keyword"
              v-on:keyup.enter="search">
          <p class="search_ico" @click="clickSearchIco" :style="(browserFlag === 1)?'':'top:17px'">
            <span></span>
          </p>
        </div>
      </div>

      <el-dropdown @command="handleCommand" class="userInfoAvatar">

        <span class="el-dropdown-link">
          <el-badge class="item" :hidden="!isLogin">
            <img v-if="!isLogin" src="../../static/images/defaultAvatar.png" alt="">
            <img v-if="isLogin && userInfo.photoUrl !== undefined" :src="userInfo.photoUrl"
                 onerror="onerror = null; src = defaultAvatar" alt="">
            <img v-if="isLogin && userInfo.photoUrl === undefined"
                 :src="defaultAvatar" alt="">
          </el-badge>
        </span>

        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="login" v-show="!isLogin">登录</el-dropdown-item>
          <el-dropdown-item command="goUserInfo" v-show="isLogin">个人中心</el-dropdown-item>
          <el-dropdown-item command="logout" v-show="isLogin">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </nav>
  </header>
  <LoginBox v-if="showLogin" @closeLoginBox="closeLoginBox"></LoginBox>


  <div>
    <router-view/>
  </div>

    <footer>
      <p>
        Copyright <a href="https://www.qingweico.cn"> &nbsp;流金岁月&nbsp;</a>
        <a href="https://beian.miit.gov.cn/"></a>
      </p>
    </footer>

  <div>
    <a
        href="javascript:void(0);"
        @click="returnTop"
        :class="isCdTopVisible?'cd-top cd-is-visible':'cd-top'"
    >Top</a>
  </div>
  </body>
  </html>
</template>

<script>


import LoginBox from "../components/LoginBox";
import index from "./index.vue";

import {mapMutations} from 'vuex';


export default {
  name: "index",
  components: {
    LoginBox
  },
  data() {
    return {
      //激活的折叠面板
      activeNames: ['1'],
      // 激活的标签
      activeName: "0",
      imageCropperShow: false,
      imageCropperKey: 0,
      webSite: process.env.VUE_MOGU_WEB,

      saveTitle: "",
      keyword: "",
      // 控制搜索框的弹出
      showSearch: false,
      // 控制导航栏的弹出
      showHead: false,
      isCdTopVisible: false,
      // 控制web端导航的隐藏和显示
      isVisible: true,
      isLogin: false,
      browserFlag: 1,
      // 显示登录框
      showLogin: false,
      // 用户信息
      userInfo: {},
      //控制删除图标的显示
      icon: false,
      labelWidth: "100px",
      userInfoRules: {
        oldPwd: [
          {required: true, message: '旧密码不能为空', trigger: 'blur'},
          {min: 5, max: 20, message: '密码长度在5到20个字符'},
        ],
        newPwd: [
          {required: true, message: '新密码不能为空', trigger: 'blur'},
          {min: 5, max: 20, message: '密码长度在5到20个字符'},
        ],
        newPwd2: [
          {required: true, message: '新密码不能为空', trigger: 'blur'},
          {min: 5, max: 20, message: '密码长度在5到20个字符'},
        ]
      }
    };
  },
  mounted() {
    const that = this;
    let offset = 300;
    let after = 0;
    window.addEventListener("scroll", function () {
      let scrollTop = document.documentElement.scrollTop;
      let scrollHeight = document.documentElement.scrollHeight;
      that.isCdTopVisible = scrollTop > offset;
      that.isVisible = scrollTop <= after;
      after = scrollTop;
    });

    // 屏幕自适应
    window.onresize = () => {
      return (() => {
        that.setSize()
      })()
    }
  },
  watch: {
    // 判断登录状态位是否改变; 用于控制弹框
    '$store.state.app.loginMessage': function (newFlag, oldFlag) {
      this.showLogin = true
    }
  },
  created() {

    this.getKeyword()
    this.setSize()
    // 获取浏览器类型
    this.getBrowser()
    console.log(process.env);
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setUserInfo', 'setLoginState', 'setWebConfigData']),
    // 搜索
    search: function () {
      if (this.keyword === "" || this.keyword.trim() === "") {
        this.$notify.error({
          title: '错误',
          message: "关键字不能为空",
          type: 'success',
          offset: 100
        });
        return;
      }
      this.$router.push({path: "/list", query: {keyword: this.keyword}});
    },

    setSize() {
      // 屏幕大于950px的时候, 显示侧边栏
      let clientWidth = document.body.clientWidth
      if (clientWidth > 1360) {
        this.drawerSize = "30%";
        this.showSearch = true
      } else if (clientWidth < 1360 && clientWidth > 950) {
        this.drawerSize = "50%";
        this.showSearch = true
      } else if (clientWidth < 950 && clientWidth > 650) {
        this.drawerSize = "70%";
        this.showSearch = false
      } else {
        this.drawerSize = "95%";
        this.showSearch = false
      }
    },
    // 跳转到文章详情
    goToInfo(uid) {
      let routeData = this.$router.resolve({
        path: "/info",
        query: {blogUid: uid}
      });
      window.open(routeData.href, '_blank');
    },


    // 跳转到资源详情
    goSource: function (comment) {
      let source = comment.source
      switch (source) {
        case "MESSAGE_BOARD": {
          let routeData = this.$router.resolve({
            path: "/messageBoard"
          });
          window.open(routeData.href, '_blank');
        }
          break;
        case "BLOG_INFO": {
          let routeData = this.$router.resolve({
            path: "/info",
            query: {blogUid: comment.blogUid}
          });
          window.open(routeData.href, '_blank');
        }
          break;
        case "ABOUT": {
          let routeData = this.$router.resolve({
            path: "/about"
          });
          window.open(routeData.href, '_blank');
        }
          break;
      }
    },


    //弹出选择图片框
    checkPhoto() {
      this.imageCropperShow = true
    },

    timeAgo(dateTimeStamp) {
      return timeAgo(dateTimeStamp)
    },


    close() {
      this.imageCropperShow = false
    },

    submitForm: function (type) {
      switch (type) {
        case "editUser": {
          this.$refs.userInfo.validate((valid) => {
            if (!valid) {
              console.log("校验失败")
            } else {
              editUser(this.userInfo).then(response => {
                if (response.code === this.$ECode.SUCCESS) {
                  this.$message({
                    type: "success",
                    message: response.data
                  })
                } else {
                  this.$message({
                    type: "error",
                    message: response.data
                  })
                }
              });
            }
          })
        }
          break;

        case "changePwd": {
          let newPwd = this.userInfo.newPwd
          let newPwd2 = this.userInfo.newPwd2
          let oldPwd = this.userInfo.oldPwd
          if (newPwd !== newPwd2) {
            this.$message({
              type: "error",
              message: "两次密码不一致"
            })
            return
          }
          if (newPwd === oldPwd) {
            this.$message({
              type: "error",
              message: "新旧密码相同"
            })
            return
          }
          let params = new URLSearchParams()
          params.append("oldPwd", oldPwd)
          params.append("newPwd", newPwd)
          updateUserPwd(params).then(response => {
            if (response.code === this.$ECode.SUCCESS) {
              this.$message({
                type: "success",
                message: response.data
              })
            } else {
              this.$message({
                type: "error",
                message: response.data
              })
            }
            // 重置表单
            this.$refs.userInfoForm.resetFields()
          })
        }
          break;
      }
    },

    getKeyword: function () {
      let tempValue = decodeURI(this.getUrlVars()["keyword"]);
      if (
          tempValue === null ||
          tempValue === undefined ||
          tempValue === "undefined"
      ) {
      } else {
        this.keyword = tempValue;
      }
    },

    /**
     * 截取URL中的参数
     * @returns {{}}
     */
    getUrlVars: function () {
      const vars = {};
      let parts = window.location.href.replace(
          /[?&]+([^=&]+)=([^&#]*)/gi,
          function (m, key, value) {
            vars[key] = value;
          }
      );
      return vars;
    },
    clickSearchIco: function () {
      if (this.keyword !== "") {
        this.search();
      }
      this.showSearch = !this.showSearch;
      //获取焦点
      this.$refs.searchInput.focus();
    },
    openHead: function () {
      this.showHead = !this.showHead;
    },
    returnTop: function () {
      window.scrollTo(0, 0);
    },
    userLogin: function () {
      this.showLogin = true;
    },

    // 点击头像触发的动作
    handleCommand(command) {
      switch (command) {
        case "logout" : {
          this.userLogout();
        }
          break;
        case "login" : {
          this.userLogin();
        }
          break;
        case "goUserInfo" : {
          // 打开抽屉

        }
          break;
      }
    },
    closeLoginBox: function () {
      this.showLogin = false;
    },
    // 获取浏览器类型
    getBrowser() {
      let sBrowser, sUsrAg = navigator.userAgent;
      if (sUsrAg.indexOf("Firefox") > -1) {
        sBrowser = "Mozilla Firefox";
        this.browserFlag = 2;
        // "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:61.0) Gecko/20100101 Firefox/61.0"
      } else if (sUsrAg.indexOf("Chrome") > -1) {
        sBrowser = "Google Chrome or Chromium";
        this.browserFlag = 1;
        // "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/66.0.3359.181
        // Chrome/66.0.3359.181 Safari/537.36"
      }

    },
  }
};
</script>


<style scoped>
.el-tag {
  height: 25px;
  line-height: 25px;
  margin-left: 6px;
}

#starlist li .title {
  color: #00a7eb;
}

.userInfoAvatar {
  width: 35px;
  height: 35px;
  position: absolute;
  right: -77px;
}

.userInfoAvatar img {
  width: 35px;
  height: 35px;
  border-radius: 50%;
}

@media only screen and (max-width: 1300px) {
  .userInfoAvatar {
    width: 35px;
    height: 35px;
    position: absolute;
    right: 10px;
    top: 0;
  }

  .searchbox {
    position: absolute;
    right: 50px;
    top: 0
  }
}

.imgBody img {
  width: 100px;
  height: 100px;
}

.uploadImgBody :hover {
  border: dashed 1px #00ccff;
}
</style>
