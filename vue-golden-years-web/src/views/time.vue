<template>
  <div>
    <div class="pagebg timer"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="javascript:void(0);" class="n2">时间轴</a>
      </h1>
      <div class="timebox">
        <ul id="list" v-infinite-scroll="load">
          <li v-for="item in newBlogData" :key="item.id">
            <span>{{ formatDate(item.createTime) }}</span>
            <a
                href="javascript:void(0);"
                @click="goToInfo(item.id)"
                :title="item.title"
            >{{ item.title }}</a>
          </li>
        </ul>
        <ul id="list2"></ul>
      </div>
    </div>
  </div>
</template>

<script>
import {timeline} from "@/api";
import {mapGetters} from "vuex";

export default {
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      newBlogData: [],
      isEnd: false,
      userInfo: {}
    };
  },
  components: {},
  mounted() {

  },
  created() {
    this.userInfo = this.getUserInfo();
    let params = new URLSearchParams();
    params.append("userId", this.userInfo.id);
    params.append("page", this.currentPage);
    params.append("pageSize", this.pageSize);
    timeline(params).then(response => {
      console.log(response.data)
      if (response.data.success) {
        let content = response.data;
        this.newBlogData = content.data.rows;
        this.total = content.data.total;
        this.currentPage = content.data.currentPage;
        this.isEnd = false;
      } else {
        this.$message.error(response.data.msg);
      }

    });
  },
  methods: {
    ...mapGetters(['getUserInfo']),
    // 跳转到文章详情
    goToInfo(id) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: id}
      });
      window.open(routeData.href, "_blank");
    },
    load() {
      let params = new URLSearchParams();
      const that = this;
      let loading = true;
      params.append("userId", this.userInfo.id);
      params.append("page", (this.currentPage + 1).toString());
      params.append("pageSize", this.pageSize);
      timeline(params).then(response => {
        if (response.data.success && response.data.data.rows.length > 0) {
          that.isEnd = false;
          let content = response.data;
          that.newBlogData = that.newBlogData.concat(content.data.rows);
          that.total = content.data.data.total;
          that.currentPage = content.data.data.currentPage;
        } else {
          that.isEnd = true;
        }
        loading = false;
      });
    },
    formatDate: function (time) {
      let date = new Date(time);
      let year = date.getFullYear();
      // 1~9月份加前缀0
      let month = date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1;
      let day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
      // 拼接
      return year + "-" + month + "-" + day;
    }
  }
};
</script>


<style>
.isEnd {
  width: 100%;
  height: 40px;
  line-height: 40px;
  text-align: center;
}
</style>
