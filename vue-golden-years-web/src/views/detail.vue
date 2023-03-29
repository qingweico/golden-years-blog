<template>
  <article>
    <el-dialog :visible.sync="dialogPictureVisible" fullscreen>
      <img :src="dialogImageUrl" alt="dialogImageUrl" style="margin: 0 auto;"/>
    </el-dialog>
    <h1 class="t_nav">
      <span>人生碌碌，竞短论长，却不道荣枯有数，得失难量。 ——沈复《浮生六记》</span>
      <a href="/" class="n1">首页</a>
      <a href="javascript:void(0);"
         v-if="articleDetail.categoryId"
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
                  href="javascript:void(0);">
                {{ articleDetail.categoryId ? getBlogCategoryNameById(articleDetail.categoryId) : "" }}</a>
            </li>

            <li class="createTime">
              <span class="iconfont">&#xe606;</span>
              {{ articleDetail.createTime }}
            </li>
            <li class="view">
              <span class="iconfont">&#xe8c7;</span>
              {{ articleDetail.readCounts }}
            </li>
            <li class="comment">
              <span class="iconfont">&#xe891;</span>
              {{ articleDetail.commentCounts }}
            </li>
          </ul>
        </div>
        <div class="tags">
          <el-tag
              :key="tag.id"
              v-for="tag in articleDetail.tagList"
              :hit="false"
              :color="tag.color"
              href="javascript:void(0);"
              target="_blank"
              effect="plain" style="color: white;margin-right: 10px;cursor: pointer"
              :disable-transitions="false">
            {{ tag.name }}
          </el-tag>
        </div>
        <div
            class="news_con ck-content"
            v-html="blogContent"
            v-highlight
            @click="imageChange">
        </div>
        <!--点赞和收藏 -->
        <LikeAndCollect :praiseCount="articleDetail.starCounts"
                        :articleCollectCounts="articleDetail.collectCounts"
                        :articleId="articleId"
                        @updateStarCounts="updateStarCounts"
                        @updateCollectCounts="updateCollectCounts"
                        :isStar="isStar"
                        :isCollect="isCollect"
                        @isCollectThisArticle="isCollectThisArticle"
                        @isStarThisArticle="isStarThisArticle"></LikeAndCollect>
        <!--分享-->
        <div class="left-social">
          <div class="social-line" @click="shareWeiBo">
            <img src="@/assets/social/weibo.png" class="social-pic" alt/>
          </div>
          <div class="social-line" @click="shareDouBan">
            <img src="@/assets/social/douban.png" class="social-pic" alt/>
          </div>
          <div class="social-line" @click="shareQQ">
            <img src="@/assets/social/qq.png" class="social-pic" alt/>
          </div>
          <div class="social-line" @click="shareQZone">
            <img src="@/assets/social/qzone.png" class="social-pic" alt/>
          </div>
          <div class="social-line" @click="shareWeChat2">
            <div class="social-share" data-wechat-qrcode-title="请打开微信扫一扫"
                 data-disabled="google,twitter,facebook,douban,qzone,qq,weibo,tencent,linkedin,diandian">
              <img src="@/assets/social/wechat.png" class="social-pic" alt/>
            </div>
          </div>
        </div>
      </div>

      <!--登录框-->
      <LoginBox v-if="showLogin" @closeLoginBox="closeLoginBox"></LoginBox>
      <div class="news_pl">
        <Comment
            v-model="data"
            :user="currentUser"
            :before-submit="submit"
            :before-like="like"
            :before-delete="deleteComment"
            :upload-img="uploadImg"
            :props="props"
        />
        <el-empty description="还没有评论,快来抢沙发吧" v-if="!data.length"></el-empty>
      </div>

      <!--评论分页-->
      <div class="block paged" v-if="data.length">
        <el-pagination
            @current-change="handleCurrentChange"
            :current-page.sync="currentPage"
            :page-size="pageSize"
            layout="total, prev, pager, next, jumper"
            :total="records">
        </el-pagination>
      </div>
    </div>

    <!-- 右侧导航栏 -->
    <div class="sidebar2" v-if="showSidebar">
      <side-catalog
          :class="vueCategory"
          v-bind="catalogProps">
      </side-catalog>
    </div>

  </article>
</template>
<script>
import {
  getArticleCollectCounts,
  getArticleStarCounts,
  getBlogById, incArticleHistory, incPagViews, isCollectThisArticle,
  isStarThisArticle,
  readArticle
} from "@/api/detail";
import {getBlogCategory} from "@/api";
import {Loading} from "element-ui";
import Sticky from "@/components/Sticky";
import SideCatalog from '@/components/VueSideCatalog'
import {getCommentList, publishComment} from "@/api/comment";
import Comment from 'vue-juejin-comment'
import {mapGetters, mapMutations} from "vuex";
import LoginBox from "@/components/LoginBox";
import LikeAndCollect from "@/components/LikeAndCollect";
import {getSystemConfig} from "@/api/center";

