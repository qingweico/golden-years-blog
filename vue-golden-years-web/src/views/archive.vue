<template>
  <div>
    <div class="pagebg sorts"></div>
    <div class="container">

      <h1 class="t_nav">
        <span>有些的时候，正是为了爱才悄悄躲开。躲开的是身影，躲不开的却是那份默默的情怀。</span>
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
                  inactive-color="#13ce66"
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
                  placement="top"
              >
                <el-card>
                  <h4 @click="goToList('blogContent', item)" class="itemTitle">{{ item.title }}</h4>
                  <br>
                  <!--文章类别-->
                  <el-tag
                      class="elTag"
                      type="success"
                      v-if="item.categoryId != null"
                      @click="goToList('category', item.categoryId)"
                  >{{ item.blogSort.sortName }}
                  </el-tag>
                  <el-tag
                      class="elTag"
                      v-for="tagItem in item.tagList"
                      v-if="tagItem != null"
                      :key="tagItem.id"
                      style="margin-left: 3px;"
                      @click="goToList('tag', tagItem)"
                      type="warning"
                  >{{ tagItem.content }}
                  </el-tag>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {getBlogByTime, getArchiveTimeList} from "@/api";
import {mapGetters} from "vuex";

export default {
  data() {
    return {
      selectContent: "",
      reverse: true,
      activities: [],
      itemByDate: [],
      articleByDate: {},
      currentPage: 1,
      pageSize: 10,
      total: 0,
      records: 0,
    };
  },
  components: {},
  mounted() {
  },
  created() {

    getArchiveTimeList(this.getUserInfo().id).then(response => {
      if (response.data.success) {
        let activities = response.data.data;
        let result = [];
        for (let i = 0; i < activities.length; i++) {
          let temp = activities[i].replace("年", "-").replace("月", "-") + "1";
          let dataForDate = {content: activities[i], timestamp: temp};
          result.push(dataForDate);
        }
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
      let params = new URLSearchParams();
      params.append("monthAndYear", this.selectContent);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getBlogByTime(params).then(response => {
        console.log(response.data);
        if (response.data.success) {
          this.itemByDate = response.data.data;
        }
      });
    },
    //跳转到搜索详情页
    goToList(type, entity) {
      switch (type) {
        case "tag": {
          let routeData = this.$router.resolve({
            path: "/list",
            query: {tagUid: entity.id}
          });
          window.open(routeData.href, "_blank");
        }
          break;
        case "category": {
          let routeData = this.$router.resolve({
            path: "/list",
            query: {id: entity.categoryId}
          });
          window.open(routeData.href, "_blank");
        }
          break;
        case "detail": {
        }
          break;
      }
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
.sortBox {
  color: #555;
}

.sortBoxSpan {
  cursor: pointer;
}

.sortBoxSpan:hover {
  color: #409eff;
}

.sortBoxSpanSelect {
  color: #409eff;
}

.itemTitle {
  cursor: pointer;
}

.itemTitle:hover {
  color: #409eff;
}

.elTag {
  cursor: pointer;
}
</style>
