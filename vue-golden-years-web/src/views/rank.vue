<template>
  <article style="margin-left: 200px; width: 100%">
    <div class="blank"></div>
    <!--blog context begin-->
    <div class="blogs_box">
      <div
          v-for="(item, index) in hotArticles"
          :key="item.id"
          class="blogs"
          data-scroll-reveal="enter bottom over 1s">
        <h3 class="blog_title">
          <a href="javascript:void(0);" @click="goToDetail(item)" v-html="item.title"></a>
        </h3>
        <span class="blog_pic">
          <a href="javascript:void(0);" @click="goToDetail(item)" title>
             <div class="top-left-text">{{ index + 1 }}</div>
            <img v-if="item.articleCover" :src="item.articleCover" alt>
          </a>
        </span>
        <p class="blog_summary">{{ item.summary }} ...</p>
        <div class="blog_info">
          <ul>
            <li class="author" v-if="item.authorVO">
              <span class="iconfont">&#xe60f;</span>
              <a href="javascript:void(0);" @click="goToAuthor(item.authorVO.id)">{{ item.authorVO.nickname }}</a>
            </li>
            <li class="category_name" v-if="item.categoryId">
              <span class="iconfont">&#xe603;</span>
              <a href="javascript:void(0);"
              > {{ getBlogCategoryNameById(item.categoryId) }}</a>
            </li>
            <li class="view">
              <span class="iconfont">&#xe8c7;</span>
              <span>{{ item.readCounts }}</span>
            </li>
            <li class="start_count">
              <span class="iconfont">&#xf010d;</span>
              {{ item.commentCounts }}
            </li>
            <li class="comments">
              <span class="iconfont">&#xe891;</span>
              {{ item.commentCounts }}
            </li>
            <li class="createTime">
              <span class="iconfont">&#xe606;</span>
              {{ item.createTime }}
            </li>
          </ul>
        </div>
      </div>

      <div class="isEnd">
        <div class="lds-css ng-scope" v-if="!isEnd&&loading">
          <div class="load-container">
            <div class="container">
              <div class="boxLoading boxLoading1"></div>
              <div class="boxLoading boxLoading2"></div>
              <div class="boxLoading boxLoading3"></div>
              <div class="boxLoading boxLoading4"></div>
              <div class="boxLoading boxLoading5"></div>
            </div>
          </div>
        </div>
        <el-divider v-if="!loading && total && currentPage >= totalPage">到底了</el-divider>
      </div>
    </div>
    <!--blog context end-->
    <div class="sidebar">

    </div>
  </article>
</template>

<script>
import {getBlogCategory, rank} from "@/api";

