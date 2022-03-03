<template>
  <body>
  <div id="mainPage" class="mainBody">
    <div id="whiteHeader" class="white-header">
      <div class="head_wrapper">
        <router-link to="/">
          <a class="logo">博客首页</a>
        </router-link>
        <div class="right-me-info">
          <router-link :to="{path: '/homepage', query: {id: userInfo.id}}">
            <div class="pic">
              <el-avatar :src="userInfo.face" :size="40"/>
            </div>
          </router-link>
          <el-dropdown>
            <span class="el-dropdown-link" style="font-weight: 300">消息</span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>评论</el-dropdown-item>
              <el-dropdown-item>点赞</el-dropdown-item>
              <el-dropdown-item>关注</el-dropdown-item>
              <el-dropdown-item>私信</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>


      </div>

    </div>

    <!-- 页面容器 -->
    <div class="container">
      <div id="all-menus" class="all-menus">

        <ul class="menu-list shadow">
          <li>
            <div class="menu-title-wrapper">
              <i class="el-icon-edit" style="font-size:20px"></i>
              <div class="menu-title">写作中心</div>
            </div>
            <ul>
              <li class="menu-item">
                <router-link to="/center/manage">
                  <a href="javascript:void(0);"
                     :class="[saveTitle === '/center/manage' ? 'title' : '']">内容管理</a>
                </router-link>
              </li>
              <li class="menu-item">
                <router-link to="/center/comment">
                  <a href="javascript:void(0);"
                     :class="[saveTitle === '/center/comment' ? 'title' : '']">评论管理</a>
                </router-link>
              </li>
            </ul>
          </li>
        </ul>

        <ul class="menu-list shadow">
          <li class="">
            <div class="menu-title-wrapper">
              <i class=" el-icon-s-custom" style="font-size:20px"></i>
              <div class="menu-title">个人账号</div>
            </div>
            <ul>
              <li class="menu-item">
                <router-link to="/center/account">
                  <a href="javascript:void(0);"
                     :class="[saveTitle === '/center/account' ? 'title' : '']">账号设置</a>
                </router-link>
              </li>
              <li class="menu-item">
                <router-link to="/center/loginlog">
                  <a href="javascript:void(0);"
                     :class="[saveTitle === '/center/loginlog' ? 'title' : '']">登录日志</a>
                </router-link>
              </li>
              <li class="menu-item">
                <router-link to="/center/fans">
                  <a href="javascript:void(0);"
                     :class="[saveTitle === '/center/fans' ? 'title' : '']">我的粉丝</a>
                </router-link>
              </li>
              <li class="menu-item">
                <router-link to="/center/fansCharts">
                  <a href="javascript:void(0);"
                     :class="[saveTitle === '/center/fansCharts' ? 'title' : '']">粉丝画像</a>
                </router-link>
              </li>
            </ul>
          </li>
        </ul>
      </div>

      <div>
        <router-view/>
      </div>
    </div>
  </div>
  </body>
</template>

<script>
import {mapGetters, mapMutations} from "vuex";
import {delCookie, getCookie} from "@/utils/cookie";
import {authVerify} from "@/api/user";


export default {
  name: "WritingCenterLayout",
  data() {
    return {
      userInfo: {
        face: '',
      },
      saveTitle: ''
    }
  },
  watch: {
    $route(to, from) {
      this.getCurrentPageTitle()
    }
  },
  created() {
    this.getCurrentPageTitle();
    this.userInfo = this.getUserInfo();
    this.getToken();

  },

  methods: {
    ...mapGetters(['getUserInfo']),
    ...mapMutations(['setUserInfo', 'setLoginState']),
    getCurrentPageTitle() {
      this.saveTitle = window.location.pathname;
    },
    getToken() {
      let token = getCookie("token");
      if (token !== null) {
        authVerify(token).then(response => {
          if (response.data.success) {
            this.isLogin = true;
            this.userInfo = response.data.data;
            this.setUserInfo(this.userInfo)
          } else {
            this.isLogin = false;
            delCookie("token");
            this.$message.error(response.data.msg);
            this.$router.push('/');
          }
          this.setLoginState(this.isLogin);
        });
      } else {
        this.isLogin = false;
        this.setLoginState(this.isLogin);
        this.$message.error("会话失效!");
        this.$router.push('/');
      }
    },
  },
  mounted() {

}
}
</script>

<style scoped>
body {
  background-color: #eef0f1;
  overflow-y: auto;
  width: 100%;
  height: 100%;
  font-size: 14px;
  margin: 0;
  padding: 0;
  border: 0;
}

.white-header {
  background-color: #ffffff;
  height: 58px;
  top: 0;
  left: 0;
  position: fixed;
  width: 100vw;
  z-index: 1000;
  box-shadow: 0 2px 4px 0 rgba(9, 9, 9, 0.08);
  min-width: 1220px;
}

.head_wrapper {
  margin: 0 auto;
  height: 100%;
  z-index: 100;
  width: 1180px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  cursor: pointer;
  text-align: center;
}

.head_wrapper .logo {
  color: #006cff;
  text-decoration: none;
  font-size: 22px;
  font-weight: 700;
  line-height: 60px;
}

.right-me-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}
.right-me-info .pic{
 margin-right: 30px;
}

.container {
  width: 1170px;
  margin: 80px 50px;
  display: flex;
  flex-direction: row;
}

.mainBody {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.menu-list {
  background-color: white;
  width: 150px;
  padding: 15px 35px;
}

a {
  text-decoration: none;
  color: gray;
}

.menu-title-wrapper {
  line-height: 20px;
  display: flex;
  flex-direction: row;
  margin-bottom: 20px;
}

ol, ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.menu-title {
  font-size: 16px;
  margin-left: 10px;
  color: #222222;
}

.menu-item {
  font-size: 14px;
  padding-left: 30px;
  height: 40px;
  line-height: 40px;
}

.menu-item a:hover {
  color: red;
}

.menu-item .title {
  color: red;
}
.shadow {
  box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 10px;
  border-bottom: 1px solid #e6e6e6;
}
</style>