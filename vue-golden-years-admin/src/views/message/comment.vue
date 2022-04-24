<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-input clearable class="filter-item" style="width: 150px;" v-model="queryParams.userName"
                placeholder="评论用户昵称"></el-input>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind">查找</el-button>
      <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete">删除选中</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%" @selection-change="handleSelectionChange">

      <el-table-column type="selection"></el-table-column>

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="头像" width="150" align="center">
        <template slot-scope="scope">
          <img
              v-if="scope.row.user"
              :src="scope.row.user"
              onerror="onerror=null;src=defaultAvatar"
              style="width: 100px;height: 100px;"
              alt>
          <img
              v-else
              :src="defaultAvatar"
              style="width: 100px;height: 100px;"
              alt>
        </template>
      </el-table-column>

      <el-table-column label="评论用户" width="150" align="center">
        <template slot-scope="scope">
          <el-tag type="primary" v-if="scope.row.user" style="cursor: pointer;" @click.native="goUser(scope.row.user)">
            {{ scope.row.user.nickName }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="回复对象" width="150" align="center">
        <template slot-scope="scope">
          <el-tag type="info" v-if="scope.row" style="cursor: pointer;" @click.native="goUser(scope.row)">{{
              scope.row
            }}
          </el-tag>
          <el-tag type="info" style="cursor: pointer;" v-else>无</el-tag>
        </template>
      </el-table-column>


      <el-table-column label="内容" width="400" align="center">
        <template slot-scope="scope">
        </template>
      </el-table-column>

      <el-table-column label="评论时间" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="150">
        <template slot-scope="scope">
          <el-button @click="handleDelete(scope.row)" type="danger" size="small">删除
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
          :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import {getCommentList, deleteComment, deleteBatchComment} from "@/api/message/comment";

export default {
  data() {
    return {
      queryParams: {
        userName: ""
      },
      multipleSelection: [],
      tableData: [],
      keyword: "",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      totalPage: 0,
      dialogFormVisible: false,
      formLabelWidth: '120px',
      defaultAvatar: this.$SysConf.defaultAvatar
    };
  },
  created() {
    //this.commentList();
  },
  methods: {
    // 跳转到用户中心
    goUser(user) {
      this.$router.push({path: "/user/user", query: {nickName: user.nickName}});
    },
    // 跳转到该博客详情
    onClick(row) {

    },
    commentList() {
      let params = {}
      params.keyword = this.queryParams.content
      params.userName = this.queryParams.userName
      params.type = this.queryParams.type
      params.currentPage = this.currentPage
      params.pageSize = this.pageSize

      getCommentList(params).then(response => {
        if (response.data.success) {
          this.tableData = response.data.records;
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
          this.total = response.data.total;
        }
      });
    },
    subStr(str, index) {
      if (str === null || str === undefined) {
        return "";
      }
      if (str.length < index) {
        return str;
      } else {
        return str.substring(0, index) + "..."
      }
    },
    handleFind() {
      this.commentList();
    },
    handleDelete(row) {
      this.$confirm("此操作将把该评论删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteComment(row.id).then(response => {
          this.$message.success(response.msg)
          this.commentList();
        })
      })
    },
    handleDeleteBatch() {
      const that = this;
      if (that.multipleSelection.length <= 0) {
        this.$message({
          type: "error",
          message: "请先选中需要删除的内容！"
        });
        return;
      }
      this.$confirm("此操作将把选中的评论删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteBatchComment(that.multipleSelection).then(response => {
          that.$message.success(response.message)
          that.commentList();
        });
      })
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.commentList();
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    }
  }
};
</script>
<style>
</style>

