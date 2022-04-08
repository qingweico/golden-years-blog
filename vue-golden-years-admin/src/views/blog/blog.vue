<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-form :inline="true" label-width="68px" style="margin-bottom: 8px;">
        <el-input
            clearable
            class="filter-item"
            style="width: 150px;"
            v-model="queryParams.keyword"
            placeholder="请输入博客名"
            @keyup.enter.native="handleFind">
        </el-input>
        <el-select
            v-model="queryParams.categoryId"
            style="width: 140px"
            filterable
            clearable
            remote
            reserve-keyword
            placeholder="请选择分类名"
            @keyup.enter.native="handleFind"
            :loading="loading">
          <el-option
              v-for="item in categoryNameOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
          </el-option>
        </el-select>
        <el-select v-model="queryParams.status" clearable placeholder="文章状态" style="width:110px">
          <el-option value="0" label="全部" checked></el-option>
          <el-option value="1" label="审核中"></el-option>
          <el-option value="2" label="已发布"></el-option>
          <el-option value="3" label="未通过"></el-option>
          <el-option value="4" label="已撤回"></el-option>
        </el-select>
        <el-button style="margin-left: 10px;" class="filter-item" type="primary" icon="el-icon-search"
                   @click="handleFind">查找
        </el-button>
        <el-button icon="el-icon-refresh"  @click="refresh">重置</el-button>
      </el-form>
    </div>
    <el-table :data="tableData"
              ref="articleTable"
              style="width: 100%"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection"></el-table-column>

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="封面" width="160" align="center">
        <template slot-scope="scope">
          <img
              v-if="scope.row.articleCover"
              :src="scope.row.articleCover"
              style="width: 130px;height: 70px;"
              alt="">
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

      <el-table-column label="文章状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.articleStatus === 1">审核中</el-tag>
          <el-tag type="success" v-if="scope.row.articleStatus === 2">通过</el-tag>
          <el-tag type="danger" v-if="scope.row.articleStatus === 3">未通过</el-tag>
          <el-tag type="info" v-if="scope.row.articleStatus === 4">已撤回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="分类" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.categoryId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核" min-width="150" align="center">
        <template slot-scope="scope">
          <template v-if="scope.row.articleStatus === 2">
            <span>已审核</span>
          </template>
          <el-button v-if="scope.row.articleStatus === 1" @click="reviewStatus(scope.row, 1)" type="primary"
                     size="small">通过
          </el-button>
          <el-button v-if="scope.row.articleStatus === 1" @click="reviewStatus(scope.row, 0)" type="error" size="small">
            不通过
          </el-button>
          <el-button v-if="scope.row.articleStatus === 3" @click="reReview(scope.row)" type="info" size="small">重新审核
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" min-width="150" align="center">
        <template slot-scope="scope">
          <el-button @click="handleDelete(scope.row)" type="danger" size="small">删除</el-button>
          <el-button v-if="scope.row.isDelete === 1" @click="withdrawDelete(scope.row)" type="info" size="small">撤回删除
          </el-button>
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
          :total="records"></el-pagination>
    </div>
  </div>
</template>

<script>
import {getBlogCategory, deleteArticle, getBlogList, reReview, reviewStatus} from "@/api/blog";
import {getToken} from '@/utils/auth'
import {getCategoryList, withdrawDelete} from "../../api/blog";

const querystring = require("querystring");

