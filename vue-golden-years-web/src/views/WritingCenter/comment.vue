<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">评论管理</span>
      </div>
    </div>

    <div class="comment-list-wrapper" v-if="commentList.length" v-for="(comment, index) in commentList" :key="index">
      <el-card class="box-card" shadow="hover" style="margin-bottom: 10px">
        <el-descriptions direction="horizontal" :colon=false size="mini" :column=4>
          <el-descriptions-item>
            <span @click="goToDetail(comment.articleId)"
                  style="font-weight: bold;font-size: 20px">{{ comment.articleTitle }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <span>{{ comment.content }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <span> {{ comment.createTime }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <span @click="deleteComment(comment.id)"> <i class="el-icon-delete"> 删除</i></span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
    <el-empty description="你暂时还没有发表任何评论" v-if="!commentList.length" style=" background-color: white;"></el-empty>


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
    goToDetail(articleId) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: articleId}
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
.comment-list-wrapper {
  margin-top: 20px;
}

.el-descriptions {
  cursor: pointer;
  margin-left: 30px;
  padding-top: 10px;
  font-weight: bold;
  color: black;
  font-size: 16px;
}

.el-descriptions-item span {
  float: left;
  font-size: 16px;
  padding: 0 0 0 10px;
  color: #748594;
  line-height: 1.5;
  display: inline-block;
}

</style>