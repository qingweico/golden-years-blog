<template>
  <article>
    <div class="blank"></div>
    <!--blog context begin-->
    <div class="blogs_box">
      <div
          v-for="item in newBlogData"
          :key="item.id"
          class="blogs"
          data-scroll-reveal="enter bottom over 1s"
      >
        <h3 class="blog_title">
          <a href="javascript:void(0);" @click="goToDetail(item)">{{ item.title }}</a>
        </h3>

        <span class="blog_pic">
          <a href="javascript:void(0);" @click="goToDetail(item)" title>
            <img v-if="item.articleCover" :src="item.articleCover" alt>
          </a>
        </span>

        <p class="blog_summary">{{ item.summary }}</p>
        <div class="blog_info">
          <ul>

            <li class="author">
              <span class="iconfont">&#xe60f;</span>
              <a href="javascript:void(0);" @click="goToAuthor(item.author)">{{ item.author }}</a>
            </li>
            <li class="category_name" v-if="item.categoryId">
              <span class="iconfont">&#xe603;</span>
              <a href="javascript:void(0);"
                 @click="goToBlogListGroupByCategory(item.categoryId)"
              >{{ item.categoryId }}</a>
            </li>
            <li class="view">
              <span class="iconfont">&#xe8c7;</span>
              <span>{{ item.readCounts }}</span>
            </li>
            <li class="like">
              <span class="iconfont">&#xe663;</span>
              {{ item.readCounts }}
            </li>
            <li class="createTime">
              <span class="iconfont">&#xe606;</span>
              {{ item.createTime }}
            </li>
          </ul>
        </div>
      </div>

      <div class="isEnd">
        <div class="loadContent" @click="loadContent" v-if="!isEnd&&!loading&&!newBlogData">点击加载更多</div>
        <div class="lds-css ng-scope" v-if="!isEnd&&loading">
          <div style="width:100%;height:100%" class="lds-facebook">
            <div></div>
            <div></div>
            <div></div>
          </div>
        </div>
        <span v-if="isEnd">我也是有底线的</span>
      </div>
    </div>
    <!--blog context end-->
    <div class="sidebar">
    </div>
  </article>
</template>

<script>
import {Loading} from 'element-ui';
import {getNewBlog} from "@/api";

export default {
  name: "index",
  components: {},
  data() {
    return {
      loadingInstance: null,
      newBlogData: [],
      // 最新文章
      keyword: "",
      category: "",
      currentPage: 1,
      pageSize: 10,
      total: 0,
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
  created() {
    // 获取最新博客
   //  this.newBlogList();
  },
  methods: {
    // 跳转到文章详情
    goToDetail(blog) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {blogId: blog.id}
      })
      window.open(routeData.href, '_blank');
    },
    // 最新博客列表
    newBlogList() {
      let that = this;
      that.loadingInstance = Loading.service({
        lock: true,
        text: '正在努力加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      let params = new URLSearchParams();
      params.append("keyword", this.keyword);
      params.append("category", this.category);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getNewBlog(params).then(response => {
        if (response.data.success) {
          that.newBlogData = response.data.data.rows;
          that.total = response.data.data.records;
          that.currentPage = response.data.data.currentPage;
        }
        that.loadingInstance.close();
      }, function () {
        that.isEnd = true;
        that.loadingInstance.close();
      });
    },
    loadContent: function () {
      let that = this;
      that.loading = true;
      that.currentPage = that.currentPage + 1;
      let params = new URLSearchParams();
      params.append("keyword", this.keyword);
      params.append("category", this.category);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getNewBlog(params).then(response => {
        if (response.data.success && response.data.data.rows.length > 0) {
          that.isEnd = false;
          let newData = that.newBlogData.concat(response.data.data.rows);
          that.newBlogData = newData;
          that.total = response.data.data.records;
          that.currentPage = response.data.data.currentPage;
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
  goToAuthor() {
  },
  goToBlogListGroupByCategory() {
  }
};
</script>

<style>
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

@keyframes lds-facebook_1 {
  0% {
    top: 0;
    height: 200px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_1 {
  0% {
    top: 0;
    height: 200px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@keyframes lds-facebook_2 {
  0% {
    top: 20px;
    height: 160px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_2 {
  0% {
    top: 20px;
    height: 160px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@keyframes lds-facebook_3 {
  0% {
    top: 40px;
    height: 120px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_3 {
  0% {
    top: 40px;
    height: 120px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

.lds-facebook {
  position: relative;
}

.lds-facebook div {
  position: absolute;
  width: 20px;
}

.lds-facebook div:nth-child(1) {
  left: 40px;
  background: #1d0e0b;
  -webkit-animation: lds-facebook_1 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_1 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  -webkit-animation-delay: -0.2s;
  animation-delay: -0.2s;
}

.lds-facebook div:nth-child(2) {
  left: 90px;
  background: #774023;
  -webkit-animation: lds-facebook_2 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_2 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  -webkit-animation-delay: -0.1s;
  animation-delay: -0.1s;
}

.lds-facebook div:nth-child(3) {
  left: 140px;
  background: #d88c51;
  -webkit-animation: lds-facebook_3 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_3 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
}

.lds-facebook {
  width: 90px !important;
  height: 90px !important;
  -webkit-transform: translate(-45px, -45px) scale(0.45) translate(45px, 45px);
  transform: translate(-45px, -45px) scale(0.45) translate(45px, 45px);
}

.iconfont {
  font-size: 15px;
  margin-right: 2px;
}

</style>
