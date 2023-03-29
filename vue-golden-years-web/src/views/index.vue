<template>
  <article>
    <div class="blank"></div>

    <h1 class="t_nav" v-if="isSearchModel">
      <span>生活不是你活过的样子，而是你记住的样子。——麦家《人生海海》</span>
      <a href="/" class="n1">网站首页</a>
      <a href="/" class="n2" v-if="categoryId">{{ getBlogCategoryNameById(categoryId) }}</a>
      <a href="/" class="n2" v-if="tagId">{{ getBlogTagNameById(tagId) }}</a>
    </h1>

    <!--blog context begin-->
    <div class="blogs_box">
      <div
          v-for="item in newBlogData"
          :key="item.id"
          class="blogs"
          data-scroll-reveal="enter bottom over 1s">
        <h3 class="blog_title">
          <a href="javascript:void(0);" @click="goToDetail(item)" v-html="item.title"></a>
        </h3>

        <span class="blog_pic">
          <a href="javascript:void(0);" @click="goToDetail(item)" title>
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
                 @click="goToList(item.categoryId)"
              > {{ getBlogCategoryNameById(item.categoryId) }}</a>
            </li>
            <li class="view">
              <span class="iconfont">&#xe8c7;</span>
              <span>{{ item.readCounts }}</span>
            </li>
            <li class="start_count">
              <span class="iconfont">&#xf010d;</span>
              {{ item.starCounts }}
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
        <div class="loadContent" @click="loadContent" v-if="!isEnd&&!loading && currentPage < totalPage">点击加载更多</div>
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
        <span v-if="!loading && total && currentPage >= totalPage">我也是有底线的</span>
        <span v-if="!loading && total === 0">什么内容也没有</span>
      </div>
    </div>
    <!--blog context end-->
    <div class="sidebar">

      <!--类别云-->
      <CategoryCloud></CategoryCloud>
      <!--标签云-->
      <TagCloud></TagCloud>
      <!-- 友情链接-->
      <Link></Link>
    </div>
  </article>
</template>

<script>
import {Loading} from 'element-ui';
import {getBlogCategory, getHotTag, searchBlogByElasticSearch, searchBlogBySql} from "@/api";
import Link from "@/components/Link";
import CategoryCloud from "@/components/CategoryCloud";
import TagCloud from "@/components/TagCloud";
import {getSystemConfig} from "@/api/center";