export default {
  components: {},
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
      // 搜索条件
      queryParams: {
        keyword: "",
        categoryId: "",
        status: "",
        startDateStr: "",
        endDateStr: "",
      },
      showSearch: null, // 显示搜索条件
      //多选, 用于批量删除
      multipleSelection: [],
      // 标签候选框
      tagOptions: [],
      // 博客分类候选框
      categoryNameOptions: [],
      // 搜索框加载状态
      loading: false,
      // 文件上传loading
      uploadLoading: null,
      CKEditorData: null,
      // 博客数据
      tableData: [],
      // 标签数据
      tagData: [],
      // 保存选中标签id(编辑时)
      tagValue: [],
      blogSortData: [],
      currentPage: 1,
      pageSize: 10,
      totalPage: 0,
      records: 0,
      // 控制弹出框
      dialogFormVisible: false,
      formLabelWidth: "120px",
      // 一行的间隔数
      lineLabelWidth: "120px",
      maxLineLabelWidth: "100px",
      isEditForm: false,
      // 控制图片选择器的显示
      photoVisible: false,
      // 图片选择器是否首次显示(用于懒加载)
      isFirstPhotoVisible: true,
      photoList: [],
      fileIds: "",
      icon: false, //控制删除图标的显示
      interval: null, //定义触发器
      isChange: false, // 表单内容是否改变
      changeCount: 0, // 改变计数器

      fileList: [],
      localUploadVisible: false,
    };
  },
  created() {
    // 获取博客类别
    getCategoryList().then(res => {
      this.categoryNameOptions = res.data;
    });
    // 获取标签列表
    this.tagList()
    //获取博客列表
    this.blogList()
  },
  methods: {
    closeLoading() {
      this.uploadLoading.close()
    },
    handleCurrentChange() {
    },
    handleSelectionChange() {
    },
    handleEdit() {
    },

    // 审核文章
    reviewStatus(row, passOrNot) {
      let param = new URLSearchParams();
      param.append("articleId", row.id);
      param.append("passOrNot", passOrNot);
      reviewStatus(param).then(response => {
        this.$message.success(response.msg);
        this.blogList();
      })
    },
    // 重新审核文章
    reReview(row) {
      let param = new URLSearchParams();
      param.append("articleId", row.id);
      reReview(param).then(response => {
        this.$message.success(response.msg);
        this.blogList();
      })
    },
    // 删除文章
    handleDelete(row) {
      this.$confirm("此操作将把博客删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        let params = new URLSearchParams();
        params.append("articleId", row.id);
        deleteArticle(params).then(response => {
          this.$message.success(response.msg)
          this.blogList();
        });
      })
    },
    // 撤回删除
    withdrawDelete(row) {
      let params = new URLSearchParams();
      params.append("articleId", row.id);
      withdrawDelete().then(response => {
        this.$message.success(response.msg)
        this.blogList();
      })
    },
    handleFind() {
    },
    tagList() {
      let tagParams = {};
      tagParams.pageSize = 500;
      tagParams.currentPage = 1;
      // getTagList(tagParams).then(response => {
      //   this.tagData = response.data.records;
      //   this.tagOptions = response.data.records;
      // });
    },
    refresh() {
      this.queryParams = {
        keyword: "",
        categoryId: "",
        status: "",
        startDateStr: "",
        endDateStr: "",
      }
      this.blogList();
    },
    blogList() {
      let params = new URLSearchParams();
      params.append("keyword", this.queryParams.keyword);
      params.append("categoryId", this.queryParams.categoryId);
      params.append("status", this.queryParams.status);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      params.append("startDateStr", this.queryParams.startDateStr);
      params.append("endDateStr", this.queryParams.startDateStr);
      getBlogList(params).then(response => {
        let content = response.data;
        this.tableData = content.rows;
        this.currentPage = content.currentPage;
        this.totalPage = content.totalPage;
        this.records = content.records;
      });
    },

    // 跳转到该博客详情
    onClick(row) {
      if (row.isPublish === 0) {
        this.$message.error("文章暂未发布, 无法进行浏览")
        return
      }
      window.open(this.BLOG_WEB_URL + "/#/info?blogOid=" + row.oid);
    },

    //关闭模态框
    cancelModel() {
      this.photoVisible = false;
    },
    deletePhoto() {
      this.form.photoList = null;
      this.form.fileUid = "";
    },

    submitStr(str, index) {
      if (str.length > index) {
        return str.slice(0, index) + "...";
      }
      return str;
    },
  },
  cancelSubjectSelect: function () {
    this.subjectVisible = false
  },
  handleFind: function () {
    this.currentPage = 1
    this.blogList();
  },
  handleDeleteBatch(row) {
    const that = this;
    if (that.multipleSelection.length <= 0) {
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
  handleCurrentChange: function (val) {
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
  width: 195px;
  height: 105px;
  line-height: 105px;
  text-align: center;
}

.imgBody {
  width: 195px;
  height: 105px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}

.uploadImgBody {
  margin-left: 5px;
  width: 195px;
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
  color: #808080;
}

.tipItem {
  line-height: 22px;
  color: #A9A9A9;
}
</style>
