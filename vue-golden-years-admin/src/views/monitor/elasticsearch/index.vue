<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-button class="button" type="success" @click="handInit" icon="el-icon-refresh">初始化ES索引</el-button>
    </div>
    <iframe id="myIframe" :src="elasticSearchServiceUrl" width="100%" height="750px;"></iframe>
  </div>
</template>

<script>

import {Loading} from "element-ui";

export default {
  data() {
    return {
      elasticSearchServiceUrl: "http://119.29.35.129:9200"
    };
  },
  mounted() {
    let iframe = document.getElementById("myIframe")
    let uploadLoading = Loading.service({
      lock: true,
      text: '正在努力加载中……'
    })
    if (iframe.attachEvent) {
      iframe.attachEvent("onload", function () {
        uploadLoading.close();
      });
    } else {
      iframe.onload = function () {
        uploadLoading.close();
      };
    }
  },
  created() {

  },
  methods: {
    handInit: function () {
      this.$confirm("此操作将初始化全部索引, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {

      })

    }
  }
};
</script>
