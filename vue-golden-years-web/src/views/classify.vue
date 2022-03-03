<template>
  <div>
    <div class="pagebg classify"></div>
    <div class="container">
      <div class="sortBox">
        <div class="time">
          <div class="block">
            <div class="radio" style="margin-bottom:20px;"></div>
            <el-timeline :reverse="reverse">
              <el-timeline-item v-for="(activity, index) in activities" hide-timestamp :key="index">
                <span
                    @click="getBlogList(activity.uid)"
                    :class="[activity.uid === selectedBlogId ? 'sortBoxSpan sortBoxSpanSelect' : 'sortBoxSpan']"
                >{{ activity.sortName }}</span>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>

        <div class="article">
          <div class="block" v-infinite-scroll="load">
            <el-timeline>
              <el-timeline-item
                  v-for="item in itemByDate"
                  :key="item.uid"
                  :timestamp="item.createTime"
                  placement="top"
              >
                <el-card>
                  <h4 @click="goToList('blogContent', item)" class="itemTitle">{{ item.title }}</h4>
                  <br>
                  <el-tag
                      class="elTag"
                      v-if="item.author"
                      @click="goToList('author', item)"
                  >{{ item.author }}
                  </el-tag>

                  <el-tag
                      class="elTag"
                      type="success"
                      v-if="item.blogSort != null"
                      @click="goToList('blogSort', item)"
                  >{{ item.blogSort.sortName }}
                  </el-tag>
                  <el-tag
                      class="elTag"
                      v-for="tagItem in item.tagList"
                      v-if="tagItem != null"
                      :key="tagItem.uid"
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
import {getBlogCategoryList, getBlogListByCategoryId} from "@/api/classify";

export default {
  data() {
    return {
      selectedBlogId: "",
      reverse: false,
      activities: [],
      itemByDate: [],
      articleByDate: {},
      count: 0,
      currentPage: 1,
      pageSize: 10
    };
  },
  components: {
    //注册组件
  },
  mounted() {
  },
  created() {
    getBlogCategoryList().then(response => {
      if (response.data.success) {
        let activities = response.data;
        let result = [];
        for (let a = 0; a < activities.length; a++) {
          let dataForDate = {
            sortName: activities[a].sortName,
            uid: activities[a].uid
          };
          result.push(dataForDate);
        }
        this.activities = result;

        // 默认选择第一个
        this.getBlogList(activities[0].uid);
      }
    });

  },
  methods: {
    getBlogList(blogSortUid) {
      this.selectBlogId = blogSortUid;
      const params = new URLSearchParams();
      params.append("blogSortUid", blogSortUid);
      getBlogListByCategoryId(params).then(response => {
        if (response.data.success) {
          this.itemByDate = response.data.records;
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
        }
      });
    },
    load() {
      const params = new URLSearchParams();
      if (
          this.selectBlogId == null ||
          this.selectedBlogId === "" ||
          this.selectedBlogId === undefined
      ) {
        return;
      }
      params.append("blogSortUid", this.selectBlogId);
      params.append("currentPage", (this.currentPage + 1).toString());
      getBlogListByCategoryId(params).then(response => {
        if (response.data.success) {
          this.itemByDate = this.itemByDate.concat(response.data.records);
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
        }
      });
    },
    // 跳转到搜索详情页
    goToList(type, entity) {
      switch (type) {
        case "tag": {
          // 标签uid
          let routeData = this.$router.resolve({
            path: "/list",
            query: {tagUid: entity.uid}
          });
          window.open(routeData.href, "_blank");
        }
          break;
        case "blogSort": {
          let routeData = this.$router.resolve({
            path: "/list",
            query: {sortUid: entity.blogSort.uid}
          });
          window.open(routeData.href, "_blank");
        }
          break;
        case "author": {
          let routeData = this.$router.resolve({
            path: "/list",
            query: {author: entity.author}
          });
          window.open(routeData.href, "_blank");
        }
          break;

        case "blogContent": {
          if (entity.type === "0") {
            let routeData = this.$router.resolve({
              path: "/info",
              query: {blogOid: entity.oid}
            });
            window.open(routeData.href, "_blank");
          } else if (entity.type === "1") {
            window.open(entity.outsideLink, '_blank');
          }
        }
          break;
      }
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
