<template>
  <div>
    <div class="page_bg timer"></div>
    <div class="container">
      <h1 class="t_nav">
        <span>我们是一群没有终点,找不到归宿的鹰,我们只能够留下在捕猎中殷勤的身影</span>
        <a href="/" class="n1">网站首页</a>
        <a href="javascript:void(0);" class="n2">时间轴</a>
      </h1>
      <div class="time_box">
        <ul id="list" v-infinite-scroll="load">
          <li v-for="item in newBlogData" :key="item.id">
            <span>{{ formatDate(item.createTime) }}</span>
            <a
                href="javascript:void(0);"
                @click="goToDetail(item.id)"
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
      let content = response.data;
      this.newBlogData = content.rows;
      this.total = content.total;
      this.currentPage = content.currentPage;
      this.isEnd = false;

    });
  },
  methods: {
    ...mapGetters(['getUserInfo']),
    // 跳转到文章详情
    goToDetail(id) {
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
        if (response.data.rows.length > 0) {
          that.isEnd = false;
          let content = response.data;
          that.newBlogData = that.newBlogData.concat(content.rows);
          that.total = content.total;
          that.currentPage = content.currentPage;
        } else {
          that.isEnd = true;
        }
        loading = false;
      });
    },
    formatDate(time) {
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
