<template>

  <div class="container">
    <div class="big-banner">
      <div class="banner">
        <img src="@/assets/homepage_banner.png" alt="banner"/>
      </div>

      <div class="basic-info-wrapper">
<!--        <div class="writer-face-wrapper">-->
<!--          <img :src="userInfo.face" class="writer-face" alt="face"/>-->
<!--        </div>-->
<!--        <div class="writer-name">{{ userInfo.nickname }}</div>-->
<!--        <button class="pay-attention" @click="beYourFan(userInfo.id, userInfo.id)"-->
<!--                v-show="!isFollowUser && userInfo != null">关注我-->
<!--        </button>-->
<!--        <button class="already-pay-attention" @click="notYourFan(userInfo.id, userInfo.id)"-->
<!--                v-show="isFollowUser && userInfo != null">已关注-->
<!--        </button>-->
<!--      </div>-->
    </div>

    <div class="main-info">
      <div class="writer-articles-wrapper">
        <div class="tabs">
          <div class="tab-item">文章</div>
        </div>
        <div class="writer-articles">
          <div id="articleList" class="article-list">
            <ul class="blank-left">

              <li class="single-article-wrapper" v-for="(article, index) in articleList" :key="index">
                <img :src="article.articleCover" class="article-cover"
                     v-show="article.articleType === 1" alt="cover">

                <div class="single-article">
                  <div class="article-title">
                    <a :href="'detail.html?articleId='+article.id" target="_blank"
                       class="link-article-title">{{ article.title }}</a>
                  </div>
                  <div class="publisher">
                    <div class="category-tag"></div>
                  </div>
                  <img class="publisher-face"
                       v-if="article.publisherVO != null && article.publisherVO != undefined"
                       :src="article.publisherVO.face" alt="face">
                  <div class="publisher-name"
                       v-if="article.publisherVO != null && article.publisherVO != undefined">
                    &nbsp;&nbsp;{{ article.publisherVO.nickname }}&nbsp;⋅
                  </div>
                  <div class="article-name">&nbsp;{{ article.readCounts }}阅读&nbsp;⋅</div>
                  <div class="publish-time">&nbsp;{{ formatData(article.publishTime) }}</div>
                </div>
          </li>
          </ul>
        </div>
      </div>

    </div>

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

<!--      <div class="recent-wrapper">-->
        <div class="recent">
          <div class="recent-words">近期佳文</div>
          <div class="recent-articles">
            <div class="recent-article-wrapper" v-for="(article, goodIndex) in goodArticleList"
                 :key="goodIndex">
              <div class="recent-article-cover" v-show="article.articleType === 1">
                <img :src="article.articleCover" class="recent-article-cover" alt="cover">
              </div>
              <div class="recent-article-title">
                <div class="recent-title">{{ article.title }}</div>
                <div class="read-counts">{{ article.readCounts }}阅读</div>
              </div>
            </div>
          </div>
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

.banner {
  width: 100%;
  height: 259px;
}

.big-banner {
  width: 1060px;
  margin: 0 auto;
}

.basic-info-wrapper {
  height: 270px;
  background-color: white;
}

.writer-face-wrapper {
  position: absolute;
  margin-top: -40px;
  margin-left: 30px;
}

.writer-face {
  width: 96px;
  height: 96px;
  border: 1px solid rgba(255, 255, 255, .2);
  border-radius: 50%;
}

.writer-name {
  color: #222;
  font-weight: 700;
  font-size: 22px;
  margin-left: 140px;
}

.pay-attention {
  color: white;
  width: 70px;
  font-size: 15px;
  background-color: #208ed1;
  margin-left: 140px;
  border-radius: 5px;
  padding: 2px 10px;
  outline: none;
  border: 0 solid rgba(255, 255, 255, .2);
}

.pay-attention:hover {
  background-color: #36a1e4;
  cursor: pointer;
}

.already-pay-attention {
  color: white;
  width: 70px;
  font-size: 15px;
  background-color: #3dc33d;
  margin-left: 140px;
  border-radius: 5px;
  padding: 2px 10px;
  outline: none;
  border: 0 solid rgba(255, 255, 255, .2);
}

.already-pay-attention:hover {
  background-color: #52e452;
  cursor: pointer;
}

/* 以下是主体部分 */
.main-info {
  width: 1060px;
  margin-top: 20px;
  align-self: center;

  /* flex 左右排列 */
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.writer-articles-wrapper {
  width: 700px;
  height: 46px;
  background-color: white;
  border-bottom: 2px solid #E8E8E8;
}

.tabs {
  border-bottom: 2px solid #E8E8E8;
}

.tab-item {
  margin-left: 40px;
  font-size: 20px;
  width: 40px;
  height: 46px;
  color: #c9394a;
  border-bottom: 2px solid #c9394a;

  display: flex;
  flex-direction: column;
  justify-content: center;
}

.writer-articles {
  background-color: white;
}

.blank-left {
  padding-left: 30px;
}

.single-in-writer {
  padding: 20px 0;
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


/* 近期佳文 */
.recent-wrapper {
  background-color: white;
}

.recent-words {
  font-size: 18px;
  font-weight: 700;
  padding: 13px 0 9px 20px;
  color: #222;
  border-top: 2px solid #ed4040;
  border-bottom: 1px solid #ddd;
}

.recent-article-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: left;
  padding-bottom: 10px;
}

.recent-articles {
  padding-top: 8px;
}

.recent-article-title {
  width: 180px;
  margin-left: 18px;
}

.recent-article-cover {
  width: 118px;
  height: 77px;
  margin-left: 8px;
}

.recent-title {
  color: #222;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
}
</style>