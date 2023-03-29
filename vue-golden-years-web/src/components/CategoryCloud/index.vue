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
import {getCategoryListWithArticleCount} from "@/api";
import {getSystemConfig} from "@/api/center";

export default {
  name: "CategoryCloud",
  data() {
    return {
      categoryList: [],
      sysConfig:{}
    };
  },
  created() {
    getCategoryListWithArticleCount().then(response => {
      this.categoryList = response.data;
    });
    getSystemConfig().then((response) => {
      this.sysConfig = response.data;
    });
  },
  methods: {
    //跳转到类别列表详情页
    goToList(id) {
      this.$router.push({path: "/", query: {categoryId: id,searchModel: this.sysConfig.searchModel}});
    },
  }
};
</script>

<style>
</style>
