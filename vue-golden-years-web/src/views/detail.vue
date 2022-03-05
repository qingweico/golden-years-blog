<template>
  <article>
    <el-dialog :visible.sync="dialogPictureVisible" fullscreen>
      <img :src="dialogImageUrl" alt="dialogImageUrl" style="margin: 0 auto;"/>
    </el-dialog>
    <h1 class="t_nav">
      <a href="/" class="n1">首页</a>
      <a href="javascript:void(0);"
         v-if="articleDetail.categoryId"
         @click="goToCategoryList(articleDetail.categoryId)"
         class="n2"
      >{{ articleDetail.categoryId ? getBlogCategoryNameById(articleDetail.categoryId) : "" }}</a>
    </h1>
    <div class="infos_box">
      <div class="news_view">
        <h3 class="news_title" v-if="articleDetail.title">{{ articleDetail.title }}</h3>
        <div class="blog_info" v-if="true">
          <ul>
            <li class="author">
              <span class="iconfont">&#xe60f;</span>
              <a href="javascript:void(0);" @click="goToAuthor(articleDetail.authorId)">{{
                  articleDetail.authorName
                }}</a>
            </li>
            <li class="category">
              <span class="iconfont">&#xe603;</span>
              <a
                  href="javascript:void(0);"
                  @click="goToSortList(articleDetail.categoryId)"
              >{{ articleDetail.categoryId ? getBlogCategoryNameById(articleDetail.categoryId) : "" }}</a>
            </li>
            <li class="createTime">
              <span class="iconfont">&#xe606;</span>
              {{ articleDetail.createTime }}
            </li>
            <li class="view">
              <span class="iconfont">&#xe8c7;</span>
              {{ articleDetail.readCounts }}
            </li>
            <li class="like">
              <span class="iconfont">&#xe663;</span>
              {{ articleDetail.collectCounts }}
            </li>
          </ul>
        </div>
        <div
            class="news_con ck-content"
            v-html="blogContent"
            v-highlight
            @click="imageChange"
        >{{ blogContent }}
        </div>
      </div>
      <div class="news_pl">
        <h2>文章评论</h2>
        <ul>
          <CommentBox
              :userInfo="userInfo"
              :commentInfo="commentInfo"
              @submit-box="submitBox"
              :showCancel="showCancel"
          ></CommentBox>

          <div class="message_infos">
            <CommentList :comments="comments" :commentInfo="commentInfo"></CommentList>
            <el-empty description="还没有评论，快来抢沙发吧!"  v-if="comments.length === 0"></el-empty>
          </div>
        </ul>
      </div>
    </div>
    <div class="sidebar2" v-if="showSidebar">
      <side-catalog
          :class="vueCategory"
          v-bind="catalogProps">
      </side-catalog>
    </div>
  </article>
</template>
<script>
import {getBlogById} from "@/api/blog";
import CommentList from "../components/CommentList";
import CommentBox from "../components/CommentBox";
import {getCommentList} from "@/api/comment";
import {Loading} from "element-ui";
import Sticky from "@/components/Sticky";
import SideCatalog from '@/components/VueSideCatalog'
import {getBlogCategory} from "@/api";

