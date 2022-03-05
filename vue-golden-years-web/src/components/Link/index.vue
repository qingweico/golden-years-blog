<template>
  <div class="links" v-if="linkData.length > 0">
    <h2 class="hometitle">友情链接</h2>
    <ul>
      <li v-for="item in linkData" :key="item.id">
        <a :href="item.linkUrl">{{ item.linkName }}</a></li>
    </ul>
  </div>
</template>

<script>
import {getLink} from "@/api";

export default {
  name: "FriendLink",
  data() {
    return {
      linkData: [],
    };
  },
  created() {
    getLink().then(response => {
      if (response.data.success) {
        this.linkData = response.data.data;
      } else {
        this.$message.error(response.data.msg);
      }
    });
  },
  methods: {}
};
</script>
<style>
</style>