export default {
  name: "rank",
  data() {
    return {
      hotArticles: [],
      categoryList: [],
      startLoading: false,
      currentPage: 1,
      pageSize: 5,
      total: 0,
      totalPage: 0,
      // 是否到底底部了
      isEnd: false,
      // 是否正在加载
      loading: false
    };
  },
  mounted() {
    window.addEventListener("scroll", this.handleScroll);
  },
  created() {
    getBlogCategory().then((response) => {
      this.categoryList = response.data;
    });
    this.getHotArticles();
  },
  methods: {
    handleScroll() {
      if (this.getScrollHeight() - (this.getClientHeight() + this.getScrollTop()) < 1) {
        if (!this.isEnd && this.currentPage < this.total) {
          this.loadContent();
        }
      }
    },
    //获取当前滚动条的位置
    getScrollTop() {
      let scrollTop = 0
      if (document.documentElement && document.documentElement.scrollTop) {
        scrollTop = document.documentElement.scrollTop
      } else if (document.body) {
        scrollTop = document.body.scrollTop
      }
      return scrollTop
    },
    //获取当前可视范围的高度
    getClientHeight() {
      let clientHeight = 0
      if (document.body.clientHeight && document.documentElement.clientHeight) {
        clientHeight = Math.min(document.body.clientHeight, document.documentElement.clientHeight)
      } else {
        clientHeight = Math.max(document.body.clientHeight, document.documentElement.clientHeight)
      }
      return clientHeight
    },
    //获取文档完整的高度
    getScrollHeight() {
      return Math.max(document.body.scrollHeight, document.documentElement.scrollHeight)
    },


    // 跳转到文章详情
    goToDetail(blog) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: blog.id}
      })
      window.open(routeData.href, '_blank');
    },
    goToAuthor(authorId) {
      let routeData = this.$router.resolve({
        path: "/homepage",
        query: {id: authorId}
      });
      window.open(routeData.href, '_self');
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.categoryList;
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].name;
        }
      }
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadContent();
    },
    loadContent() {
      const that = this;
      that.loading = true;
      that.currentPage = that.currentPage + 1;
      let params = new URLSearchParams();
      params.append("page", that.currentPage);
      params.append("pageSize", that.pageSize);
      rank(params).then(response => {
        if (response.data.rows.length > 0) {
          that.isEnd = false;
          that.hotArticles = that.hotArticles.concat(response.data.rows);
          that.total = response.data.records;
          that.totalPage = response.data.totalPage;
          that.currentPage = response.data.currentPage;
          // 全部加载完毕
          if (that.hotArticles.length < that.pageSize) {
            that.isEnd = true;
          }
        } else {
          that.isEnd = true;
        }
        that.loading = false;
      })
    },
    getHotArticles() {
      let params = new URLSearchParams();
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      rank(params).then(response => {
        this.hotArticles = response.data.rows;
        this.total = response.data.records;
        this.totalPage = response.data.totalPage;
        this.currentPage = response.data.currentPage;
      })
    },
  }
}

</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.isEnd {
  float: left;
  width: 100%;
  height: 80px;
  text-align: center;
}

.ng-scope {
  margin: 0 auto;
  width: 18%;
  height: 10%;
}

.loadContent {
  width: 120px;
  height: 30px;
  line-height: 30px;
  font-size: 16px;
  margin: 0 auto;
  color: aliceblue;
  cursor: pointer;
  background: rgba(0, 0, 0, 0.8);
}

.load-container {
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;

  .container {
    width: 50px;
    height: 60px;
    text-align: center;
    font-size: 10px;

    .boxLoading {
      background-color: #00adb5;
      height: 100%;
      width: 6px;
      display: inline-block;

      -webkit-animation: stretch_delay 1.2s infinite ease-in-out;
      animation: stretch_delay 1.2s infinite ease-in-out;
    }

    .boxLoading2 {
      -webkit-animation-delay: -1.1s;
      animation-delay: -1.1s;
    }

    .boxLoading3 {
      -webkit-animation-delay: -1.0s;
      animation-delay: -1.0s;
    }

    .boxLoading4 {
      -webkit-animation-delay: -0.9s;
      animation-delay: -0.9s;
    }

    .boxLoading5 {
      -webkit-animation-delay: -0.8s;
      animation-delay: -0.8s;
    }

  }
}

@-webkit-keyframes stretch_delay {
  0%, 40%, 100% {
    -webkit-transform: scaleY(0.4)
  }
  20% {
    -webkit-transform: scaleY(1.0)
  }
}

@keyframes stretch_delay {
  0%, 40%, 100% {
    transform: scaleY(0.4);
    -webkit-transform: scaleY(0.4);
  }
  20% {
    transform: scaleY(1.0);
    -webkit-transform: scaleY(1.0);
  }
}

.iconfont {
  font-size: 15px;
  margin-right: 2px;
}

.top-left-text {
  z-index: 10;
  font-size: 22px;
  width: 50px;
  height: 50px;
  color: white;
  font-weight: bold;
  margin: auto;
  text-align: center;
  position: absolute;
  background: #7b859a;
  line-height: 50px;
}
</style>
