<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">评论管理</span>
      </div>
    </div>

    <div class="comment-list-wrapper" v-if="commentList.length">
      <el-table :data="commentList"
                style="width: 100%">
        <el-table-column label="文章封面" width="200" align="center">
          <template slot-scope="scope">
            <img
                v-if="scope.row.articleCover"
                :src="scope.row.articleCover"
                style="width: 130px;height: 70px;"
                alt="">
          </template>
        </el-table-column>
        <el-table-column label="文章标题" width="200" align="center">
          <template slot-scope="scope">
            <span @click="goToDetail(scope.row.articleId)" style="cursor:pointer;">{{ scope.row.articleTitle }}</span>
          </template>
        </el-table-column>
        <el-table-column label="评论内容" width="200" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="200" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column
            fixed="right"
            label="操作"
            width="100">
          <template slot-scope="scope">
            <el-button @click="deleteComment(scope.row.id)" type="text" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-empty description="你暂时还没有发表任何评论" v-else style=" background-color: white;"></el-empty>


    <!--分页-->
    <div class="block paged" v-if="commentList.length">
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
import {deleteComment, getUserCommentList} from "@/api/center";
import {mapGetters} from "vuex";

export default {
  name: "comment",
  data() {
    return {
      commentList: [],
      userInfo: {},
      currentPage: 1,
      pageSize: 10,
      totalPage: 0,
      records: 0,
    }
  },
  created() {
    this.userInfo = this.getUserInfo();
    this.queryCommentList();
  },
  methods: {
    ...mapGetters(['getUserInfo']),
    deleteComment(commentId) {
      this.$confirm('是否删除当前评论', '确认信息', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteComment(commentId).then((response) => {
          this.$message.success(response.msg);
          this.queryCommentList();
        })
      })
    },
    queryCommentList() {
      let params = new URLSearchParams();
      params.append("userId", this.userInfo.id);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getUserCommentList(params).then(res => {
        const grid = res.data;
        this.commentList = grid.rows;
        this.currentPage = grid.currentPage;
        this.totalPage = grid.totalPage;
        this.records = grid.records;
      });
    },
    // 跳转到文章详情
    goToDetail(blogId) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: blogId}
      })
      window.open(routeData.href, '_blank');
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.queryCommentList();
    },
  }
}
</script>

<style scoped>

</style>