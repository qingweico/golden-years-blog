<template>
  <div class="cloud" v-if="hotTagData.length > 0">
    <h2 class="home_title">标签云</h2>
    <ul>
      <a v-for="item in hotTagData" :key="item.uid" href="javascript:void(0);"
         @click="goToList(item.id)">{{ item.name }}</a>
    </ul>
  </div>
</template>

<script>
import {getHotTag} from "@/api";
import {getSystemConfig} from "@/api/center";

export default {
  name: "TagCloud",
  data() {
    return {
      hotTagData: [],
      systemConfig: {}
    };
  },
  created() {
    getHotTag().then(response => {
      this.hotTagData = response.data;
    });
    getSystemConfig().then((response) => {
      this.systemConfig = response.data;
    });
  },
  methods: {
    //跳转到搜索详情页
    goToList(tagId) {
      this.$router.push({path: "/", query: {tagId: tagId, searchModel: this.systemConfig.searchModel}});
    },
  }
};
</script>

<style>
</style>
