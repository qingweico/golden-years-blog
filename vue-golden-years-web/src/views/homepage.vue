<template>

  <div class="container">

    <div class="else-info-wrapper">

      <div class="counts-wrapper">
        <div class="padd">
          <div class="inner-flex sign">
            <span class="counts-words">{{ userInfo.myFollowCounts == null ? 0 : userInfo.myFollowCounts }}</span>
            <span class="counts-cn-words">关注</span>
          </div>
          <div class="middle-line"></div>
          <div class="inner-flex">
            <span class="counts-words">{{ userInfo.myFansCounts == null ? 0 : userInfo.myFansCounts }}</span>
            <span class="counts-cn-words">粉丝</span>
          </div>
        </div>
      </div>



      </div>
    </div>
</template>

<script>
export default {
  name: "homepage",
  data() {
    return {
      userInfo: {},
      articleList: [],
      goodArticleList: [],
      isFollowUser: false,
      // 分页页数
      page: 1,
      // 分页每页显示数量
      pageSize: 10,
      // 总页数
      totalPage: 1,
      // 总记录数
      total: 1,
    }
  },
  methods: {
    // 成为粉丝, 关注作者
    beYourFan() {
    },
    // 取消关注
    notYourFan() {
    },
    queryUserInfo() {
    },

    queryUserBlog() {
    }

  }
}
</script>

<style scoped>
body {
  width: 100%;
  height: 100%;
  margin:0;

  font-size: 12px;
  line-height: 1.5;
  color: #657181;
  background-color: #fff;
  -webkit-font-smoothing: antialiased;
}
.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-top: 100px;
  margin-bottom: 200px;
}

/* 右侧 */

.counts-wrapper {
  width: 340px;
  height: 85px;
  background-color: white;
  margin-bottom: 15px;
}

.padd {
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  padding: 16px;
}

.counts-words {
  font-weight: 700;
  color: #222;
  font-size: 22px;
  align-self: center;
}

.counts-cn-words {
  font-size: 14px;
  align-self: center;
}

.inner-flex {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.middle-line {
  display: block;
  background: #e8e8e8;
  height: 46px;
  width: 1px;
  left: 100%;
  top: 18px;
}



</style>