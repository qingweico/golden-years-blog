<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">浏览历史</span>
        <el-dropdown trigger="click" @command="handleCommand" v-if="historyList.length">
        <span class="el-dropdown-link">
          清除记录<i class="el-icon--right"></i>
        </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="all">所有时间</el-dropdown-item>
            <el-dropdown-item command="one_hour">最近一个小时</el-dropdown-item>
            <el-dropdown-item command="one_day">最近一天</el-dropdown-item>
            <el-dropdown-item command="three_day">最近三天</el-dropdown-item>
            <el-dropdown-item command="one_week">最近一周</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <div class="history-list-wrapper" v-if="historyList.length" v-for="(history, index) in historyList" :key="index">
      <el-card class="box-card" shadow="hover" style="margin-bottom: 10px">
        <el-descriptions direction="horizontal" :colon=false size="mini" :column=6>
          <el-descriptions-item>
            <span @click="goToDetail(history.articleId)" style="font-weight: bold;font-size: 20px">{{ history.articleName }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <span>{{ history.browseTime }}</span>
          </el-descriptions-item>
          <el-descriptions-item style="display: inline">
            <el-avatar :src="history.articleAuthorFace"></el-avatar>
          </el-descriptions-item>
          <el-descriptions-item style="display: inline">
            <span @click="goUserHomePage(history.userId)"> {{ history.articleAuthorName }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <span>{{ history.articleReadCounts }} 阅读 - {{ history.articleCollectCounts }}
              收藏  - {{ history.articleStarCounts }} 点赞 - {{ history.articleCommentCounts }} 评论</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
    <el-empty description="你暂时还没有阅读任何文章, 快去找一篇吧" v-if="!historyList.length"
              style=" background-color: white;"></el-empty>


    <!--分页-->
    <div class="block paged" v-if="historyList.length">
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
import {deleteHistory, getHistoryList} from "@/api/center";
import {mapGetters} from "vuex";

export default {
  data() {
    return {
      historyList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  }, methods: {
    getHistoryList() {
      let params = new URLSearchParams();
      params.append("userId", this.getUserInfo.id);
      params.append("pageNum", this.currentPage);
      params.append("pageSize", this.pageSize);
      getHistoryList(params).then((response) => {
        this.historyList = response.data.rows;
        this.total = response.data.records;
      })
    },
    goUserHomePage(userId) {
      let routeData = this.$router.resolve({
        path: "/homepage",
        query: {id: userId}
      });
      window.open(routeData.href, '_self');
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getHistoryList();
    },
    // 跳转到文章详情
    goToDetail(articleId) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: articleId}
      })
      window.open(routeData.href, '_blank');
    },
    // 点击头像触发的动作
    handleCommand(command) {
      let deleteModel = "";
      let params = {};
      params.userId = this.getUserInfo.id;
      if (command === "all") {
        deleteModel = "";
      } else if (command === "one_hour") {
        deleteModel = 0;
      } else if (command === "one_day") {
        deleteModel = 1;
      } else if (command === "three_day") {
        deleteModel = 2;
      } else if (command === "one_week") {
        deleteModel = 3;
      }
      params.deleteModel = deleteModel;
      deleteHistory(params).then((response) => {
        this.$message.success(response.msg);
        this.getHistoryList();
      })
    },
  },
  computed: {
    ...mapGetters(['getUserInfo']),
  },
  created() {
    this.getHistoryList();
  }
}
</script>

<style scoped>
.history-list-wrapper {
  background-color: white;
  margin-top: 20px;
}

.el-dropdown {
  float: right;
  margin-right: 10px;
}
.el-descriptions {
  cursor: pointer;
}
.el-dropdown-link {
  cursor: pointer;
}

.el-icon-arrow-down {
  font-size: 12px;
}
</style>