export default {
  name: "detail",
  data() {
    return {
      // 评论列表
      data: [],
      props: {
        id: 'commentId',
        content: 'content',
        imgSrc: 'imgSrc',
        children: 'childrenComments',
        likes: 'likes',
        liked: 'liked',
        reply: 'reply',
        createAt: 'createAt',
        user: 'visitor',
      },
      currentUser: {
        name: "",
        avatar: "",
        author: false
      },
      showLogin: false,
      // 判断用户是否点赞该文章
      isStar: false,
      // 判断用户是否收藏该文章
      isCollect: false,
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
      articleId: this.$route.query.id,
      currentPage: 1,
      pageSize: 10,
      totalPages: 0,
      records: 0,
      articleDetail: {},
      dialogPictureVisible: false,
      dialogImageUrl: "",
      sysConfig: {}
    };
  },
  computed: {
    ...mapGetters(['getUserPhoto', 'getUserInfo']),
    vueCategory() {
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
    SideCatalog,
    Sticky,
    Comment,
    LoginBox,
    LikeAndCollect
  },
  watch: {
    $route(to, from) {
      location.reload()
    }
  },
  mounted() {
    let that = this;
    let params = new URLSearchParams();
    if (this.articleId) {
      params.append("articleId", this.articleId);
    }
    getBlogById(params).then(response => {
      this.articleDetail = response.data;
      document.title = this.articleDetail.title;
      this.getCommentList();
      setTimeout(() => {
        that.blogContent = this.articleDetail.content;
        that.isStarThisArticle();
        that.isCollectThisArticle();
        that.loadingInstance.close();
        let params = {};
        params.articleId = this.articleId;
        // 假如用户登录则添加文章浏览历史 否则不做该操作
        if (this.getUserInfo.id) {
          params.userId = this.getUserInfo.id;
          incArticleHistory(params);
        }
        let isLogin = this.$store.state.user.isLogin;
        if (!isLogin) {
          that.currentUser.avatar = that.$SysConf.defaultAvatar;
        } else {
          that.currentUser.avatar = that.getUserInfo.face;
        }
        that.currentUser.name = that.getUserInfo.nickname;
        that.currentUser.author = that.getUserInfo.id === that.articleDetail.authorId;
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
        /**无限加载action**/

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
    getSystemConfig().then((response) => {
      this.sysConfig = response.data;
    });
    // 文章阅读数累加
    incPagViews(this.articleId);
    this.getCommentList();
    this.loadingInstance = Loading.service({
      fullscreen: true,
      text: "正在努力加载中"
    });
    // 屏幕大于950px的时候, 显示侧边栏
    this.showSidebar = document.body.clientWidth > 950;
    getBlogCategory().then(res => {
      this.articleCategoryList = res.data;
    });
  },
  methods: {
    ...mapMutations(['setUserInfo', 'setLoginState']),
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getCommentList();
    },
    isStarThisArticle() {
      let params = {};
      params.articleId = this.articleId;
      params.userId = this.getUserInfo.id;
      isStarThisArticle(params).then((response) => {
        this.isStar = response.data;
      })
    },
    // 判断用户是否收藏过该文章
    isCollectThisArticle() {
      let params = {};
      params.articleId = this.articleId;
      params.userId = this.getUserInfo.id;
      isCollectThisArticle(params).then((response) => {
        this.isCollect = response.data;
      })
    },
    updateStarCounts() {
      let params = new URLSearchParams();
      params.append("articleId", this.articleId);
      getArticleStarCounts(params).then(response => {
        this.articleDetail.starCounts = Number(response.data);
      })
    },
    updateCollectCounts() {
      let params = new URLSearchParams();
      params.append("articleId", this.articleId);
      getArticleCollectCounts(params).then(response => {
        this.articleDetail.collectCounts = Number(response.data);
      })
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.articleCategoryList;
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].name;
        }
      }
    },
    goToAuthor(authorId) {
      let routeData = this.$router.resolve({
        path: "/homepage",
        query: {id: authorId}
      });
      window.open(routeData.href, '_self');
    },
    closeLoginBox() {
      this.showLogin = false;
    },

    // ###################### 评论 ######################
    async submit(newComment, parent, add) {
      // 判断用户是否登录
      let isLogin = this.$store.state.user.isLogin;
      if (!isLogin) {
        this.showLogin = true;
        return;
      }
      const res = await new Promise((resolve) => {
        setTimeout(() => {
          resolve({newComment, parent})
        }, 300)
      })

      add(Object.assign(res.newComment, {_id: new Date().getTime()}))
      let comments = res.newComment;
      let params = {};
      params.content = comments.content;
      params.imgSrc = comments.imgSrc;
      params.likes = comments.likes;
      params.reply = comments.reply;
      if (res.parent === null) {
        params.parent = 0;
      } else {
        params.parent = res.parent.id;
      }
      params.articleId = this.articleId;
      params.commentUserId = this.$store.state.user.userInfo.id;
      params.commentUserNickname = this.currentUser.name;
      params.commentUserFace = this.face;
      publishComment(params).then(response => {
        this.$notify({
          title: "成功",
          message: response.msg,
          type: "success",
        });
        this.getCommentList();
      });
    },
    async like(comment) {
      const res = await new Promise((resolve) => {
        setTimeout(() => {
          resolve(comment)
        }, 0)
      })

    }
    ,

    async uploadImg({file, callback}) {
      const res = await new Promise((resolve, reject) => {
        const reader = new FileReader()

        reader.readAsDataURL(file)

        reader.onload = () => {
          resolve(reader.result)
        }

        reader.onerror = () => {
          reject(reader.error)
        }
      })

      callback(res)
    },

    async deleteComment(comment, parent) {
      const res = await new Promise((resolve) => {
        setTimeout(() => {
          resolve({comment, parent})
        }, 300)
      })
    },
    getCommentList() {
      let params = new URLSearchParams();
      params.append("articleId", this.articleId);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getCommentList(params).then(response => {
        let content = response.data;
        this.currentPage = content.currentPage;
        this.records = content.records;
        this.data = content.rows;
        for (let item of this.data) {
          if (item.reply.name === null) {
            Reflect.deleteProperty(item, "reply");
          }
        }
      })
    }
    ,
    imageChange(e) {
      let type = e.target.localName;
      if (type === "img") {
        this.dialogPictureVisible = true;
        this.dialogImageUrl = e.target.currentSrc;
      }
    }
    ,
    // 切割字符串
    subText(str, index) {
      if (str.length < index) {
        return str;
      }
      return str.substring(0, index) + "...";
    },

    // #######################  社交分享 #######################
    shareWeiBo() {
      let weiBoShareUrl = 'http://v.t.sina.com.cn/share/share.php';
      // 参数url设置分享的内容链接|默认当前页location, 可选参数
      weiBoShareUrl += '?url=' + encodeURIComponent(document.location);
      // 参数title设置分享的标题|默认当前页标题, 可选参数
      weiBoShareUrl += '&title=' + encodeURIComponent(document.title);
      weiBoShareUrl += '&source=' + encodeURIComponent('');
      weiBoShareUrl += '&sourceUrl=' + encodeURIComponent('');
      // 参数content设置页面编码gb2312|utf-8, 可选参数
      weiBoShareUrl += '&content=' + 'utf-8';
      // 参数pic设置图片链接|默认为空, 可选参数
      weiBoShareUrl += '&pic=' + encodeURIComponent('');
      window.open(weiBoShareUrl, '_blank');
    }
    ,
    shareDouBan() {
      let douBanShareUrl = 'http://shuo.douban.com/!service/share?';
      douBanShareUrl += 'href=' + encodeURIComponent(location.href);      //分享的链接
      douBanShareUrl += '&name=' + encodeURIComponent(document.title);    //分享的标题
      window.open(douBanShareUrl, '_blank');
    }
    ,
    shareQQ() {
      let qqShareUrl = 'https://connect.qq.com/widget/shareqq/iframe_index.html?';
      qqShareUrl += 'url=' + encodeURIComponent(location.href);           //分享的链接
      qqShareUrl += '&title=' + encodeURIComponent(document.title);       //分享的标题
      window.open(qqShareUrl, '_blank');
    }
    ,
    shareQZone() {
      let qzoneShareUrl = 'https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?';
      qzoneShareUrl += 'url=' + encodeURIComponent(document.location);
      qzoneShareUrl += '&title=' + encodeURIComponent(document.title);
      window.open(qzoneShareUrl, '_blank');
    }
    ,
    shareWeChat2() {
      let target_url = 'http://zixuephp.net/inc/qrcode_img.php?url=' + document.location.href;
      target_url = 'http://zixuephp.net/inc/qrcode_img.php?url=http://www.itzixi.com';
      window.open(target_url, 'weixin', 'height=320, width=320');
    }
    ,
  }
};
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
.news_view {
  background-image: linear-gradient(90deg, rgba(50, 0, 0, .05) 3%, transparent 0), linear-gradient(1turn, rgba(50, 0, 0, .05) 3%, transparent 0);
  background-size: 20px 20px;
  background-position: 50%;
  padding-bottom: 10px;
}

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

/**分享**/
.left-social {
  float: right;
  display: flex;
  margin-bottom: 20px;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.share-title {
  flex-wrap: nowrap;
  margin-bottom: 18px;
  align-self: center;
}

.social-line {
  margin-top: 6px;
  display: flex;
  flex-direction: row;
  margin-right: 10px;
}

.social-pic {
  width: 38px;
  height: 38px;
}
</style>
