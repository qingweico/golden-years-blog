<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-form :inline="true" v-show="showSearch" label-width="68px" style="margin-bottom: 8px;">
        <el-input
          clearable
          class="filter-item"
          style="width: 150px;"
          v-model="queryParams.keyword"
          placeholder="请输入博客名"
          @keyup.enter.native="handleFind">
        </el-input>
        <el-select
          v-model="queryParams.sortKeyword"
          style="width: 140px"
          filterable
          clearable
          remote
          reserve-keyword
          placeholder="请输入分类名"
          @keyup.enter.native="handleFind"
          :loading="loading">
          <el-option
            v-for="item in sortOptions"
            :key="item.uid"
            :label="item.sortName"
            :value="item.uid">
          </el-option>
        </el-select>
        <el-select v-model="queryParams.publishKeyword" clearable placeholder="文章状态" style="width:110px">
        </el-select>
        <el-button style="margin-left: 10px;" class="filter-item" type="primary" icon="el-icon-search" @click="handleFind">查找</el-button>
      </el-form>


      <el-row :gutter="10" style="margin-bottom: 8px;">
        <right-toolbar :showSearch.sync="showSearch" @queryTable="resetBlogList"></right-toolbar>
      </el-row>

    </div>
    <el-table :data="tableData"
              ref="articleTable"
              style="width: 100%"
              @selection-change="handleSelectionChange"
              @sort-change="changeSort"
              :default-sort="{prop: 'createTime', order: 'descending'}">
      <el-table-column type="selection"></el-table-column>

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{scope.$index + 1}}</span>
        </template>
      </el-table-column>
      <el-table-column label="标题图" width="160" align="center">
        <template slot-scope="scope">
          <img
            v-if="scope.row.photoList"
            :src="scope.row.photoList[0]"
            style="width: 130px;height: 70px;"
          >
        </template>
      </el-table-column>
      <el-table-column label="标题" width="160" align="center">
        <template slot-scope="scope">
          <span @click="onClick(scope.row)" style="cursor:pointer;">{{ scope.row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column label="作者" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.author }}</span>
        </template>
      </el-table-column>

      <el-table-column label="文章状态" width="100" align="center" prop="isOriginal" sortable="custom" :sort-by="['isOriginal']">
        <template slot-scope="scope">
          <el-tag type="success">原创</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="分类" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.blogSort.sortName }}</span>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <div class="block">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="total, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="dialogFormVisible"
      :before-close="closeDialog"
      fullscreen
    >
      <el-form :model="form" :rules="rules" ref="form">
        <el-row>
          <el-col :span="16">
            <el-form-item label="标题" :label-width="formLabelWidth" prop="title">
              <el-input v-model="form.title" auto-complete="off" @input="contentChange"></el-input>
            </el-form-item>
            <el-form-item label="简介" :label-width="formLabelWidth">
              <el-input v-model="form.summary" auto-complete="off" @input="contentChange"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="标题图" :label-width="formLabelWidth">
              <div class="imgBody" v-if="form.photoList">
                <i
                  class="el-icon-error inputClass"
                  v-show="icon"
                  @click="deletePhoto()"
                  @mouseover="icon = true"
                ></i>
                <img
                  @mouseover="icon = true"
                  @mouseout="icon = false"
                  v-bind:src="form.photoList[0]"
                  style="display:inline; width: 195px;height: 105px;"
                >
              </div>
              <div v-else class="uploadImgBody" @click="checkPhoto">
                <i class="el-icon-plus avatar-uploader-icon"></i>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6.5">
            <el-form-item label="分类" :label-width="formLabelWidth" prop="blogSortUid">
              <el-select
                @input="contentChange"
                v-model="form.blogSortUid"
                size="small"
                placeholder="请选择"
                style="width:150px"
              >
                <el-option
                  v-for="item in blogSortData"
                  :key="item.uid"
                  :label="item.sortName"
                  :value="item.uid"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { getBlogList, deleteBlog, deleteBatchBlog } from "@/api/blog";
