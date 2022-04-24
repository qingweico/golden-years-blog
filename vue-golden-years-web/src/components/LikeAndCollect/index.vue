<template>
  <div class="praise_collect">
    <p class="digit" @click="praiseBlog(articleId)">
      <span class="iconfont" v-if="!isStar">&#xf010d;</span>
      <a href="javascript:void(0);" v-if="!isStar">点赞</a>
      <span class="iconfont" v-if="isStar">&#xe622;</span>
      <a href="javascript:void(0);" v-if="isStar">已点赞</a>
      <span>
        (<b id="praiseNum">{{ praiseCount }}</b>)
      </span>
    </p>
    <p class="collect" @click="choiceFavorites">
      <span class="iconfont" v-if="!isCollect">&#xe602;</span>
      <a href="javascript:void(0)" v-if="!isCollect">收藏本文</a>
      <span class="iconfont" v-if="isCollect">&#xe661;</span>
      <a href="javascript:void(0)" v-if="isCollect">已收藏</a>
      <span>
        (<b id="collectNum">{{ articleCollectCounts }}</b>)
      </span>
    </p>

    <!--创建收藏夹-->
    <el-dialog title="创建新收藏夹" :visible.sync="dialogCreateFavorites"
               :center=true
               width="30%"
               :before-close="closeCreateFavorites">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item prop="name">
          <el-input v-model="form.name" @input="isChange = true" placeholder="收藏夹名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.description" @input="isChange = true" placeholder="收藏描述(可选)"
                    :autosize="{ minRows: 3, maxRows: 10}"
                    type="textarea"></el-input>
        </el-form-item>
        <el-form-item>
          <el-radio v-model="form.open" :label=1>公开</el-radio>
          <el-radio v-model="form.open" :label=0>私密</el-radio>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-row>
          <el-col :span="12">
            <el-button @click="handleCancel" style="width: 90%">返回</el-button>
          </el-col>
          <el-col :span="12">
            <el-button type="primary" @click="submitForm" style="width: 90%" :disabled="!form.name">确定创建</el-button>
          </el-col>
        </el-row>


      </div>
    </el-dialog>
    <!--收藏文章-->
    <el-dialog title="添加收藏" :visible.sync="dialogChoiceFavorites"
               :center=true width="30%"
               :before-close="closeChoiceFavorites">
      <ul class="favorites" v-for="item in favorites">
        <li>
          <el-row>
            <el-col :span="6">
              <div class="favoritesName">{{ item.name }} <i class="el-icon-lock" v-if="item.open === 0"></i>
                <i class="el-icon-unlock" v-if="item.open === 1"></i></div>
              <div class="collectCount">
                {{ item.favoritesCollectCount }} 条内容
              </div>
            </el-col>
            <el-col :span="6" :offset="12">
              <el-button plain @click="collect(item.id, item.isFavoritesCollect)" style="width: 80px"
                         v-if="!item.isFavoritesCollect">收藏
              </el-button>
              <el-button type="info" @click="collect(item.id, item.isFavoritesCollect)" style="width: 80px" v-else>已收藏
              </el-button>
            </el-col>
          </el-row>
        </li>
        <el-divider></el-divider>
      </ul>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" style="width:200px" @click="createFavorites">创建收藏夹</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {mapGetters} from "vuex";
import {
  cancelCollect,
  collect,
  getFavorites,
  isCollectThisArticle,
  cancelStar,
  praiseArticleById,
  createFavorites
} from "@/api/detail";

