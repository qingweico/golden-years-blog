<template>
  <div class="cloud" v-if="categoryList.length > 0">
    <h2 class="home_title">类别云</h2>
    <ul>
      <a v-for="item in categoryList" :key="item.id"
         @click="goToList(item.id)">{{ item.name }}({{ item.eachCategoryArticleCount }})</a>
    </ul>
  </div>
</template>

<script>
import {getBlogCategoryWithBlogAmount} from "@/api";

export default {
  name: "CategoryCloud",
  data() {
    return {
      categoryList: []
    };
  },
  created() {
    getBlogCategoryWithBlogAmount().then(response => {
      this.categoryList = response.data;
    });
  },
  methods: {
    //跳转到类别列表详情页
    goToList(uid) {
      // let routeData = this.$router.resolve({path: "/list", query: {tagUid: uid}});
      //window.open(routeData.href, '_blank');
    },
  }
};
</script>

<style>
</style>