import { getToken } from '@/utils/auth'
import {getCookie } from "@/utils/cookie";
const querystring = require("querystring");
import { Loading } from 'element-ui';
export default {
  components: {

  },
  data() {
    return {
      // uploadPictureHost: process.env.PICTURE_API + "/file/pictures",
      // uploadAdminHost: process.env.ADMIN_API + "/blog/uploadLocalBlog",
      importHeaders: {
        Authorization: getToken()
      },
      otherData: {
        source: "picture",
        userUid: "uid00000000000000000000000000000000",
        adminUid: "uid00000000000000000000000000000000",
        projectName: "blog",
        sortName: "admin",
        token: getToken()
      },
      queryParams:{
        keyword: "",
        tagKeyword: "", //标签搜索
        sortKeyword: "", //分类搜索
        levelKeyword: "", //等级搜索
        publishKeyword: "", // 发布 搜索
        originalKeyword: "", // 原创 搜索
        typeKeyword: "", // 文章类型
      }, // 搜索条件
      showSearch: null, // 显示搜索条件
      pictureList: [], // 上传的图片列表
      BLOG_WEB_URL: process.env.BLOG_WEB_URL,
      multipleSelection: [], //多选，用于批量删除
      tagOptions: [], //标签候选框
      sortOptions: [], //分类候选框
      loading: false, //搜索框加载状态
      uploadLoading: null, //文件上传loading
      CKEditorData: null,
      tableData: [], //博客数据
      tagData: [], //标签数据
      tagValue: [], //保存选中标签id(编辑时)
      blogSortData: [],
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      title: "增加博客",
      dialogFormVisible: false, //控制弹出框
      subjectVisible: false, // 是否显示专题
      isFirstSubjectVisible: true, // 专题选择器是否首次显示【用于懒加载】
      formLabelWidth: "120px",
      lineLabelWidth: "120px", //一行的间隔数
      maxLineLabelWidth: "100px",
      isEditForm: false,
      photoVisible: false, //控制图片选择器的显示
      isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
      photoList: [],
      fileIds: "",
      icon: false, //控制删除图标的显示
      interval: null, //定义触发器
      isChange: false, // 表单内容是否改变
      changeCount: 0, // 改变计数器
      blogOriginalDictList: [], //存储区域字典
      blogPublishDictList: [], //是否字典
      blogLevelDictList: [], //博客推荐等级字典
      openDictList: [], // 是否启动字典
      blogTypeDictList:[], // 文章类型字典
      blogOriginalDefault: null, //博客原创默认值
      blogLevelDefault: null, //博客等级默认值
      blogPublishDefault: null, //博客发布默认值
      openDefault: null, // 是否开启评论默认值
      blogTypeDefault: null, // 文章类型默认值
      fileList: [],
      localUploadVisible: false,
      systemConfig: {}, // 系统配置
      orderByDescColumn: "", // 降序字段
      orderByAscColumn: "", // 升序字段
      form: {
        uid: null,
        title: null,
        summary: null,
        content: null,
        tagUid: null,
        fileUid: null,
        isOriginal: null, //是否原创
        isPublish: null,
        author: null, //作者
        clickCount: 0,
        articlesPart: null //文章出处
      },
      rules: {
        title: [
          {required: true, message: '标题不能为空', trigger: 'blur'}
        ],
        blogSortUid: [
          {required: true, message: '分类不能为空', trigger: 'blur'}
        ],
        tagUid: [
          {required: true, message: '标签不能为空', trigger: 'blur'}
        ],
        level: [
          {required: true, message: '推荐等级不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '推荐等级只能为自然数'},
        ],
        isPublish: [
          {required: true, message: '发布字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '发布字段只能为自然数'},
        ],
        isOriginal: [
          {required: true, message: '原创字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '原创字段只能为自然数'},
        ],
        openComment: [
          {required: true, message: '网站评论不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '网站评论只能为自然数'},
        ],
        content: [
          {required: true, message: '内容不能为空', trigger: 'blur'}
        ],
        outsideLink: [
          {required: true, message: '外链地址不能为空', trigger: 'blur'},
          {pattern:  /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入有效的URL'},
        ],
      }
    };
  },
  created() {
    //从dashboard传递过来的 tagUid 以及 blogSortUid
    let tempTag = this.$route.query.tag;
    let tempBlogSort = this.$route.query.blogSort;

    if(tempTag != undefined) {
      this.tagRemoteMethod(tempTag.name);
      this.queryParams.tagKeyword = tempTag.tagUid;
    }
    if(tempBlogSort != undefined) {
      this.sortRemoteMethod(tempBlogSort.name);
      this.queryParams.sortKeyword = tempBlogSort.blogSortUid;
    }
    // 判断是否需要展开条件查询
    this.getShowSearch()

    // 获取系统配置
    this.getSystemConfigList()

    // 获取字典
    this.getDictList()

    // 获取标签列表
    this.tagList()
    // 获取博客分类
    this.blogSortList()
    //获取博客列表
    this.blogList()
  },
  methods: {
    // 从后台获取数据,重新排序
    changeSort (val) {
      // 根据当前排序重新获取后台数据,一般后台会需要一个排序的参数
      if(val.order == "ascending") {
        this.orderByAscColumn = val.prop
        this.orderByDescColumn = ""
      } else {
        this.orderByAscColumn = ""
        this.orderByDescColumn = val.prop
      }
      this.blogList()
    },
    openLoading() {
      this.uploadLoading = Loading.service({
        lock: true,
        text: '正在努力上传中……'
      })
    },
    closeLoading() {
        this.uploadLoading.close()
    },
    // 判断是否需要展开条件查询
    getShowSearch: function () {
      let showSearch = getCookie("showSearch")
      if(showSearch == "false"){ //此时的hasAuth是true
        this.showSearch = false
      } else {
        this.showSearch = true
      }
    },
    tagList: function() {
      var tagParams = {};
      tagParams.pageSize = 500;
      tagParams.currentPage = 1;
      getTagList(tagParams).then(response => {
        this.tagData = response.data.records;
        this.tagOptions = response.data.records;
      });
    },
    blogSortList: function() {
      var blogSortParams = {};
      blogSortParams.pageSize = 500;
      blogSortParams.currentPage = 1;
      getBlogSortList(blogSortParams).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.blogSortData = response.data.records;
          this.sortOptions = response.data.records;
        }
      });
    },
    resetBlogList: function (){
      this.queryParams = {}
      this.blogList();
    },
    blogList: function() {
      var params = {};
      params.keyword = this.queryParams.keyword;
      params.blogSortUid = this.queryParams.sortKeyword;
      params.tagUid = this.queryParams.tagKeyword;
      params.levelKeyword = this.queryParams.levelKeyword;
      params.isPublish = this.queryParams.publishKeyword;
      params.isOriginal = this.queryParams.originalKeyword;
      params.type = this.queryParams.typeKeyword;
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.orderByDescColumn = this.orderByDescColumn
      params.orderByAscColumn = this.orderByAscColumn
      getBlogList(params).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.tableData = response.data.records;
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
          this.total = response.data.total;
        }
      });
    },

    getFormObject: function() {
      var formObject = {
        uid: null,
        title: null,
        summary: null,
        content: null,
        tagUid: null,
        fileUid: null,
        isOriginal: this.blogOriginalDefault, //是否原创
        isPublish: this.blogOriginalDefault, //是否发布
        type: this.blogTypeDefault, // 文章类型
        author: null, //作者
        level: parseInt(this.blogLevelDefault), //推荐等级，默认是正常
        openComment: this.openDefault, // 是否启动
        articlesPart: null //文章出处，默认蘑菇博客
      };
      return formObject;
    },
    // 跳转到该博客详情
    onClick: function(row) {
      if(row.isPublish === 0) {
        this.$message.error("文章暂未发布, 无法进行浏览")
        return
      }
      window.open( this.BLOG_WEB_URL + "/#/info?blogOid=" + row.oid);
    },



    // // 获取系统配置
    // getSystemConfigList: function() {
    //   getSystemConfig().then(response => {
    //     if (response.code == this.$ECode.SUCCESS) {
    //       if (response.data) {
    //         this.systemConfig = response.data;
    //       }
    //     }
    //   });
    // },
    //关闭模态框
    cancelModel() {
      this.photoVisible = false;
    },
    deletePhoto: function() {
      this.form.photoList = null;
      this.form.fileUid = "";
    },

    submitStr: function(str, index) {
      if (str.length > index) {
        return str.slice(0, index) + "...";
      }
      return str;
    },
    },
    cancelSubjectSelect: function() {
      this.subjectVisible = false
    },
    handleFind: function() {
      this.currentPage = 1
      this.blogList();
    },
    handleDelete: function(row) {
      var that = this;
      this.$confirm("此操作将把博客删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          var params = {};
          params.uid = row.uid;
          deleteBlog(params).then(response => {
            that.$commonUtil.message.success(response.message)
            that.blogList();
          });
        })
        .catch(() => {
          that.$commonUtil.message.info("已取消删除")
        });
    },
    handleDeleteBatch: function(row) {
      var that = this;
      if(that.multipleSelection.length <= 0 ) {
        that.$commonUtil.message.error("请先选中需要删除的博客")
        return;
      }
      this.$confirm("此操作将把选中博客删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          deleteBatchBlog(that.multipleSelection).then(response => {
          });
        })
        .catch(() => {
          that.$commonUtil.message.info("已取消删除")
        });
    },
    handleCurrentChange: function(val) {
      this.currentPage = val;
      this.blogList();
    },

    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val;
    }
};
</script>
<style scoped>

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  margin: 0 0 0 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width:  195px;
  height: 105px;
  line-height: 105px;
  text-align: center;
}
.imgBody {
  width:  195px;
  height: 105px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}
.uploadImgBody {
  margin-left: 5px;
  width:  195px;
  height: 105px;
  border: dashed 1px #c0c0c0;
  float: left;
  position: relative;
}
.uploadImgBody :hover {
  border: dashed 1px #00ccff;
}
.inputClass {
  position: absolute;
}
.el-dialog__body {
  padding-top: 10px;
  padding-bottom: 0;
}
.el-dialog {
  min-height: 400px;
}
.el-upload__tip {
  margin-top: 10px;
  margin-left: 10px;
  color: #3e999f;
}

.upload-demo {
  margin-top: 50px;
}
.tipBox {
  margin-bottom: 30px;
}
.tip {
  font-size: 14px;
  font-weight: bold;
  color: 	#808080;
}
.tipItem {
  line-height: 22px;
  color: 	#A9A9A9;
}
</style>
