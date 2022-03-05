<template>
  <div class="main-page">

    <!--条件搜索-->
    <div class="search-box">
      <div class="status-wrapper">
        <el-form :inline="true" v-show="showSearch" label-width="68px" style="margin-bottom: 18px;">
          <el-input
              clearable
              class="filter-item"
              style="width: 130px;  margin-left: 10px;"
              v-model="queryParams.keyword"
              placeholder="博客标题"
              @keyup.enter.native="handleFind"
          ></el-input>
          <el-select
              @input="contentChange"
              v-model="queryParams.categoryId"
              placeholder="文章分类"
              style="width:150px">
            <el-option
                v-for="item in articleCategoryList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            ></el-option>
          </el-select>
          <el-select v-model="queryParams.status" clearable placeholder="文章状态"
                     style="width:130px; margin-left: 10px;">
            <el-option value="0" label="全部" checked></el-option>
            <el-option value="1" label="审核中"></el-option>
            <el-option value="2" label="已发布"></el-option>
            <el-option value="3" label="未通过"></el-option>
            <el-option value="4" label="已撤回"></el-option>
          </el-select>

          <el-date-picker
              v-model="value2"
              type="datetimerange"
              :picker-options="pickerOptions"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              align="right" style="width: 400px; margin-left: 10px;">
          </el-date-picker>

          <el-button style="margin-left: 10px;" class="filter-item" type="info" icon="el-icon-search"
                     @click="handleFind">查找
          </el-button>
        </el-form>
      </div>
      <el-row :gutter="10" style="margin-bottom: 8px; margin-left: 20px;">
        <el-col :span="1.5">
          <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit">添加博客</el-button>
        </el-col>
        <el-col :span="20">
          <right-toolbar :showSearch.sync="showSearch" @queryTable="resetBlogList"></right-toolbar>
        </el-col>

      </el-row>
    </div>

    <!--添加博客开始-->
    <el-dialog
        :title="navTitle"
        :visible.sync="dialogFormVisible" fullscreen>
      <el-form :model="form" :rules="rules" ref="form">
        <el-row>
          <el-col :span="16">
            <el-form-item label="标题" :label-width="formLabelWidth" prop="title">
              <el-input v-model="form.title" placeholder="请输入文字标题(6-30长度)"
                        auto-complete="off" maxlength="30" @input="contentChange"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="16">
            <div class="cover-wrapper">
              <div class="cover">文章封面</div>
              <div class="choose-type">
                <div>
                  <label><input type="radio" name="articleType" v-model="form.articleType" value=1 checked/></label>
                  <span class="choose-words">单封面</span></div>
                <div style="margin-left: 30px;">
                  <label><input type="radio" v-model="form.articleType" value=2 name="articleType"/>
                  </label>
                  <span class="choose-words">无封面</span>
                </div>
              </div>
            </div>
            <div class="cover-wrapper" v-show="form.articleType == 1">
              <div class="cover"></div>
              <div class="choose-cover">
                <div class="uploader-comp">
                  <div id="block-choose" class="block-choose" :style="coverStyle">
                    <img src="@/assets/icon-go-upload.png" style="width: 20px; height: 20px; align-self: center;"
                         v-show="form.articleCover === '' || form.articleCover === null" alt=""/>
                  </div>
                  <input type="file" @change="uploadCover" @mouseover="mouseOver" @mouseout="mouseOut"
                         id="inputPic" class="inputPic" accept="image/jpeg,image/jpg,image/png">
                </div>
                <div style="margin-top: 10px; color: #9b9d9e;">请上传JPG、JPEG、PNG格式的封面图</div>
              </div>
            </div>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="6.5">
            <el-form-item label="分类" :label-width="formLabelWidth" prop="categoryId">
              <el-select
                  @input="contentChange"
                  v-model="form.categoryId"
                  size="small"
                  placeholder="请选择"
                  style="width:150px">
                <el-option
                    v-for="item in articleCategoryList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
        </el-row>
        <el-form-item label="内容" :label-width="formLabelWidth" prop="content">
          <CKEditor ref="editor" :content="form.content" style="margin-bottom:30px" @contentChange="contentChange"
                    :height="360"></CKEditor>
        </el-form-item>
        <div class="publish-bottom">
          <div class="buttons">
            <button class="white-btn" type="button" @click="dialogFormVisible = false">取消</button>
            <button class="white-btn" type="button" @click="preview">预览</button>
            <button class="white-btn" type="button" v-if="!isEdit" @click="doTiming">{{ appointWords }}</button>
            <el-date-picker
                v-model="form.createTime"
                type="datetime"
                id="choose-date"
                class="timing-date-picker"
                v-show="isAppoint ===  1"
                placeholder="定时日期">
            </el-date-picker>
            <button class="submit-btn" type="button" @click="publish">{{ publishBtn }}</button>
          </div>
        </div>
      </el-form>
    </el-dialog>
    <!--添加博客结束-->

    <!--文章结果展示-->
    <div id="article-list-wrapper" class="article-list-wrapper" v-if="articleList.length">
      <div class="article-list">
        <div class="every-article" v-for="(article, index) in articleList" :key="index">
          <img :src="article.articleCover" style="width: 175px; height: 125px;"
               v-show="article.articleType === 1" alt="cover"/>
          <div class="main-content">
            <div class="basic-info">
              <span><a href="javascript:void(0);" target="_blank" class="article-link">{{ article.title }}</a></span>
              <span class="counts-wrapper">
										<span>阅读 {{ article.readCounts }}</span>
										<span>评论 {{ article.commentCounts }}</span>
                	<span>收藏 {{ article.collectCounts }}</span>
              </span>
              <span class="status-reviewing article-status"
                    v-show="article.articleStatus === 1">审核中</span>
              <span class="status-published article-status"
                    v-show="article.articleStatus === 2">已发布</span>
              <span class="status-fail article-status"
                    v-show="article.articleStatus === 3">审核未通过</span>
              <span class="status-back article-status" v-show="article.articleStatus === 5">已撤回</span>
              <span class="publish-time">
										<span v-show="article.isAppoint === 1">预计发布时间:</span>
                										{{ article.createTime }}
									</span>
            </div>
          </div>
          <el-button type="text" icon="el-icon-edit" v-show="article.articleStatus === 2"
                     @click="handleEdit(article)"></el-button>
          <el-button type="text" icon="el-icon-refresh-left"
                     v-show="article.articleStatus === 2 || article.articleStatus === 3"
                     @click="withdraw((article.id))"></el-button>
          <el-button type="text" icon="el-icon-delete"
                     v-show="article.articleStatus === 2 || article.articleStatus === 3"
                     @click="deleteArticle(article.id)"/>
        </div>
      </div>
    </div>

    <el-empty description="你还没有文章, 快去写一篇吧" v-else style="background-color: white;"></el-empty>

    <!--分页-->
    <div class="block paged">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="queryParams.currentPage"
          :page-size="queryParams.pageSize"
          layout="total, prev, pager, next, jumper"
          :total="records">
      </el-pagination>
    </div>

  </div>

