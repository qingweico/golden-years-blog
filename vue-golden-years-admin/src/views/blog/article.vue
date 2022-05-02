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
        <el-select
            v-model="queryParams.tagId"
            style="width: 140px"
            filterable
            clearable
            reserve-keyword
            placeholder="请选择标签"
            @keyup.enter.native="handleFind"
            :loading="loading">
          <el-option
              v-for="item in tagNameOptions"
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
        <el-select v-model="queryParams.deleteStatus" clearable placeholder="逻辑状态" style="width:110px">
          <el-option value="0" label="未删除"></el-option>
          <el-option value="1" label="已删除"></el-option>
        </el-select>
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
        <el-button style="margin-left: 10px;" class="filter-item" type="primary" icon="el-icon-search"
                   @click="handleFind">查找
        </el-button>
        <el-button icon="el-icon-refresh" @click="refresh">重置</el-button>
      </el-form>
    </div>
    <el-table :data="tableData"
              ref="articleTable"
              style="width: 100%">
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

          <el-popover
              placement="right"
              width="50"
              trigger="hover">
            <div class="author-info">
              <div class="author-pic">
                <el-avatar shape="square" :size="50" :src="scope.row.authorFace"></el-avatar>
              </div>
              <div class="author-fans">
                <span>关注数: {{ scope.row.followCounts }}</span>
              </div>
              <div class="author-attention">
                <span>粉丝数: {{ scope.row.fansCounts }}</span>
              </div>
            </div>
            <span slot="reference" style="cursor: pointer">{{ scope.row.authorName }}</span>
          </el-popover>


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
          <span>{{ getBlogCategoryNameById(scope.row.categoryId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="逻辑状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isDelete === 0">未删除</el-tag>
          <el-tag type="danger" v-if="scope.row.isDelete === 1">已删除</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.updateTime }}</span>
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
      <el-table-column label="操作" fixed="right" min-width="100" align="center">
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
import {
  conditionalQueryArticle,
  getCategoryList,
  getAllTag,
  withdrawDelete,
  deleteArticle,
  reReview,
  reviewStatus
} from "@/api/blog";

const querystring = require("querystring");

export default {
  components: {},
  data() {
    return {
      WEB_URL: "http://localhost:9527",
      // 搜索条件
      queryParams: {
        keyword: "",
        categoryId: "",
        tagId: "",
        status: "",
        deleteStatus: "",
        startDateStr: "",
        endDateStr: "",
      },
      dateRange: "",
      //多选, 用于批量删除
      multipleSelection: [],
      // 博客分类候选框
      categoryNameOptions: [],
      // 博客标签候选框
      tagNameOptions: [],
      // 搜索框加载状态
      loading: false,
      // 博客数据
      tableData: [],
      currentPage: 1,
      pageSize: 10,
      totalPage: 0,
      records: 0
    };
  },
  created() {
    this.loading = true;
    // 获取博客类别
    getCategoryList().then(res => {
      this.loading = false;
      this.categoryNameOptions = res.data;
    });
    // 获取博客标签
    getAllTag().then(res => {
      this.loading = false;
      this.tagNameOptions = res.data;
    });
    // 接受从dashboard类别饼状图中传来的categoryId
    let categoryId = this.$route.query.categoryId;
    // 接受从dashboard标签饼状图中传来的tagId
    let tagId = this.$route.query.tagId;
    if (categoryId !== undefined && categoryId !== "") {
      this.queryParams.categoryId = categoryId;
      this.blogList();
    } else if (tagId !== undefined && tagId !== "") {
      this.queryParams.tagId = tagId;
      this.blogList();
    } else {
      this.blogList();
    }
  },
  methods: {
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.categoryNameOptions;
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
    refresh() {
      this.queryParams = {
        keyword: "",
        categoryId: "",
        status: "",
        tagId:"",
        deleteStatus: "",
        startDateStr: "",
        endDateStr: "",
      };
      this.dateRange = "";
      this.blogList();
    },
    blogList() {
      let params = new URLSearchParams();
      params.append("keyword", this.queryParams.keyword.trim());
      params.append("categoryId", this.queryParams.categoryId);
      params.append("status", this.queryParams.status);
      params.append("tagId", this.queryParams.tagId);
      params.append("deleteStatus", this.queryParams.deleteStatus);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      params.append("startDateStr", this.queryParams.startDateStr);
      params.append("endDateStr", this.queryParams.endDateStr);
      conditionalQueryArticle(params).then(response => {
        let content = response.data;
        this.tableData = content.rows;
        this.totalPage = content.totalPage;
        this.records = content.records;
      });
    },

    // 跳转到该文章详情
    onClick(row) {
      if (row.articleStatus !== 2) {
        this.$message.error("文章暂未审核或者审核未通过, 无法进行浏览")
        return;
      }
      if (row.isDelete === 1) {
        this.$message.error("文章已经删除, 无法进行浏览")
        return;
      }
      window.open(this.WEB_URL + "/detail?id=" + row.id, "_blank");
    },
    submitStr(str, index) {
      if (str.length > index) {
        return str.slice(0, index) + "...";
      }
      return str;
    },
    handleFind() {
      this.currentPage = 1;
      this.blogList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.blogList();
    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val;
    }
  },

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