export default {
  name: "index",
  components: {
    Link,
    TagCloud,
    CategoryCloud
  },
  data() {
    return {
      loadingInstance: null,
      // 初始化默认使用es
      searchModel: 0,
      isSearchModel: false,
      sysConfig: {},
      // 最新文章
      newBlogData: [],
      keyword: "",
      categoryId: "",
      tagId: "",
      categoryList: [],
      tagList: [],
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
    // 注册scroll事件并监听
   this.loading = false;
  },
  watch: {
    $route(to, from) {
      let searchModel = this.$route.query.searchModel;
      let keyword = this.$route.query.keyword;
      if (keyword !== undefined && keyword !== "") {
        this.keyword = keyword;
      }
      if (searchModel !== undefined && searchModel !== "") {
        this.searchModel = searchModel;
      }
      let categoryId = this.$route.query.categoryId;
      if (categoryId !== undefined && categoryId !== "") {
        // 暂不开启类别和标签同时搜索
        this.categoryId = categoryId;
        this.tagId = "";
      }
      let tagId = this.$route.query.tagId;
      if (tagId !== undefined && tagId !== "") {
        // 暂不开启类别和标签同时搜索
        this.tagId = tagId;
        this.categoryId = "";
      }
      if (this.keyword !== "" || this.categoryId !== "" || this.tagId !== "") {
        this.isSearchModel = true;
      }
      this.newBlogList();
    }
  },
  created() {
    getBlogCategory().then((response) => {
      this.categoryList = response.data;
    });
    getHotTag().then(response => {
      this.tagList = response.data;
    });
    getSystemConfig().then((response) => {
      this.sysConfig = response.data;
    });
    // 获取最新博客
    this.newBlogList();
  },
  methods: {
    // 跳转到文章详情
    goToDetail(blog) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: blog.id}
      })
      window.open(routeData.href, '_blank');
    },
    // 最新博客列表
    newBlogList() {
      let that = this;
      that.loading = true;
      that.currentPage = 1;
      that.loadingInstance = Loading.service({
        lock: true,
        text: '正在努力加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      let params = new URLSearchParams();
      params.append("keyword", this.keyword);
      params.append("category", this.categoryId);
      params.append("tag", this.tagId);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      if (Number(this.searchModel) === 1) {
        searchBlogByElasticSearch(params).then(response => {
          that.newBlogData = response.data.rows;
          that.total = response.data.records;
          that.totalPage = response.data.totalPage;
          that.loadingInstance.close();
          that.loading = false;
        }, () => {
          that.isEnd = true;
          that.loading = false;
          that.loadingInstance.close();
        });
      } else if (Number(this.searchModel) === 0) {
        searchBlogBySql(params).then(response => {
          that.newBlogData = response.data.rows;
          that.total = response.data.records;
          that.totalPage = response.data.totalPage;
          that.loadingInstance.close();
          that.loading = false;
        }, () => {
          that.isEnd = true;
          that.loading = false;
          that.loadingInstance.close();
        })
      }
    },
    loadContent() {
      let that = this;
      that.loading = true;
      that.currentPage = that.currentPage + 1;
      let params = new URLSearchParams();
      params.append("keyword", this.keyword);
      params.append("category", this.categoryId);
      params.append("tag", this.tagId);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      if (Number(this.searchModel) === 1) {
        searchBlogByElasticSearch(params).then(response => {
          if (response.data.rows.length > 0) {
            that.isEnd = false;
            let newData = that.newBlogData.concat(response.data.rows);
            that.newBlogData = newData;
            that.total = response.data.records;
            that.currentPage = response.data.currentPage;
            // 全部加载完毕
            if (newData.length < that.pageSize) {
              that.isEnd = true;
            }
          } else {
            that.isEnd = true;
          }
          that.loading = false;
        });
      } else if (Number(this.searchModel) === 0) {
        searchBlogBySql(params).then(response => {
          if (response.data.rows.length > 0) {
            that.isEnd = false;
            let newData = that.newBlogData.concat(response.data.rows);
            that.newBlogData = newData;
            that.total = response.data.records;
            that.currentPage = response.data.currentPage;
            // 全部加载完毕
            if (newData.length < that.pageSize) {
              that.isEnd = true;
            }
          } else {
            that.isEnd = true;
          }
          that.loading = false;
        });
      }
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.categoryList;
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].name;
        }
      }
    },
    getBlogTagNameById(tagId) {
      let tagList = this.tagList;
      for (let i = 0; i < tagList.length; i++) {
        if (tagId === tagList[i].id) {
          return tagList[i].name;
        }
      }
    },
    goToList(categoryId) {
      this.$router.push({
        path: "/",
        query: {categoryId: categoryId, searchModel: this.sysConfig.searchModel}
      });
    },
    goToAuthor(authorId) {
      let routeData = this.$router.resolve({
        path: "/homepage",
        query: {id: authorId}
      });
      window.open(routeData.href, '_self');
    }
  },

};
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

      -webkit-animation: stretchdelay 1.2s infinite ease-in-out;
      animation: stretchdelay 1.2s infinite ease-in-out;
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

@-webkit-keyframes stretchdelay {
  0%, 40%, 100% {
    -webkit-transform: scaleY(0.4)
  }
  20% {
    -webkit-transform: scaleY(1.0)
  }
}

@keyframes stretchdelay {
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

</style>