</template>

<script>
import {getCookie} from "@/utils/cookie";
import RightToolbar from '@/components/RightToolbar'
import CKEditor from "@/components/CKEditor";
import {deleteBlog, publish, queryArticleList, uploadBlogCover, withdrawBlog} from "@/api/blog";
import {mapGetters} from "vuex";
import {getBlogCategory} from "@/api";

export default {
  name: "manage",
  components: {
    RightToolbar,
    CKEditor
  },
  data() {
    return {
      articleList: [],
      showSearch: null,
      articleCategoryList: [],
      userInfo: {},
      isAppoint: 0,
      appointWords: "定时发布",
      isEdit: false,
      showUploadCover: true,
      coverStyle: "",
      timingDate: "",
      dialogFormVisible: false,
      closeDialog: false,
      formLabelWidth: "120px",
      CKEditorData: "",
      navTitle: "增加博客",
      publishBtn: "发布文章",
      // 查询我的文章
      totalPage: 0,
      records: 0,
      queryParams: {
        keyword: "",
        categoryId: "",
        status: "",
        currentPage: 1,
        pageSize: 5,
        startDateStr: "",
        endDateStr: "",
      },
      form: {
        title: "",
        categoryId: "",
        content: "",
        author: "",
        articleType: "1",
        articleCover: "",
        isAppoint: "",
        createTime: "",
      },
      rules: {
        title: [
          {required: true, message: '标题不能为空', trigger: 'blur'}
        ],
        categoryId: [
          {required: true, message: '分类不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '内容不能为空', trigger: 'blur'}
        ],
      },
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      value1: [new Date(2000, 10, 10, 10, 10), new Date(2000, 10, 11, 10, 10)],
      value2: '',
    }
  },
  created() {
    // 判断是否需要展开条件查询
    this.getShowSearch();
    this.userInfo = this.getUserInfo();
    getBlogCategory().then(res => {
      if (res.data.success) {
        this.articleCategoryList = res.data.data;
      } else {
        this.$message.error(res.data.msg);
      }
    });
    this.queryArticleList();
  },
  methods: {
    ...mapGetters(['getUserInfo']),
    withdraw(articleId) {
      this.$confirm('是否撤回当前文章', '确认信息', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确认撤回',
        cancelButtonText: '取消'
      }).then(() => {
        let params = new URLSearchParams();
        params.append("articleId", articleId);
        params.append("userId", this.userInfo.id);
        withdrawBlog(params).then((response) => {
          if (response.data.success) {
            this.$message.success(response.data.msg);
            this.queryArticleList();
          } else {
            this.$message.error(response.data.msg);
          }
        })
      }).catch(() => {
        /**取消action*/
      })
    },

    deleteArticle(articleId) {
      this.$confirm('是否删除当前文章', '确认信息', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确认删除',
        cancelButtonText: '取消'
      }).then(() => {
        let params = new URLSearchParams();
        params.append("articleId", articleId);
        params.append("userId", this.userInfo.id);
        deleteBlog(params).then((response) => {
          if (response.data.success) {
            this.$message.success(response.data.msg);
            this.queryArticleList();
          } else {
            this.$message.error(response.data.msg);
          }
        })
      }).catch(() => {
        /**取消action*/
      })
    },
    initQueryParams() {
      return {
        keyword: "",
        categoryId: "",
        status: "",
        currentPage: 1,
        pageSize: 5,
        startDateStr: "",
        endDateStr: "",
      }
    },
    // 查询文章列表
    queryArticleList() {
      let queryParams = this.queryParams;
      let params = new URLSearchParams();
      params.append("userId", this.userInfo.id);
      params.append("keyword", queryParams.keyword);
      params.append("status", queryParams.status);
      params.append("startDate", "");
      params.append("endDate", "");
      params.append("categoryId", queryParams.categoryId);
      params.append("page", queryParams.currentPage);
      params.append("pageSize", queryParams.pageSize);

      queryArticleList(params).then(res => {
        if (res.data.success) {
          let content = res.data;
          this.articleList = content.data.rows;
          this.queryParams.currentPage = content.data.currentPage;
          this.records = content.data.records;
          this.totalPage = content.data.totalPage;
        } else {
          this.$message.error(res.data.msg);
        }
      });
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.queryArticleList();
    },
    handleFind() {
      this.queryArticleList();
    },

    handleAdd() {
      this.navTitle = "增加博客";
      this.dialogFormVisible = true;
      this.$nextTick(() => {
        this.$refs.editor.initData(); //设置富文本内容
      });
    },
    handleEdit(article) {
      this.navTitle = "编辑博客";
      this.publishBtn = "保存修改"
      this.isEdit = true;
      this.dialogFormVisible = true;
      this.form.title = article.title;
      this.form.categoryId = article.categoryId;
      this.form.articleCover = article.articleCover;
      this.form.articleType = article.articleType;
      this.setPicCoverStyle(this.form.articleCover);
      this.$nextTick(() => {
        this.$refs.editor.setData(article.content);
      });
    },
    // 判断是否需要展开条件查询
    getShowSearch: function () {
      let showSearch = getCookie("showSearch")
      this.showSearch = showSearch !== "false";
    },
    resetBlogList: function () {
      this.queryParams = this.initQueryParams();
      this.queryArticleList();
      this.$message.success("刷新成功");
    },
    // 内容改变, 触发监听
    contentChange: function () {
    },
    preview() {
    },
    doTiming() {
      const _this = this;
      if (_this.isAppoint === 1) {
        _this.isAppoint = 0;
        _this.appointWords = "定时发布";
        _this.timingDate = "";
        $("#choose-date").val("");
      } else if (_this.isAppoint === 0) {
        _this.isAppoint = 1;
        _this.appointWords = "取消定时";
      }
    },
    publish() {
      //获取CKEditor中的内容
      this.form.content = this.$refs.editor.getData();
      this.form.authorId = this.userInfo.id;
      this.form.isAppoint = this.isAppoint;
      this.form.createTime = this.timingDate;
      this.$refs.form.validate((valid) => {
        if (valid) {
          publish(this.form).then(response => {
            if (response.data.success) {
              if (this.isEdit) {
                this.$message.success("文章修改成功")
              } else {
                this.$message.success(response.data.msg)
              }
              this.dialogFormVisible = false;
              this.queryArticleList();
              this.form = {};
            } else {
              this.$message.error(response.data.msg)
            }
          });
        }
      })
    },
    // 上传封面
    uploadCover() {
      const that = this;
      let f = document.getElementById('inputPic').files[0];
      //创建一个form对象
      let multiForm = new FormData();
      //append 向form表单添加数据
      multiForm.append('files', f, f.name);
      uploadBlogCover(multiForm).then(response => {
        if (response.data.success) {
          let imagesList = response.data.data;
          if (imagesList.length < 1) {
            this.$notify.error({
              title: '错误',
              message: '图片上传失败,请保证图片不能为空,并且符合 jpg/png/jpeg 的后缀格式!'
            });
          } else {
            this.$notify.success({
              title: '提示',
              message: response.data.msg
            });
            that.form.articleCover = response.data.data[0];
            this.setPicCoverStyle(that.form.articleCover);
          }
        } else {
          this.$notify.info({
            title: '消息',
            message: response.data.msg
          });
        }
      });
    },
    setPicCoverStyle(url) {
      this.coverStyle = "background-image: url(" + url + ");";
      this.coverStyle += "background-repeat: no-repeat;";
      this.coverStyle += "background-position: center center;";
      this.coverStyle += "background-size: cover;";
    },

    // 鼠标移动到上传组件上发生css变化
    mouseOver() {
      $("#block-choose").css({"background-color": "#f0efef", "border": "1px dashed #a7a7a7"});
    },
    // 鼠标离开上传组件上发生css变化
    mouseOut() {
      $("#block-choose").css({"background-color": "#f7f7f7", "border": "1px dashed #d8d8d8"});
    },
  }
  ,

}
</script>

