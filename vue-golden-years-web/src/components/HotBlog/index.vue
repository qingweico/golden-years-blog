<template>
  <div class="rank" v-if="hotBlogData.length > 0">
    <h2 class="home_title">点击排行</h2>
    <ul class="rank_pic" v-if="hotBlogData[0]">
      <i><img style="cursor:pointer" v-if="hotBlogData[0].articleCover" :src="hotBlogData[0].articleCover"
              @click="goToDetail(hotBlogData[0])" alt></i>
      <p><a href="javascript:void(0);" @click="goToDetail(hotBlogData[0])">{{ hotBlogData[0].title }}</a></p>
    </ul>
    <ul class="side_news">
      <li v-for="(item) in sideNews" :key="item.id">
        <i><img style="cursor:pointer" v-if="item.articleCover" :src="item.articleCover" @click="goToDetail(item)" alt></i>
        <p><a href="javascript:void(0);" @click="goToDetail(item)">{{ item.title }}</a></p>
        <span>{{ item.createTime }}</span>
      </li>
    </ul>
  </div>
</template>

<script>
import {rank} from "@/api/homepage";
import {getUrlVars} from "@/utils/util";

export default {
  name: "rank",
  data() {
    return {
      hotBlogData: [],
      articleId: "",
    };
  },
  created() {
    this.authorId = getUrlVars().id;
    rank(this.authorId).then(response => {
      this.hotBlogData = response.data.rows;
    });
  },
  computed: {
    // 添加一个计算属性用于简单过滤掉数组中第一个数据
    sideNews() {
      return this.hotBlogData.filter(blog =>
          this.hotBlogData.indexOf(blog) !== 0
      )
    }
  },
  methods: {
    goToDetail(blog) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: blog.id}
      });
      window.open(routeData.href, '_blank');
    },
  }
};
</script>

<style>

</style>
