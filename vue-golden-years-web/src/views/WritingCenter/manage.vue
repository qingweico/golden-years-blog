<template>
  <div class="main-page">

    <!--条件搜索-->
    <div class="search-box">
      <div class="status-wrapper">

        <el-col :span="20" :xs="24">
          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
            <el-form-item label="博客标题" prop="keyword">
              <el-input
                  v-model="queryParams.keyword"
                  placeholder="请输入博客标题"
                  clearable
                  style="width: 240px"
                  @keyup.enter.native="handleFind"
              />
            </el-form-item>
            <el-form-item label="文章状态" prop="status">
              <el-select
                  v-model="queryParams.status"
                  placeholder="文章状态"
                  clearable
                  style="width: 240px"
                  @keyup.enter.native="handleFind">
                <el-option value="0" label="全部" checked></el-option>
                <el-option value="1" label="审核中"></el-option>
                <el-option value="2" label="已发布"></el-option>
                <el-option value="3" label="未通过"></el-option>
                <el-option value="4" label="已撤回"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="文章分类" prop="categoryId">
              <el-select
                  v-model="queryParams.categoryId"
                  placeholder="请选择文章分类"
                  clearable
                  style="width: 240px"
              >
                <el-option
                    v-for="item in articleCategoryList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="发布时间">
              <el-date-picker
                  v-model="dateRange"
                  @change="dateChoice"
                  style="width: 240px"
                  value-format="yyyy-MM-dd"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
              ></el-date-picker>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleFind">搜索</el-button>
              <el-button class="primary" @click="handleAdd" size="mini" icon="el-icon-edit">添加博客</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetBlogList">重置</el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </div>
    </div>
    <!--添加博客开始-->
    <el-dialog
        :title="navTitle"
        :visible.sync="dialogFormVisible"
        :before-close="closeDialog" fullscreen>
      <el-form :model="form" :rules="rules" ref="form">
        <el-row>
          <el-col :span="16">
            <el-form-item label="标题" :label-width="formLabelWidth" prop="title">
              <el-input v-model="form.title" placeholder="请输入文字标题(1-30长度)"
                        maxlength="30" @input="contentChange"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="16">
            <el-form-item label="文章封面" :label-width="formLabelWidth">
              <el-radio-group v-model="form.articleType">
                <el-radio label="1">单封面</el-radio>
                <el-radio label="2">无封面</el-radio>
              </el-radio-group>
            </el-form-item>
            <div class="cover-wrapper" v-show="form.articleType === '1'">
              <div class="cover"></div>
              <div class="choose-cover">
                <div class="uploader-comp">
                  <div id="block-choose" class="block-choose" :style="coverStyle">
                    <img src="@/assets/icon-go-upload.png" style="width: 20px; height: 20px; align-self: center;"
                         v-show="form.articleCover === '' || form.articleCover === null" alt=""/>
                  </div>
                  <input type="file" @change="uploadCover" @mouseover="mouseOver"
                         @mouseout="mouseOut"
                         @input="contentChange"
                         id="inputPic"
                         class="inputPic"
                         accept="image/jpeg,image/jpg,image/png">
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
          <el-col :span="6.5">
            <el-form-item label="标签" :label-width="formLabelWidth">
              <el-select
                  @input="contentChange"
                  v-model="selectedTag.id"
                  size="small"
                  placeholder="请选择"
                  @change="handleChooseSysTag(selectedTag.id)"
                  style=" width:150px">
                <el-option
                    v-for="item in tagList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"></el-option>
              </el-select>
              <el-tag
                  :key="tag.id"
                  v-for="tag in dynamicTags"
                  closable
                  :hit="false"
                  :color="tag.color"
                  effect="plain" style="color: white"
                  :disable-transitions="false"
                  @close="handleClose(tag)">
                {{ tag.name }}
              </el-tag>
              <el-input
                  class="input-new-tag"
                  v-if="inputVisible"
                  v-model="newTagValue"
                  ref="saveTagInput"
                  size="small"
                  @keyup.enter.native="handleInputConfirm"
                  @blur="handleInputConfirm">
              </el-input>
              <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Tag</el-button>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
        </el-row>
        <el-form-item label="内容" :label-width="formLabelWidth" prop="content">
          <!--富文本编辑器-->
          <quill-editor v-if="systemConfig.editorModel === 0" ref="textEditor"
                        @change="onEditorChange" v-model="richTextContent" class="editor"
                        :options="editorOption"></quill-editor>
          <!--Markdown编辑器-->
          <mavon-editor v-if="systemConfig.editorModel === 1" ref="md" :value="initContent"
                        autofocus @imgAdd="$imgAdd" @change="change" class="editor"/>
        </el-form-item>
        <div class="publish-bottom">
          <div class="buttons">
            <button class="white-btn" type="button" @click="closeDialog">取消</button>
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
            <el-button class="white-btn" @click="publishOrEdit">{{ publishOrEditBtn }}</el-button>
          </div>
        </div>
      </el-form>
    </el-dialog>
    <!--添加博客结束-->

    <!--文章结果展示-->
    <div class="article-list" v-if="articleList.length" v-for="(article, index) in articleList" :key="index">
      <el-card class="box-card" shadow="hover" style="margin-bottom: 10px">
        <el-descriptions :title="article.title" direction="horizontal" :colon=false size="mini" :column=5>
          <el-descriptions-item>
            <img :src="article.articleCover" style="width: 175px; height: 125px;"
                 v-show="article.articleType === 1" alt="cover"/>
          </el-descriptions-item>
          <el-descriptions-item>
            <span>{{ article.createTime }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <span>{{ getBlogCategoryNameById(article.categoryId) }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <el-tag v-show="article.articleStatus === 1">审核中</el-tag>
            <el-tag type="success" v-show="article.articleStatus === 2">已发布</el-tag>
            <el-tag type="danger" v-show="article.articleStatus === 3">审核未通过</el-tag>
            <el-tag type="info" v-show="article.articleStatus === 4">已撤回</el-tag>
          </el-descriptions-item>
          <el-descriptions-item>
            <el-button type="text" icon="el-icon-edit" v-show="article.articleStatus === 2 ||
             article.articleStatus === 3 || article.articleStatus === 4"
                       @click="handleEdit(article)">编辑
            </el-button>
            <el-button type="text" icon="el-icon-refresh-left"
                       v-show="article.articleStatus === 2 || article.articleStatus === 3"
                       @click="withdraw((article.id))">撤回
            </el-button>
            <el-button type="text" icon="el-icon-delete"
                       v-show="article.articleStatus === 2 || article.articleStatus === 3 || article.articleStatus === 4"
                       @click="deleteArticle(article.id)">删除
            </el-button>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>

    <el-empty description="你还没有文章, 快去写一篇吧" v-if="!articleList.length"
              style="background-color: white;"></el-empty>

    <!--分页-->
    <div class="block paged">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="records">
      </el-pagination>
    </div>

  </div>

</template>

<script>
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import 'quill/dist/quill.bubble.css';
import {quillEditor} from 'vue-quill-editor';
import {
  deleteBlog, getSystemConfig,
  getTagList,
  publishOrUpdate,
  queryArticleList,
  uploadBlogPic,
  withdrawBlog
} from "@/api/center";
import {mapGetters} from "vuex";
import {getBlogCategory} from "@/api";
import {mavonEditor} from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

export default {
  name: "manage",
  components: {
    mavonEditor,
    quillEditor
  },

  data() {
    return {
      articleList: [],
      articleCategoryList: [],
      tagList: [],
      selectedTag: {},
      userInfo: {},
      isAppoint: 0,
      initContent: "",
      markdownContent: "",
      inputVisible: false,
      newTagValue: "",
      dynamicTags: [],
      dateRange: "",
      systemConfig: {},
      // 监听表单内容是否改变
      isChange: false,
      appointWords: "定时发布",
      isEdit: false,
      showUploadCover: true,
      coverStyle: "",
      timingDate: "",
      dialogFormVisible: false,
      formLabelWidth: "120px",
      CKEditorData: "",
      navTitle: "增加博客",
      publishOrEditBtn: "发布文章",

      richTextContent: '',
      editorOption: {
        placeholder: '开始编辑......',
        toolbar: {
          // 工具栏
          handlers: {
            'image': function (value) {
              if (value) {
                // TODO 使用图片上传服务器方式代替base64
                document.querySelector('.avatar-uploader input').click()
              } else {
                this.quill.format('image', false)
              }
            }
          }
        }
      },
      // 查询我的文章
      currentPage: 1,
      pageSize: 5,
      totalPage: 0,
      records: 0,
      queryParams: {
        keyword: "",
        categoryId: "",
        status: "",
        startDateStr: "",
        endDateStr: "",
      },
      form: {
        articleId: "",
        title: "",
        categoryId: "",
        content: "",
        authorId: "",
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
        ]
      }
    }
  },
  created() {
    this.userInfo = this.getUserInfo;
    getSystemConfig().then((response) => {
      this.systemConfig = response.data;
    })
    getBlogCategory().then(res => {
      this.articleCategoryList = res.data;
    });
    getTagList().then(response => {
      this.tagList = response.data;
    });
    this.queryArticleList();
  },
  computed: {
    ...mapGetters(['getUserInfo']),
  },
  methods: {

    // MarkDown
    // 将图片上传到服务器, 返回地址替换到md中
    $imgAdd(pos, $file) {
      let formData = new FormData();
      formData.append('files', $file);
      uploadBlogPic(formData).then((response) => {
        this.$message.success(response.msg);
        this.$refs.md.$img2Url(pos, response.data);
      })
    },
    change(value, render) {
      this.isChange = true;
      // render为markdown解析后的结果
      this.markdownContent = render;
    },
    onEditorChange({editor, html, text}) {
      this.content = html;
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.articleCategoryList;
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].name;
        }
      }
    },
    dateChoice() {
      let dateArray = String(this.dateRange).split(",");
      this.queryParams.startDateStr = dateArray[0];
      this.queryParams.endDateStr = dateArray[1];
    },
    closeDialog() {
      if (this.isChange) {
        this.$confirm("系统不会保存此次修改, 是否放弃本次操作", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.clearStatus();
          done();
        }).catch(() => {
          /**取消action*/
        });
      } else {
        this.clearStatus();
      }
    },
    withdraw(articleId) {
      this.$confirm('是否撤回当前文章', '确认信息', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确认撤回',
        cancelButtonText: '取消',
        type: "info"
      }).then(() => {
        let params = new URLSearchParams();
        params.append("articleId", articleId);
        params.append("userId", this.userInfo.id);
        withdrawBlog(params).then((response) => {
          this.$message.success(response.msg);
          this.queryArticleList();
        })
      })
    },

    deleteArticle(articleId) {
      this.$confirm('是否删除当前文章', '确认信息', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: "error"
      }).then(() => {
        let params = new URLSearchParams();
        params.append("articleId", articleId);
        params.append("userId", this.userInfo.id);
        deleteBlog(params).then((response) => {
          this.$message.success(response.msg);
          this.queryArticleList();
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
      params.append("startDate", queryParams.startDateStr);
      params.append("endDate", queryParams.endDateStr);
      params.append("categoryId", queryParams.categoryId);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      queryArticleList(params).then(res => {
        let content = res.data;
        this.articleList = content.rows;
        this.queryParams.currentPage = content.currentPage;
        this.records = content.records;
        this.totalPage = content.totalPage;
      });
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.queryArticleList();
    },
    handleFind() {
      this.queryArticleList();
    },
    clearForm() {
      this.form = {
        title: "",
        articleId: "",
        categoryId: "",
        content: "",
        authorId: "",
        articleType: "1",
        articleCover: "",
        isAppoint: "",
        createTime: "",
      }
    },
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },
    handleChooseSysTag(tagId) {
      for (let item of this.tagList) {
        if (item.id === tagId && this.dynamicTags) {
          let name = item.name;
          let color = item.color;
          let isPresent = false;
          for (let hasPresentItem of this.dynamicTags) {
            if (hasPresentItem.id === item.id) {
              isPresent = true;
            }
          }
          if (!isPresent) {
            this.dynamicTags.push({
              id: tagId,
              name: name,
              color: color
            });
            break;
          }
        }
      }
    },
    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    handleInputConfirm() {
      let newTagValue = this.newTagValue;
      if (newTagValue) {
        this.dynamicTags.push({
          id: null,
          name: newTagValue,
          // 用户自定义标签默认颜色
          color: "#409eff"
        });
      }
      this.inputVisible = false;
      this.newTagValue = '';
    },
    handleAdd() {
      this.navTitle = "增加博客";
      this.dialogFormVisible = true;
      if (this.systemConfig.editorModel === 0) {
        // rich text
        this.richTextContent = "";
      } else if (this.systemConfig.editorModel === 1) {
        // markdown
        this.initContent = "";
        this.markdownContent = "";
      }

    },
    handleEdit(article) {
      this.navTitle = "编辑博客";
      this.publishOrEditBtn = "保存修改"
      this.isEdit = true;
      this.dialogFormVisible = true;
      this.form.articleId = article.id;
      this.form.title = article.title;
      this.form.articleStatus = article.articleStatus;
      this.form.categoryId = article.categoryId;
      this.form.articleCover = article.articleCover;
      this.form.articleType = String(article.articleType);
      this.dynamicTags = article.tagList;
      if (this.systemConfig.editorModel === 0) {
        // rich text
        this.richTextContent = article.content;
      } else if (this.systemConfig.editorModel === 1) {
        // markdown
        this.initContent = article.content;
      }
      this.setPicCoverStyle(this.form.articleCover);
    },
    resetBlogList() {
      this.queryParams = this.initQueryParams();
      this.dateRange = "";
      this.queryArticleList();
      this.$message.success("刷新成功");
    },
    // 内容改变, 触发监听
    contentChange() {
      this.isChange = true;
    },
    clearStatus() {
      this.dialogFormVisible = false;
      this.clearForm();
      this.coverStyle = "";
      this.isChange = false;
      this.isEdit = false;
      this.isAppoint = 0;
      this.selectedTag = {};
      this.dynamicTags = [];
      this.markdownContent = "";
      this.initContent = "";
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
    publishOrEdit() {
      this.form.authorId = this.userInfo.id;
      this.form.isAppoint = this.isAppoint;
      this.form.createTime = this.timingDate;
      if (this.systemConfig.editorModel === 0) {
        this.form.content = this.richTextContent
      } else if (this.systemConfig.editorModel === 1) {
        this.form.content = this.markdownContent;
      }
      this.form.tags = this.dynamicTags;
      if (this.dynamicTags.length === 0) {
        this.$message.error("请至少选择一个标签");
        return;
      }
      if (this.form.articleType === 1 && this.form.articleCover === "") {
        this.$notify.error({
          title: '错误',
          message: '请上传一个文章封面吧'
        });
        return;
      }
      this.$refs.form.validate((valid) => {
        if (valid) {
          publishOrUpdate(this.form).then(res => {
            if (this.isEdit) {
              this.$message.success(res.msg)
            } else {
              this.$message.success(res.msg)
            }
            this.queryArticleList();
            this.clearStatus();
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
      uploadBlogPic(multiForm).then(response => {
        if (response.success) {
          let imagesList = response.data;
          if (imagesList.length < 1) {
            this.$notify.error({
              title: '错误',
              message: '图片上传失败,请保证图片不能为空,并且符合 jpg/png/jpeg 的后缀格式!'
            });
          } else {
            this.$notify.success({
              title: '提示',
              message: response.msg
            });
            that.form.articleCover = response.data[0];
            this.setPicCoverStyle(that.form.articleCover);
          }
        } else {
          this.$notify.info({
            title: '消息',
            message: response.msg
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
}
</script>

<style scoped>
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

.cover-wrapper {
  margin-bottom: 20px;
  display: flex;
  flex-direction: row;
}

.cover {
  width: 120px;
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

.editor {
  z-index: 1;
  border: 1px solid #d9d9d9;
  height: 100vh;
  margin-bottom: 50px;
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

.el-tag + .el-tag {
  margin-left: 10px;
}

.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>