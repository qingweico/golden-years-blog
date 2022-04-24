<template>
  <div>
    <div class="page_bg classify"></div>
    <div class="container">
      <h1 class="t_nav">
        <span>哪里会有人喜欢孤独,不过是不喜欢失望罢了 ——村上春树《挪威的森林》</span>
        <a href="/" class="n1">网站首页</a>
        <a href="javascript:void(0);" class="n2">分类</a>
      </h1>

      <div class="sortBox">
        <div class="time">
          <div class="block">
            <div class="radio" style="margin-bottom:20px;"></div>
            <el-timeline :reverse="reverse">
              <el-timeline-item v-for="(activity, index) in activities" hide-timestamp :key="index">
                <span
                    @click="getArticleListByCategoryId(activity.id)"
                    :class="[activity.id === selectedBlogId ? 'sortBoxSpan sortBoxSpanSelect' : 'sortBoxSpan']"
                >{{ activity.categoryName }}</span>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>

        <div class="article" v-if="articleListCategory.length">
          <div class="block">
            <el-timeline>
              <el-timeline-item
                  v-for="item in articleListCategory"
                  :key="item.articleId"
                  :timestamp="item.createTime"
                  placement="top">
                <el-card>
                  <h4 @click="goToList( item)" class="itemTitle">{{ item.title }}</h4>
                  <br>
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
                  >{{ tag.name }}
                  </el-tag>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
          <!--分页-->
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
        <el-empty :description=" '暂无' + getBlogCategoryNameById(selectedBlogId)  +  '类别的文章' " v-else
                  style="background-color: white;"></el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import {getBlogListByCategoryId, getBlogCategory} from "@/api";
import {mapGetters} from "vuex";

export default {
  data() {
    return {
      selectedBlogId: "",
      reverse: false,
      activities: [],
      articleListCategory: [],
      currentPage: 1,
      pageSize: 5,
      totalPage: 0,
      records: 0
    };
  },
  components: {},
  mounted() {
  },
  created() {
    getBlogCategory().then(response => {
      let activities = response.data;
      let result = [];
      for (let i = 0; i < activities.length; i++) {
        let dataForDate = {
          categoryName: activities[i].name,
          id: activities[i].id
        };
        result.push(dataForDate);
        this.activities = result;
        // 默认选择第一个
        this.getArticleListByCategoryId(activities[0].id);
      }
    });

  },
  methods: {
    ...mapGetters(['getUserInfo']),
    getArticleListByCategoryId(id) {
      this.selectedBlogId = id;
      this.getPagedArticleList();
    },
    getPagedArticleList() {
      let params = new URLSearchParams();
      params.append("userId", this.getUserInfo().id);
      params.append("categoryId", this.selectedBlogId);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getBlogListByCategoryId(params).then(response => {
        let content = response.data;
        this.articleListCategory = content.rows;
        this.currentPage = content.currentPage;
        this.totalPage = content.totalPage;
        this.records = content.records;
      });
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.activities;
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].categoryName;
        }
      }
    },
    // 跳转到搜索详情页
    goToList(entity) {
      let routeData = this.$router.resolve({
        path: "/detail",
        query: {id: entity.articleId}
      });
      window.open(routeData.href, "_blank");
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getPagedArticleList();
    },
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