<style scoped>
.main-page {
  width: 980px;
  margin-left: 20px;
}

.search-box {
  background-color: white;
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
}

.status-wrapper {
  margin-top: 25px;
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}


/* 文章列表 start */
.article-list-wrapper {
  margin-top: 15px;
  background-color: #fff;
  padding: 20px 25px;
}

.every-article {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  padding: 20px 0;

  border-bottom: 1px solid #d5d2d2;
}


.main-content {
  /* width: 720px; */
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;

  margin-left: 20px;
  margin-right: 20px;
}

.basic-info {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 610px;
  margin-top: 10px;
}

.article-link {
  font-size: 16px;
  color: #222;
}

.operation {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.counts-wrapper {
  margin-top: 8px;
  color: #6b6464;
}

.article-status {
  font-size: 12px;
  padding: 1px;

  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 8px;
}

.status-reviewing {
  color: orange;
  background-color: rgb(242, 222, 191);
  width: 50px;
}

.status-back {
  color: rgb(136, 135, 135);
  background-color: rgb(215, 214, 214);
  width: 50px;
}

.status-fail {
  color: rgb(225, 68, 68);
  background-color: rgb(240, 209, 209);
  width: 70px;
}

.status-published {
  color: rgb(34, 151, 34);
  background-color: rgb(187, 243, 187);
  width: 50px;
}

.publish-time {
  color: gray;
  margin-top: 8px;
}

.cover-wrapper {
  margin-bottom: 20px;
  margin-left: 80px;
  display: flex;
  flex-direction: row;
}

.cover {
  width: 120px;
}

.choose-type {
  display: flex;
  flex-direction: row;
}

.choose-words {
  margin-left: 10px;
}

.uploader-comp {
  display: flex;
  flex-direction: row;
}

.block-choose {
  width: 158px;
  height: 108px;
  background-color: #f7f7f7;
  border-radius: 4px;
  border: 1px dashed #d8d8d8;
  display: flex;
  flex-direction: row;
  justify-content: center;
}

.inputPic {
  cursor: pointer;
  opacity: 0;
  width: 158px;
  height: 108px;
  position: relative;
  left: -158px;
  z-index: 9;
}

.block-choose:hover {
  width: 158px;
  height: 108px;
  background-color: #f0efef;
  border-radius: 4px;
  border: 1px dashed #a7a7a7;
  display: flex;
  flex-direction: row;
  justify-content: center;
  cursor: pointer;
}

/* 底部固定 */
.publish-bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 66px;
  line-height: 40px;
  z-index: 88;
  background-color: white;
  margin-top: 100px;
  border-top: 1px solid #dfdede;
  display: flex;
  flex-direction: row;
  justify-content: center;
}

.buttons {
  width: 888px;
  align-self: center;
}

.white-btn {
  background-color: white;
  color: #222222;
  outline: none;
  border: 1px solid rgb(201, 201, 201);
  height: 40px;
  margin-right: 10px;
  padding: 0 20px;
  border-radius: 5px;
}

.white-btn:hover {
  background-color: rgb(240, 239, 239);
  border: 1px solid rgb(180, 179, 179);
}

.timing-date-picker {
  height: 40px;
  padding-left: 5px;
  padding-right: 5px;
  width: 200px;
  margin-right: 10px;
}

.submit-btn {
  background-color: black;
  color: white;
  outline: none;
  border: 1px solid black;
  height: 40px;
  margin-right: 10px;
  padding: 0 20px;
  border-radius: 5px;
}

.submit-btn:hover {
  /*background-color: #dd4456;*/
  /*border: 1px solid #dd4456;*/
}

.paged {
  text-align: center;
  margin-top: 60px;
  margin-bottom: 20px;
}
</style>