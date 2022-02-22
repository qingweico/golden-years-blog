<template>
  <body>
  <div id="mainPage" class="mainBody">

    <div id="whiteHeader" class="white-header">
      <div class="head_wrapper">
        <router-link to="/">
          <a class="logo">博客首页</a>
        </router-link>
        <div class="right-me-info">
          <div>
            <el-avatar icon="el-icon-user-solid" :size="40"/>
          </div>
          <span class="me-nickname">{{ userInfo.nickname }}</span>
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
                <router-link to="/center/newly">
                  <a href="javascript:void(0);"
                     :class="[saveTitle === '/center/newly' ? 'title' : '']">发布文章</a>
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
import Login from "@/views/User/Login";

export default {
  name: "WritingCenterLayout",
  components: {Login},
  data() {
    return {
      userInfo: {},
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
  },

  methods: {
    getCurrentPageTitle() {
      this.saveTitle = window.location.pathname;
    },
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

.me-face {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  align-self: center;
}

.me-nickname {
  margin-left: 10px;
  color: gray;
  align-self: center;
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
  width: 200px;
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
.menu-item a:hover{
  color: red;
}
.menu-item .title{
  color: red;
}

.menu-item-default:hover {
  color: #c9394a;
}

.shadow {
  box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 10px;
  border-bottom: 1px solid #e6e6e6;
}
</style>