<template>
  <html>
  <body>
  <head>
    <meta charset="utf-8">
    <title>流金岁月</title>
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
          <a href="javascript:void(0);">流金岁月博客</a>
        </router-link>
      </div>

      <h2 id="mnavh" @click="openHead" :class="showHead?'open':''">
        <span class="navicon"></span>
      </h2>

      <ul id="star_list" :style="showHead?'display: block':''">
        <li>
          <router-link to="/">
            <a href="javascript:void(0);" :class="[saveTitle === '/' ? 'title' : '']">首页</a>
          </router-link>
        </li>
        <li>
          <router-link to="/classify" v-if="isLogin">
            <a href="javascript:void(0);" :class="[saveTitle === '/classify' ? 'title' : '']">分类</a>
          </router-link>
        </li>
        <li>
          <router-link to="/time" v-if="isLogin">
            <a href="javascript:void(0);" :class="[saveTitle === '/time' ? 'title' : '']">时间轴</a>
          </router-link>
        </li>

        <li>
          <router-link to="/rank">
            <a href="javascript:void(0);" :class="[saveTitle === '/tag' ? 'title' : '']">排行</a>
          </router-link>
        </li>

        <li>
          <router-link to="/ssr">
            <a href="javascript:void(0);" :class="[saveTitle === '/time' ? 'title' : '']">订阅</a>
          </router-link>
        </li>

        <li>
          <el-button type="primary" icon="el-icon-edit" @click="goWritingCenter">创作中心</el-button>
        </li>
      </ul>
      <div class="search_box">
        <div id="search_bar" :class="(showSearch || keyword.length > 0)?'search_bar search_open':'search_bar'">
          <input
              ref="searchInput"
              class="input"
              placeholder="想搜点什么呢.."
              type="text"
              name="keyboard"
              v-model="keyword"
              v-on:keyup.enter="search">
          <p class="search_ico" @click="clickSearchIco">
            <span></span>
          </p>
        </div>
      </div>

      <el-dropdown @command="handleCommand" class="userInfoAvatar">
        <span class="el-dropdown-link">
          <el-badge class="item" :hidden="!isLogin">
            <img v-if="!isLogin" src="../../static/images/defaultAvatar.png" alt="">
            <img v-if="isLogin && userInfo.face !== undefined" :src="userInfo.face"
                 onerror="onerror = null; src = defaultAvatar" alt="">
            <img v-if="isLogin && userInfo.face === undefined"
                 :src="defaultAvatar" alt="">
          </el-badge>
        </span>

        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="login" v-show="!isLogin">登录</el-dropdown-item>
          <el-dropdown-item command="goUserHomePage" v-show="isLogin">个人中心</el-dropdown-item>
          <el-dropdown-item command="logout" v-show="isLogin">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>

    </nav>
  </header>
  <LoginBox v-if="showLogin" @closeLoginBox="closeLoginBox"></LoginBox>

  <!--文章-->
  <div class="content" >
    <div class="article">
      <router-view/>
    </div>
  </div>

  <!--底部-->
  <footer>
    <p style="margin-bottom: 10px"> 豫ICP备2020030311号</p>
    <p>Copyright © 2020 - 2022 流金岁月 All Rights Reserved</p>
  </footer>


  <!--回到顶部-->
  <div>
    <a href="javascript:void(0);" @click="returnTop"
       :class="isCdTopVisible?'cd-top cd-is-visible':'cd-top'">Top</a>
  </div>


  </body>
  </html>
</template>

<script>


import LoginBox from "../components/LoginBox";
import {mapMutations} from 'vuex';
import {delCookie, getCookie, setCookie} from "@/utils/cookie";
import {authVerify, deleteUserAccessToken} from "@/api/user";
import {timeAgo} from "@/utils/web";

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
      // 默认头像
      defaultAvatar: this.$SysConf.defaultAvatar,

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
    watch: {
      $route(to, from) {
        this.getCurrentPageTitle()
      }
    },
    // 判断登录状态位是否改变; 用于控制弹框
    '$store.state.app.loginMessage': function (newFlag, oldFlag) {
      this.showLogin = true
    }
  },
  created() {

    this.getKeyword();
    this.setSize();
    this.getToken();
    // 获取浏览器类型: navigator.userAgent
    this.getCurrentPageTitle();
  },
  methods: {
    //拿到vuex中的写的方法
    ...mapMutations(['setUserInfo', 'setLoginState']),
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
    getCurrentPageTitle() {
      this.saveTitle = window.location.pathname;
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
    goToDetail(blogId) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {blogId: blogId}
      });
      window.open(routeData.href, '_blank');
    },
    goWritingCenter() {
      if (!this.isLogin) {
        this.showLogin = true;
        return;
      }
      this.$router.push("/center");
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

    submitForm(type) {
    },

    getKeyword() {
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
    getToken() {
      let token = getCookie("token")
      if (token !== null) {
        authVerify(token).then(response => {
          if (response.data.success) {
            this.isLogin = true;
            this.userInfo = response.data.data;
            this.setUserInfo(this.userInfo)
          } else {
            this.isLogin = false;
            delCookie("token");
          }
          this.setLoginState(this.isLogin);
        });
      } else {
        this.isLogin = false;
        this.setLoginState(this.isLogin);
      }
    },

    /**
     * 截取URL中的参数
     * @returns {{}}
     */
    getUrlVars: function () {
      const vars = {};
      window.location.href.replace(
          /[?&]+([^=&]+)=([^&#]*)/gi,
          function (m, key, value) {
            vars[key] = value;
          }
      );
      return vars;
    }
    ,
    clickSearchIco: function () {
      if (this.keyword !== "") {
        this.search();
      }
      this.showSearch = !this.showSearch;
      // 获取焦点
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
        case "goUserHomePage" : {
          this.goUserHomePage();
        }
          break;
      }
    },
    userLogout() {
      deleteUserAccessToken(getCookie("token"));
      delCookie("token");
      let url = window.parent.location.href;
      let haveToken = url.indexOf("?token")
      if (haveToken !== -1) {
        let list = url.split("?token");
        this.isLogin = false;
        window.location.href = list[0]
        let userInfo = {};
        this.setUserInfo(userInfo);
        this.$message.success('已退出');
      } else {
        this.$message.success('已退出');
        window.location.reload()
      }

    },
    goUserHomePage() {
      let routeData = this.$router.resolve({
        path: "/homepage",
        query: {id: this.userInfo.id}
      });
      window.open(routeData.href, '_self');
    },
    closeLoginBox: function () {
      this.showLogin = false;
    },
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
html, body {
  height: 100%;
}

nav .logo {
  color: #006cff;
  text-decoration: none;
  font-size: 22px;
  font-weight: 700;
  line-height: 60px;
}

#star_list li .title {
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

  .search_box {
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

.content {
  min-height: 100%;
  .article {
    padding-bottom: 280px;
    zoom: 1;
  }
}

footer {
  padding: 10px;
  margin-top: 100px;
}
</style>
