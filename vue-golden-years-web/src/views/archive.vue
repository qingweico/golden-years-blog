<template>
  <div>
    <div class="page_bg sorts"></div>
    <div class="container">

      <h1 class="t_nav">
        <span>种一棵树最好的时间是十年前,其次是现在</span>
        <a href="/" class="n1">网站首页</a>
        <a href="javascript:void(0);" class="n2">归档</a>
      </h1>

      <div class="sortBox">
        <div class="time">
          <div class="block">
            <div class="radio" style="margin-bottom:20px;">
              <el-switch
                  v-model="reverse"
                  active-text="倒序"
                  inactive-text="正序"
                  active-color="#000000"
                  inactive-color="#920784"
              ></el-switch>
            </div>
            <el-timeline :reverse="reverse">
              <el-timeline-item v-for="(activity, index) in activities" hide-timestamp :key="index">
                <span
                    @click="clickTime(activity.content)"
                    :class="[activity.content === selectContent ? 'sortBoxSpan sortBoxSpanSelect' : 'sortBoxSpan']"
                >{{ activity.content }}</span>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>

        <div class="article">
          <div class="block">
            <el-timeline>
              <el-timeline-item
                  v-for="item in itemByDate"
                  :key="item.timestamp"
                  :timestamp="item.createTime"
                  placement="top">
                <el-card>
                  <h4 @click="goToList(item)" class="itemTitle">{{ item.title }}</h4>
                  <br>
                  <!--文章类别-->
                  <el-tag
                      class="elTag"
                      type="success"
                      v-if="item.categoryId != null"
                  >{{ getBlogCategoryNameById(item.categoryId) }}
                  </el-tag>
                  <el-tag
                      class="elTag"
                      v-for="tag in item.tagList"
                      v-if="tag != null"
                      :key="tag.id"
                      style="margin-left: 3px; color: white"
                      :color="tag.color"
                      type="warning"
                  >{{ tag.name }}
                  </el-tag>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>

          <div class="block paged">
            <el-pagination
                @current-change="handleCurrentChange"
                :current-page.sync="currentPage"
                :page-size="pageSize"
                layout="total, prev, pager, next, jumper"
                :total="records">
            </el-pagination>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {getBlogByTime, getArchiveTimeList, getBlogCategory} from "@/api";
import {mapGetters} from "vuex";

export default {
  data() {
    return {
      selectContent: "",
      reverse: true,
      activities: [],
      categoryList: [],
      itemByDate: [],
      articleByDate: {},
      currentPage: 1,
      pageSize: 5,
      totalPage: 0,
      records: 0,
    };
  },
  components: {},
  mounted() {
  },
  created() {
    getBlogCategory().then(response => {
      this.categoryList = response.data;
    });
    getArchiveTimeList(this.getUserInfo().id).then(response => {
      let activities = response.data;
      let result = [];
      for (let i = 0; i < activities.length; i++) {
        let temp = activities[i].replace("年", "-").replace("月", "-") + "1";
        let dataForDate = {content: activities[i], timestamp: temp};
        result.push(dataForDate);
        this.activities = result;
        // 默认选择最后一个
        this.clickTime(activities[activities.length - 1]);
      }
    });

  },
  methods: {
    ...mapGetters(['getUserInfo']),
    clickTime(content) {
      this.selectContent = content;
      this.getArchiveArticleList();
    },
    getArchiveArticleList() {
      let params = new URLSearchParams();
      params.append("monthAndYear", this.selectContent);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getBlogByTime(params).then(response => {
        this.itemByDate = response.data.rows;
        this.records = response.data.records;
        this.totalPage = response.data.totalPage;
      });
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.categoryList;
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].name;
        }
      }
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getArchiveArticleList();
    },
    //跳转到搜索详情页
    goToList(entity) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: entity.articleId}
      });
      window.open(routeData.href, "_blank");
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
.sortBox {
  color: #555;
}

.sortBoxSpan {
  cursor: pointer;
}

.sortBoxSpan:hover {
  color: #920784;
}

.sortBoxSpanSelect {
  color: #920784;
}

.itemTitle {
  cursor: pointer;
}

.itemTitle:hover {
  color: #920784;
}

.elTag {
  cursor: pointer;
}
</style>
