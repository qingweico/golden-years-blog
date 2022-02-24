<template>
  <div class="main-page">

    <!--条件搜索-->
    <div class="search-box">
      <div class="status-wrapper">
        <el-form :inline="true" label-width="68px" style="margin-bottom: 18px;">
          <el-input
              clearable
              class="filter-item"
              style="width: 130px;  margin-left: 10px;"
              v-model="queryParams.keyword"
              placeholder="博客标题"
              @keyup.enter.native="handleFind"
          ></el-input>
          <el-input
              clearable
              class="filter-item"
              style="width: 130px;  margin-left: 10px;"
              v-model="queryParams.keyword"
              placeholder="文章类别 "
              @keyup.enter.native="handleFind"
          ></el-input>
          <el-select v-model="queryParams.blogStatusType" clearable placeholder="文章状态"
                     style="width:130px; margin-left: 10px;">
            <el-option key="0" value="全部" checked></el-option>
            <el-option key="1" value="审核中"></el-option>
            <el-option key="2" value="已发布"></el-option>
            <el-option key="3" value="未通过"></el-option>
            <el-option key="4" value="已撤回"></el-option>
          </el-select>

          <el-date-picker
              v-model="value2"
              type="datetimerange"
              :picker-options="pickerOptions"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              align="right" style="width: 400px; margin-left: 10px;">
          </el-date-picker>

          <el-button style="margin-left: 10px;" class="filter-item" type="primary" icon="el-icon-search"
                     @click="handleFind">查找
          </el-button>
        </el-form>
      </div>
    </div>
    <!--文章结果展示-->
    <div id="article-list-wrapper" class="article-list-wrapper">
      <div class="article-list">
        <div class="every-article" v-for="(article, index) in articleList" :key="index">
          <img :src="article.articleCover" style="width: 175px; height: 125px;"
               v-show="article.articleType === 1" alt="cover"/>
          <div class="main-content">
            <div class="basic-info">
              <span><a href="javascript:void(0);" target="_blank" class="article-link">{{ article.title }}</a></span>
              <span class="counts-wrapper">
										<span>阅读 0 ⋅</span>
										<span>评论 0 ⋅</span>
									</span>
              <span class="status-reviewing article-status"
                    v-show="article.articleStatus === 1 || article.articleStatus === 2">审核中</span>
              <span class="status-published article-status"
                    v-show="article.articleStatus === 3">已发布</span>
              <span class="status-fail article-status"
                    v-show="article.articleStatus === 4">审核未通过</span>
              <span class="status-back article-status" v-show="article.articleStatus === 5">已撤回</span>
              <span class="publish-time">
										<span v-show="article.isAppoint === 1">预计发布时间:</span>
                <!--										{{ formatData(article.publishTime) }}-->
									</span>
            </div>
            <div class="operation">
              <a href="javascript:void(0);" style="margin-top: 8px;" @click="withdraw(article.id)"
                 v-show="article.articleStatus === 3">
                <span class="oper-words">撤回</span>
              </a>
              <a href="javascript:void(0);" style="margin-top: 8px;"
                 @click="deleteArticle(article.id)"
                 v-show="article.articleStatus === 3 || article.articleStatus === 4">
                <span class="oper-words">删除</span>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>

</template>

<script>
export default {
  name: "manage",
  data() {
    return {
      startDateStr: "",
      endDateStr: "",
      queryParams: {},
      // 文章列表
      articleList: [],
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      value1: [new Date(2000, 10, 10, 10, 10), new Date(2000, 10, 11, 10, 10)],
      value2: ''
    }
  },
  methods: {
    handleFind() {

    }
  }
}
</script>

<style scoped>
.main-page {
  width: 980px;
  margin-left: 20px;
}

.search-box {
  background-color: white;

  display: flex;
  flex-direction: column;
}

.status-wrapper {
  margin-top: 25px;
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}


/* 文章列表 start */

.article-list-wrapper {
  margin-top: 15px;
  background-color: #fff;
  padding: 20px 25px;
}

.every-article {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;

  /* margin-top: 20px; */
  padding: 20px 0;

  border-bottom: 1px solid #d5d2d2;
}


.main-content {
  /* width: 720px; */
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;

  margin-left: 20px;
  margin-right: 20px;
}

.basic-info {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 610px;
  margin-top: 10px;
}

.article-link {
  font-size: 16px;
  color: #222;
}

.operation {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

.counts-wrapper {
  margin-top: 8px;
  color: #6b6464;
}

.article-status {
  font-size: 12px;
  padding: 1px;

  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 8px;
}

.status-reviewing {
  color: orange;
  background-color: rgb(242, 222, 191);
  width: 50px;
}

.status-back {
  color: rgb(136, 135, 135);
  background-color: rgb(215, 214, 214);
  width: 50px;
}

.status-fail {
  color: rgb(225, 68, 68);
  background-color: rgb(240, 209, 209);
  width: 70px;
}

.status-published {
  color: rgb(34, 151, 34);
  background-color: rgb(187, 243, 187);
  width: 50px;
}

.publish-time {
  color: gray;
  margin-top: 8px;
}

.cp-icon-wrapper {
  /* padding-top: 1px; */
}

.link-opers {
  display: flex;
  justify-content: flex-start;
}

.oper-icon {
  width: 16px;
  height: 16px;
  /* margin-right: 5px; */
  align-self: center;
}

.oper-words {
  font-size: 14px;
}
</style>