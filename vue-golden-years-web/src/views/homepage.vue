<template>
  <!-- 左侧作者文章 -->
  <article>
    <!--blog context begin-->
    <div class="blogs_box">
      <div
          v-for="item in authorArticleData"
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
              <a href="javascript:void(0);">{{ item.authorVO.nickname }}</a>
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
      <!--分页-->
      <div class="block paged" v-show="total">
        <el-pagination
            @current-change="handleCurrentChange"
            :current-page.sync="page"
            :page-size="pageSize"
            layout="total, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
    </div>
    <!-- 右侧作者信息 -->
    <div class="attached-info">
      <div id="userInfo" class="user-info">
        <div class="my-info">
          <div class="my-face">
            <a target="_blank">
              <img :src="authorInfo.face" class="my-face-img" alt>
            </a>
          </div>
          <div class="author">{{ authorInfo.nickname }}</div>
          <div class="fans-info">
            <div class="my-subscribe">
              <div class="fans-info-num item-center">
                {{ authorInfo.myFollowCounts }}
              </div>
              <div class="fans-info-word item-center">关注</div>
            </div>
            <div class="middle-line"></div>
            <div class="my-fans">
              <div class="fans-info-num item-center">{{ authorInfo.myFansCounts }}
              </div>
              <div class="fans-info-word item-center">粉丝</div>
            </div>
          </div>
          <el-row :gutter="32">
            <el-col :span="12">
              <el-button v-if="!isFollowUser" @click="attentionOrNot">关注</el-button>
              <el-button v-else @click="attentionOrNot">已关注</el-button>
            </el-col>
            <el-col :span="12">
              <el-button @click="privateLetter">私信</el-button>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <!--登录框-->
    <LoginBox v-if="showLogin" @closeLoginBox="closeLoginBox"></LoginBox>

    <div class="sidebar">
      <!--用户热门文章-->
      <Rank/>
    </div>
  </article>
</template>

<script>
import {
  attention,
  cancelAttention,
  getAuthorArticleList,
  getAuthorInfo,
  hasFollowThisAuthorOrNot
} from "@/api/homepage";
import {getUrlVars} from '@/utils/util'
import {getBlogCategory} from "@/api";
import {mapGetters} from "vuex";
import LoginBox from "@/components/LoginBox";
import Rank from "@/components/HotBlog";

export default {
  name: "homepage",
  components: {
    LoginBox,
    Rank
  },
  data() {
    return {
      authorInfo: {},
      authorId: "",
      authorArticleData: [],
      goodArticleList: [],
      categoryList: [],
      isFollowUser: false,
      // 起始分页
      page: 1,
      // 分页每页显示数量
      pageSize: 5,
      // 总页数
      totalPage: 1,
      // 总记录数
      total: 0,
      showLogin: false
    }
  },
  computed: {
    ...mapGetters(['getUserInfo', 'getUserLoginState']),
  },
  created() {
    getBlogCategory().then(response => {
      this.categoryList = response.data;
    });
    this.authorId = getUrlVars().id;
    this.getUserInfo.id;
    this.getAuthorArticleList();
    this.queryUserInfo();
    this.hasFollowThisAuthorOrNot();
  },
  methods: {
    hasFollowThisAuthorOrNot() {
      let params = new URLSearchParams();
      params.append("userId", this.authorId)
      params.append("fanId", this.getUserInfo.id);
      hasFollowThisAuthorOrNot(params).then(response => {
        this.isFollowUser = response.data;
      })
    },
    getAuthorArticleList() {
      let params = new URLSearchParams();
      params.append("authorId", this.authorId);
      params.append("page", this.page);
      params.append("pageSize", this.pageSize);
      getAuthorArticleList(params).then(response => {
        let content = response.data;
        this.authorArticleData = content.rows;
        this.page = content.currentPage;
        this.totalPage = content.totalPage;
        this.total = content.records;
      })
    },
    attentionOrNot() {
      if (!this.getUserLoginState) {
        this.showLogin = true;
        return;
      }
      let data = {};
      data.authorId = this.authorId;
      data.fanId = this.getUserInfo.id;
      if (this.isFollowUser) {
        this.notYourFan(data);
      } else {
        this.beYourFan(data);
      }
    },
    privateLetter() {
    },
    // 成为粉丝, 关注作者
    beYourFan(data) {
      attention(data).then(response => {
        this.isFollowUser = true;
        this.queryUserInfo();
        this.$message.success(response.msg)
      })
    },
    // 取消关注
    notYourFan(data) {
      cancelAttention(data).then(response => {
        this.isFollowUser = false;
        this.queryUserInfo();
        this.$message.success(response.msg)
      })
    },
    // 查询作者信息
    queryUserInfo() {
      let params = new URLSearchParams();
      params.append("userId", this.authorId);
      getAuthorInfo(params).then(response => {
        this.authorInfo = response.data;
      })

    },
    handleCurrentChange(val) {
      this.page = val;
      this.getAuthorArticleList();
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.categoryList;
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].name;
        }
      }
    },
    closeLoginBox() {
      this.showLogin = false;
    },
    // 跳转到文章详情
    goToDetail(blog) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: blog.id}
      })
      window.open(routeData.href, '_blank');
    },
  }
}
</script>

<style scoped>
body {
  width: 100%;
  height: 100%;
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  color: #657181;
  background-color: #fff;
  -webkit-font-smoothing: antialiased;
}

.middle-line {
  display: block;
  background: #e8e8e8;
  height: 46px;
  width: 1px;
  left: 100%;
  top: 18px;
}

.attached-info {
  width: 32%;
  float: right;
  margin-bottom: 20px;
}

.user-info {
  padding: 20px 20px;
  background-color: #f4f5f5;
  border-radius: 3px;
}

.my-info {
  background-color: #fff;
  border-radius: 3px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid #f0eded;
}

.my-face {
  align-self: center;
}

.my-face-img {
  width: 55px;
  height: 55px;
  border-radius: 50%;
}

.author {
  color: #222;
  font-size: 16px;
}

.fans-info {
  margin-top: 5px;
  width: 250px;
  display: flex;
  flex-direction: row;
  justify-content: space-around;
}

.my-subscribe {
  width: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.item-center {
  align-self: center;
}

.my-fans {
  width: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.fans-info-num {
  color: #222;
  font-size: 20px;
  margin-bottom: 3px;
  font-weight: 600;
  line-height: 22px;
}

.middle-line {
  width: 1px;
  height: 64px;
  transform: scale(.5);
  background-color: rgb(214, 209, 209);
}

.fans-info-word {
  font-size: 15px;
  color: #777;
}
</style>