export default {
  name: "detail",
  data() {
    return {
      // 目录列表数
      catalogSum: 0,
      showStickyTop: false,
      showSideCatalog: true,
      // 是否显示侧边栏
      showSidebar: true,
      blogContent: "",
      articleCategoryList: [],
      catalogProps: {
        // 内容容器selector(必需)
        container: '.ck-content',
        watch: true,
        levelList: ["h2", "h3"],
      },
      // loading对象
      loadingInstance: null,
      showCancel: false,
      submitting: false,
      comments: [],
      commentInfo: {
        blogId: this.$route.query.blogId
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      toInfo: {},
      userInfo: {},
      blogId: null,
      articleDetail: {},
      dialogPictureVisible: false,
      dialogImageUrl: "",
    };
  },
  computed: {
    vueCategory: function () {
      if (!this.showStickyTop && this.showSideCatalog) {
        return 'catalog'
      }
      if (!this.showStickyTop && !this.showSideCatalog) {
        return 'catalog'
      }
      if (this.showStickyTop && this.showSideCatalog) {
        return 'catalog3'
      }
      if (this.showStickyTop && !this.showSideCatalog) {
        return 'catalog2'
      }
    },
  },
  components: {
    CommentList,
    CommentBox,
    SideCatalog,
    Sticky
  },
  watch: {
    $route(to, from) {
      location.reload()
    }
  },
  mounted() {
    let that = this;
    let params = new URLSearchParams();
    if (this.blogId) {
      params.append("articleId", this.blogId);
    }
    getBlogById(params).then(response => {
      console.log(response.data);
      if (response.data.success) {
        this.articleDetail = response.data.data;
        this.blogId = this.articleDetail.id
        document.title = this.articleDetail.title
       // this.getCommentList();
      }
      setTimeout(() => {
        that.blogContent = this.articleDetail.content
        that.loadingInstance.close();
      }, 200)
    });

    let after = 0;
    let offset = 110;
    $(window).scroll(function () {
      let docHeight = $(document).height();
      let winHeight = $(window).height();
      let winScrollHeight = $(window).scrollTop();
      that.showStickyTop = winScrollHeight >= offset;
      that.showSideCatalog = winScrollHeight > after;
      after = winScrollHeight;
      if (docHeight === winHeight + winScrollHeight) {
        if (that.comments.length >= that.total) {
          return;
        }
        let params = {};
        params.source = that.commentInfo.source;
        params.blogUid = that.commentInfo.blogUid;
        params.currentPage = that.currentPage + 1
        params.pageSize = that.pageSize;
        // getCommentList(params).then(response => {
        //   if (response.data.success) {
        //     that.comments = that.comments.concat(response.data.records);
        //     that.setCommentList(that.comments);
        //     that.currentPage = response.data.current;
        //     that.pageSize = response.data.size;
        //     that.total = response.data.total;
        //   }
        // });
      }
    })

    // 屏幕自适应
    window.onresize = () => {
      return (() => {
        // 屏幕大于950px的时候, 显示侧边栏
        that.showSidebar = document.body.clientWidth > 950
      })()
    }
  },
  created() {
    this.loadingInstance = Loading.service({
      fullscreen: true,
      text: "正在努力加载中~"
    });
    this.blogId = this.$route.query.blogId;
    // 屏幕大于950px的时候, 显示侧边栏
    this.showSidebar = document.body.clientWidth > 950;
    getBlogCategory().then(res => {
      if (res.data.success) {
        this.articleCategoryList = res.data.data;
      } else {
        this.$message.error(res.data.msg);
      }
    });
  },
  methods: {
    handleCurrentChange: function (val) {
      this.currentPage = val;
      this.getCommentList();
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.articleCategoryList;
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].name;
        }
      }
    },
    goToCategoryList() {
    },
    goToAuthor() {
    },
    submitBox(e) {
      let params = {};
      params.blogUid = e.blogUid;
      params.source = e.source;
      params.userUid = e.userUid;
      params.content = e.content;
      params.blogUid = e.blogUid;
      addComment(params).then(response => {
        if (response.data.status) {
          this.$notify({
            title: "成功",
            message: "评论成功",
            type: "success",
            offset: 100
          });
        } else {
          this.$notify.error({
            title: "错误",
            message: response.data,
            offset: 100
          });
        }
        this.getCommentList();
      });
    },
    // getCommentList: function () {
    //   let params = {};
    //   params.source = this.commentInfo.source;
    //   params.blogUid = this.commentInfo.blogUid;
    //   params.currentPage = this.currentPage;
    //   params.pageSize = this.pageSize;
    //   getCommentList(params).then(response => {
    //     if (response.data.success) {
    //       // this.comments = response.data.records;
    //       // this.setCommentList(this.comments);
    //       // this.currentPage = response.data.current;
    //       // this.pageSize = response.data.size;
    //       // this.total = response.data.total
    //     }
    //   });
    // },
    imageChange: function (e) {
      let type = e.target.localName;
      if (type === "img") {
        this.dialogPictureVisible = true;
        this.dialogImageUrl = e.target.currentSrc;
      }
    },
    // 切割字符串
    subText: function (str, index) {
      if (str.length < index) {
        return str;
      }
      return str.substring(0, index) + "...";
    }
  }
};
</script>
<style>
.emoji-panel-wrap {
  box-sizing: border-box;
  border: 1px solid #cccccc;
  border-radius: 5px;
  background-color: #ffffff;
  width: 470px;
  height: 190px;
  position: absolute;
  z-index: 999;
  top: 10px;
}

.emoji-size-small {
  zoom: 0.3;
  margin: 5px;
  vertical-align: middle;
}

.emoji-size-large {
  zoom: 0.5;
  margin: 5px;
}

.iconfont {
  font-size: 14px;
  margin-right: 3px;
}

.message_infos {
  width: 96%;
  margin-left: 10px;
}

.noComment {
  width: 100%;
  text-align: center;
}

.catalog {
  position: fixed;
  margin-left: 20px;
  /*max-height: 700px*/
}

.catalog2 {
  position: fixed;
  margin-left: 20px;
  top: 70px;
}

.catalog3 {
  position: fixed;
  margin-left: 20px;
  top: 20px;
}

.line-style {
  display: inline-block;
  height: 20px;
  width: 3px;
  background: transparent;
}

.line-style--active {
  background: currentColor;
}
</style>
