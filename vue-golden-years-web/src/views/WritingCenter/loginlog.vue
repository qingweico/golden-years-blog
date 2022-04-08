<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">登录日志</span>
      </div>
    </div>
    <div id="log-list-wrapper" class="log-list-wrapper">
      <div class="log-list">
        <span>只展示最近一周的登陆日志信息</span>
        <span style="color:red">*</span>
        <table class="table-log">
          <tr class="head-th">
            <th>登录时间</th>
            <th>登陆位置</th>
            <th>IP</th>
            <th>浏览器</th>
            <th>操作系统</th>
          </tr>
          <tr class="log-single-line" v-for="(loginLog) in logList">
            <td>{{ loginLog.loginTime }}</td>
            <td>{{ loginLog.loginLocation }}</td>
            <td>{{ loginLog.ipaddr }}</td>
            <td>{{ loginLog.browser }}</td>
            <td>{{ loginLog.os }}</td>
          </tr>
        </table>
      </div>
    </div>

    <!--分页-->
    <div class="block paged">
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
import {getLoginLogList} from '@/api/user'
import {mapGetters} from "vuex";

export default {
  name: "loginlog",
  data() {
    return {
      logList: [],
      userId: "",
      currentPage: 1,
      pageSize: 10,
      total: 0,
      totalPage: 0

    }
  },
  methods: {
    ...mapGetters(['getUserInfo']),

    queryLoginLogList() {
      let params = new URLSearchParams();
      params.append("userId", this.userId);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getLoginLogList(params).then(response => {
        let content = response.data;
        this.logList = content.rows;
        this.total = content.records;
        this.totalPage = content.totalPage;
        this.currentPage = content.currentPage;
      })
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.queryLoginLogList();
    },
  },
  created() {
    this.userId = this.getUserInfo().userId;
    this.queryLoginLogList();
  }
}
</script>

<style scoped>
.log-list-wrapper {
  background-color: #fff;
  padding: 20px 25px;
}

.log-list span {
  font-size: 8px;
  float: right;
  margin-bottom: 10px;
}

.table-log {
  width: 900px;
  margin-top: 20px;
  border-collapse: collapse;
  border: 1px solid #e8e8e8;
  color: #222;
}

.head-th {
  height: 50px;
  background-color: #f4f4f4;
  width: 15%;
  text-align: center;
  padding-left: 10px;
  color: #222;
}

.log-single-line {
  border: 1px solid #e8e8e8;
  height: 50px;
  text-align: center;
  color: #222;
}

.log-single-line:hover {
  background-color: #f4f4f4;
}
</style>