export default {
  props: {
    praiseCount: {
      type: Number,
      default: 0
    },
    articleCollectCounts: {
      type: Number,
      default: 0
    },
    articleId: {
      type: String
    },
    isStar: {
      type: Boolean
    },
    isCollect: {
      type: Boolean
    }
  },
  data() {
    return {
      // 我的收藏夹
      favorites: [],
      // 判断用户每个收藏夹是否收藏指定文章
      isFavoritesCollect: false,
      // 每个收藏夹收藏数
      favoritesCollectCount: 0,
      dialogCreateFavorites: false,
      dialogChoiceFavorites: false,
      form: {
        name: "",
        description: "",
        open: 0
      },
      rules: {
        name: [
          {required: true, message: '收藏夹名称不能为空', trigger: 'blur'},
          {min: 1, max: 10, message: '收藏夹名称长度在1到10个字符'},
        ]
      }

    };
  },
  created() {

  },
  computed: {
    ...mapGetters(['getUserInfo']),
  },
  methods: {

    createFavorites() {
      this.dialogChoiceFavorites = false;
      this.dialogCreateFavorites = true;
    },
    closeCreateFavorites() {
      this.dialogCreateFavorites = false;
      this.clearForm();
      // 清除本次的校验信息
      this.$refs.form.clearValidate();
    },
    closeChoiceFavorites() {
      this.dialogChoiceFavorites = false;
    },
    // 获取我的收藏夹
    getFavorites(userId, articleId) {
      let params = new URLSearchParams();
      params.append("userId", userId);
      params.append("articleId", articleId);
      getFavorites(params).then((response) => {
        this.favorites = response.data;
        console.log(this.favorites)
      })

    },
    // 创建收藏夹
    submitForm() {
      let params = {};
      params.userId = this.getUserInfo.id;
      params.articleId = this.articleId;
      params.name = this.form.name;
      params.description = this.form.description;
      params.open = this.form.open;
      createFavorites(params).then(response => {
        this.getFavorites(this.getUserInfo.id);
        this.dialogCreateFavorites = false;
        this.dialogChoiceFavorites = true;
        this.clearForm();
        this.$message.success(response.msg);
      })
    },

    // 选择收藏夹
    choiceFavorites() {
      let userId = this.getUserInfo.id;
      if (!userId) {
        this.$message.error("请先登录");
        return;
      }
      this.dialogChoiceFavorites = true;
      this.getFavorites(userId, this.articleId);
    },
    // 收藏文章
    collect(favoritesId, isFavoritesCollect) {
      let params = {};
      params.userId = this.getUserInfo.id;
      params.articleId = this.articleId;
      params.favoritesId = favoritesId;
      if (!isFavoritesCollect) {
        collect(params).then((response) => {
          this.$emit("updateCollectCounts");
          this.$emit("isCollectThisArticle");
          this.getFavorites(this.getUserInfo.id, this.articleId);
          this.$message.success(response.msg);
        })
      } else {
        cancelCollect(params).then(response => {
          this.$emit("updateCollectCounts");
          this.$emit("isCollectThisArticle");
          this.getFavorites(this.getUserInfo.id, this.articleId);
          this.$message.success(response.msg);
        })
      }
    },
    handleCancel() {
      this.dialogChoiceFavorites = true;
      this.dialogCreateFavorites = false;
      this.clearForm();
      // 清除本次的校验信息
      this.$refs.form.clearValidate();
    },
    clearForm() {
      this.form = {
        name: "",
        description: "",
        open: ""
      };
    },
    //文章点赞
    praiseBlog() {
      // 判断用户是否登录
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.error({
          title: '提示',
          message: '请先登录'
        });
        return;
      }
      let params = {};
      params.articleId = this.articleId;
      params.userId = this.$store.state.user.userInfo.id;
      if (!this.isStar) {
        praiseArticleById(params).then(response => {
          this.$emit("updateStarCounts");
          this.$emit("isStarThisArticle");
          this.$message.success(response.msg);
        });
      } else {
        // 取消点赞
        cancelStar(params).then(response => {
          this.$emit("updateStarCounts");
          this.$emit("isStarThisArticle");
          this.$message.success(response.msg);
        });
      }
    },
  }
};
</script>

<style>
.digit {
  cursor: pointer;
}

.collect {
  cursor: pointer;
}

.favoritesName {
  font-weight: bold;
  line-height: 20px;
  color: black;
  font-size: 14px;
}

.open {
  float: left;
}

.collectCount {
  float: left;
}
</style>
