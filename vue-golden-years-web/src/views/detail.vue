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
                  @click="goToCategoryList(articleDetail.categoryId)">
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
            <li class="like">
              <span class="iconfont">&#xe663;</span>
              {{ articleDetail.collectCounts }}
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
              @click="goToTagList(tag.id)"
              target="_blank"
              effect="plain" style="color: white;margin-right: 10px"
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
      </div>

      <!-- 分享点赞和收藏 -->
      <div class="left-social">
        <div class="share-title">
          <span class="share-words">分享</span>
        </div>
        <div class="back-line"></div>
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
import {getBlogById} from "@/api";
import {getBlogCategory} from "@/api";
import {Loading} from "element-ui";
import Sticky from "@/components/Sticky";
import SideCatalog from '@/components/VueSideCatalog'
import {getCommentList, publishComment} from "@/api/comment";
import Comment from 'vue-juejin-comment'
import {mapGetters} from "vuex";
import { EXAMPLE_DATA } from '@/data'
export default {
  name: "detail",
  data() {
    const users = [
      {
        name: 'Up&Up',
        avatar: require('../assets/image/avatar1.jpg'),
        author: true,
      },
      {
        name: '我叫白云',
        avatar: require('../assets/image/comment.png'),
      },
      {
        name: '我叫黑土',
        avatar: require('../assets/image/avatar3.jpg'),
      },
      {
        name: 'NARUTO',
        avatar: require('../assets/image/avatar2.jpg'),
      },
    ]
    return {
      // 评论列表
      data: [],
      props: {
        id: '_id',
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
        name: '',
        avatar: '',
        author: true,
      },
      users,
      wrapStyle: '',

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
      articleId: this.$route.query.id,
      currentPage: 1,
      pageSize: 10,
      totalPages: 0,
      records: 0,
      toInfo: {},
      userInfo: this.$store.state.user.userInfo,
      blogId: null,
      articleDetail: {},
      dialogPictureVisible: false,
      dialogImageUrl: "",


      defaultAvatar: this.$SysConf.defaultAvatar,
    };
  },
  computed: {
    ...mapGetters(['getUserPhoto']),
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
    Comment
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
      this.articleDetail = response.data;
      document.title = this.articleDetail.title
      this.getCommentList();
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
        // if (that.comments.length >= that.records) {
        //   /**无限加载action**/
        // }
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
    this.addData(1);
    this.getCommentList();
    this.loadingInstance = Loading.service({
      fullscreen: true,
      text: "正在努力加载中"
    });
    this.blogId = this.$route.query.id;
    // 屏幕大于950px的时候, 显示侧边栏
    this.showSidebar = document.body.clientWidth > 950;
    getBlogCategory().then(res => {
      this.articleCategoryList = res.data;
    });
  },
  methods: {
    handleCurrentChange(val) {
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
    goToCategoryList(categoryId) {
      this.$router.push({path: "/", query: {categoryId: categoryId}});
    },
    goToTagList() {
    },
    goToAuthor() {
    },

    // // ###################### 评论 ######################
    // addComment(comment, parent, add) {
    //   let newComment = {};
    //   let info = this.userInfo;
    //   let isLogin = this.$store.state.user.isLogin
    //   if (!isLogin) {
    //     this.$notify.info({
    //       title: '提示',
    //       message: '登录后才可以评论',
    //     });
    //     return;
    //   }
    //   console.log(comment)
    //   let params = {};
    //   params.content = comment.content;
    //   params.reply = comment.reply;
    //   params.articleId = this.articleId;
    //   params.fatherId = 0;
    //   params.commentUserId = info.id;
    //   publishComment(params).then(response => {
    //     this.$notify({
    //       title: "成功",
    //       message: "评论成功",
    //       type: "success",
    //     });
    //     this.getCommentList();
    //   });
    //   //需调用 add 函数, 并传入 newComment 对象
    //   add(newComment)
    // },



    //
    // deleteComment(comment, parent) {
    //   // ...
    // },
    // likeComment(comment) {
    //   // ...
    // },
    // uploadOrCopyImg({file, callback}) {
    //   // ...
    //   let imgUrl = {};
    //   callback(imgUrl) // 图片地址必传
    // },
    async submit(newComment, parent, add) {
      const res = await new Promise((resolve) => {
        setTimeout(() => {
          resolve({ newComment, parent })
        }, 300)
      })

      add(Object.assign(res.newComment, { _id: new Date().getTime() }))

      console.log('addComment: ', res)
    },

    async like(comment) {
      const res = await new Promise((resolve) => {
        setTimeout(() => {
          resolve(comment)
        }, 0)
      })

      console.log('likeComment: ', res)
    },

    async uploadImg({ file, callback }) {
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
      console.log('uploadImg： ', res)
    },

    async deleteComment(comment, parent) {
      const res = await new Promise((resolve) => {
        setTimeout(() => {
          resolve({ comment, parent })
        }, 300)
      })

      console.log('deleteComment: ', res)
    },

    changeUser() {
      const users = this.users
      const index = users.findIndex((c) => c.name === this.currentUser.name)

      this.currentUser = users[index === users.length - 1 ? 0 : index + 1]
      this.$refs.comment.scrollTo({ left: 0, top: 0, behavior: 'smooth' })
    },

    addData(times) {
      setTimeout(() => {
        this.data = new Array(times).fill(EXAMPLE_DATA).flat(Infinity)
      }, 0)
    },

    getCommentList() {
      let params = new URLSearchParams();
      params.append("articleId", this.articleId);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getCommentList(params).then(response => {
        let content = response.data;
        this.currentPage = content.currentPage;
        this.totalPages = content.totalPages;
        this.records = content.records;
      })
    },
    imageChange(e) {
      let type = e.target.localName;
      if (type === "img") {
        this.dialogPictureVisible = true;
        this.dialogImageUrl = e.target.currentSrc;
      }
    },
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
    },
    shareDouBan() {
      let douBanShareUrl = 'http://shuo.douban.com/!service/share?';
      douBanShareUrl += 'href=' + encodeURIComponent(location.href);      //分享的链接
      douBanShareUrl += '&name=' + encodeURIComponent(document.title);    //分享的标题
      window.open(douBanShareUrl, '_blank');
    },
    shareQQ() {
      let qqShareUrl = 'https://connect.qq.com/widget/shareqq/iframe_index.html?';
      qqShareUrl += 'url=' + encodeURIComponent(location.href);           //分享的链接
      qqShareUrl += '&title=' + encodeURIComponent(document.title);       //分享的标题
      window.open(qqShareUrl, '_blank');
    },
    shareQZone() {
      let qzoneShareUrl = 'https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?';
      qzoneShareUrl += 'url=' + encodeURIComponent(document.location);
      qzoneShareUrl += '&title=' + encodeURIComponent(document.title);
      window.open(qzoneShareUrl, '_blank');
    },
    shareWeChat2() {
      let target_url = 'http://zixuephp.net/inc/qrcode_img.php?url=' + document.location.href;
      target_url = 'http://zixuephp.net/inc/qrcode_img.php?url=http://www.itzixi.com';
      window.open(target_url, 'weixin', 'height=320, width=320');
    },
  }
};
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
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
  width: 420px;
  margin-bottom: 40px;
  flex-direction: row;
  justify-content: center;
  align-items: center
}

.share-title {
  flex-wrap: nowrap;
  margin-bottom: 18px;
  align-self: center;
}

.share-words {
  display: inline-block;
  padding: 0 10px;
  width: 50px;
}

.back-line {
  border-top: 1px solid white;
  position: relative;
  top: -27px;
  z-index: -2;
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

.social-words {
  vertical-align: middle;
  padding-left: 12px;
  font-size: 16px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
</style>