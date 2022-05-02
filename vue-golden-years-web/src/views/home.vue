<template>
  <html>
  <head>
    <meta charset="utf-8">
    <title></title>
    <meta name="keywords">
    <meta name="description">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
  </head>
  <body>
  <!--顶栏开始-->
  <header
      :class="isVisible?'header-navigation slideDown':'header-navigation slideUp'"
      id="header">
    <nav>
      <el-row :gutter="20">
        <el-col :span="16">
          <div class="logo">
            <router-link to="/">
              <a href="javascript:void(0);" v-if="webInfo.name">{{ webInfo.name }}</a>
            </router-link>
          </div>

          <h2 id="m_nav" @click="openHead" :class="showHead?'open':''">
            <span class="nav_icon"></span>
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
              <router-link to="/archive" v-if="isLogin">
                <a href="javascript:void(0);" :class="[saveTitle === '/archive' ? 'title' : '']">归档</a>
              </router-link>
            </li>

            <li>
              <router-link to="/rank">
                <a href="javascript:void(0);" :class="[saveTitle === '/rank' ? 'title' : '']">排行</a>
              </router-link>
            </li>
            <li>
              <a href="http://blog.qingweico.cn/yiqing">疫情</a>
            </li>
            <li>
              <router-link to="/ssr">
                <a href="javascript:void(0);" :class="[saveTitle === '/ssr' ? 'title' : '']">订阅</a>
              </router-link>
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
        </el-col>
        <el-col :span="3">
          <el-button icon="el-icon-edit" size="small" style="background: #920784; color: white"
                     @click="goWritingCenter">创作中心
          </el-button>
        </el-col>
      </el-row>
    </nav>
  </header>
  <!--顶栏结束-->

  <!--登录框-->
  <LoginBox v-if="showLogin" @closeLoginBox="closeLoginBox"></LoginBox>

  <!--主体区域; 文章和侧边栏-->
  <div class="content">
    <div class="article">
      <router-view/>
    </div>
  </div>

  <!--底部-->
  <footer>
    <p>
      Copyright © 2020 流金岁月 All Rights Reserved &nbsp;流金岁月博客&nbsp;
      <a href="https://beian.miit.gov.cn/" v-if="webInfo.recordNum">{{ webInfo.recordNum }}</a>
    </p>
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
import LoginBox from "@/components/LoginBox";
import {mapMutations} from 'vuex';
import {delCookie, getCookie} from "@/utils/cookie";
import {authVerify, deleteUserAccessToken} from "@/api/user";
import {getUrlVars} from "@/utils/util";
import {getWebConfig} from "@/api";
import {getSystemConfig} from "@/api/center";

export default {
  name: "index",
  components: {
    LoginBox
  },
  data() {
    return {
      webInfo: {},
      // 默认头像
      defaultAvatar: this.$SysConf.defaultAvatar,
      systemConfig: {},
      saveTitle: "",
      keyword: "",
      // 控制搜索框的弹出
      showSearch: true,
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
    $route(to, from) {
      this.getCurrentPageTitle()
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
    getWebConfig().then(response => {
      this.webInfo = response.data;
    });
    getSystemConfig().then((response) => {
      this.systemConfig = response.data;
    });
  },
  methods: {
    ...mapMutations(['setUserInfo', 'setLoginState']),
    // 搜索
    search() {
      if (this.keyword === "" || this.keyword.trim() === "") {
        this.$refs.searchInput.focus();
        return;
      }
      this.$router.push({path: "/", query: {keyword: this.keyword, searchModel: this.systemConfig.searchModel}});
    },
    getCurrentPageTitle() {
      let test = window.location.href;
      let start = 0;
      let end = test.length;
      for (let i = 0; i < test.length; i++) {
        if (test[i] === "#") {
          start = i;
        }
        if (test[i] === "?" && i > start) {
          end = i;
        }
      }
      this.saveTitle = test.substring(start + 1, end);
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
        query: {id: blogId}
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
    getKeyword() {
      let tempValue = decodeURI(getUrlVars()["keyword"]);
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
          if (response.success) {
            this.isLogin = true;
            this.userInfo = response.data;
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
    }
    ,
    clickSearchIco() {
      this.search();
      this.showSearch = !this.showSearch;
    },
    openHead() {
      this.showHead = !this.showHead;
    },
    returnTop() {
      window.scrollTo(0, 0);
    },
    userLogin() {
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
    closeLoginBox() {
      this.showLogin = false;
    },
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
html, body {
  height: 100%;
}

#star_list li .title {
  color: #920784;
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

.content {
  min-height: 100%;

  .article {
    padding-bottom: 180px;
    zoom: 1;
  }
}
</style>
