<template>
  <div class="app-container">
    <el-tabs type="border-card" @tab-click="handleClick" v-model="activeName">
      <el-tab-pane name="one">
        <span slot="label"><i class="el-icon-edit"></i>系统配置</span>
        <el-form style="margin-left: 20px;" label-position="left" label-width="140px">
          <aside>
            配置文章编辑器模式和主站文章搜索模式
            <br/>
          </aside>
          <el-form-item label="文本编辑器">
            <el-radio v-model="form.editorModel" :label=0 border size="medium">富文本编辑器
            </el-radio>
            <el-radio v-model="form.editorModel" :label=1 border size="medium">MarkDown
            </el-radio>
          </el-form-item>

          <!-- 搜索模式-->
          <el-form-item>
            <template slot="label">
              文章搜索模式
              <el-popover
                  placement="top-start"
                  width="200"
                  trigger="hover"
                  content="用于控制主站搜索功能使用SQL方式, 还是全文检索">
                <i slot="reference" style="cursor: pointer;margin-left: 2px" class="el-icon-question"></i>
              </el-popover>
            </template>
            <el-radio v-model="form.searchModel"
                      :label=0 border size="medium">SQL
            </el-radio>
            <el-radio v-model="form.searchModel"
                      :label=1 border size="medium">ElasticSearch
            </el-radio>

          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm()">保存</el-button>
          </el-form-item>

        </el-form>
      </el-tab-pane>

      <el-tab-pane name="three">
        <span slot="label">
          <i class="el-icon-date"></i> 本地文件存储
        </span>
        <el-form
            style="margin-left: 20px;"
            label-position="left"
            :model="form"
            label-width="120px"
            :rules="rules"
            ref="form">
          <aside>
            使用IO流将文件存储本地磁盘中<br/>
          </aside>

          <el-form-item label="本地文件域名" prop="localPicUrl">
            <el-input v-model="form.localPicUrl" style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item label="文件上传本地">
            <el-radio v-model="form.uploadLocal" :label=1
                      border size="medium"> 是
            </el-radio>
            <el-radio v-model="form.uploadLocal" :label=0
                      border size="medium"> 否
            </el-radio>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="submitForm()">保 存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane name="seven">
        <span slot="label"><i class="el-icon-edit"></i> Redis管理</span>
        <el-form style="margin-left: 20px;" label-position="left" label-width="120px">
          <aside>
            Redis管理主要用于清空一些缓存数据<br/><br/>
          </aside>
          <el-form-item label="全部">
            <el-row>
              <el-col :span="6">
                <el-button type="danger" @click="cleanRedis('ALL')">清空全部</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="博客相关">
            <el-row>
              <el-col :span="3">
                <el-button type="primary" @click="cleanRedis('redis_article_detail')">博客详情</el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="primary" @click="cleanRedis('redis_article_comment_counts')">博客评论</el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="primary" @click="cleanRedis('redis_article_read_counts')">博客阅读量</el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="primary" @click="cleanRedis('redis_article_star_counts, redis_article_already_star')">
                  博客点赞量
                </el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="primary"
                           @click="cleanRedis('redis_article_collect_counts,redis_article_already_collect')">博客收藏量
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="标签">
            <el-row>
              <el-col :span="3">
                <el-button type="success" @click="cleanRedis('redis_article_tag')">标签数据</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="分类">
            <el-row>
              <el-col :span="3">
                <el-button type="success" @click="cleanRedis('redis_article_category')">分类数据</el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="success" @click="cleanRedis('redis_article_category_with_article_count')">
                  带有文章数量的类别
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="用户">
            <el-row>
              <el-col :span="3">
                <el-button type="info" @click="cleanRedis('redis_user_token')">用户token</el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="info" @click="cleanRedis('redis_user_info')">用户信息</el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="info" @click="cleanRedis('redis_author_fans_counts')">用户粉丝</el-button>
              </el-col>
              <el-col :span="3">
                <el-button type="info" @click="cleanRedis('redis_my_follow_counts')">用户关注</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="系统">
            <el-row>
              <el-col :span="3">
                <el-button  @click="cleanRedis('redis_friend_link')">友情链接</el-button>
              </el-col>
              <el-col :span="3">
                <el-button @click="cleanRedis('redis_system_config')">系统配置</el-button>
              </el-col>
              <el-col :span="3">
                <el-button @click="cleanRedis('redis_web_config')">网站配置</el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script>
import {getSystemConfig, cleanRedisByKey, alterSystemConfig} from "@/api/system/sysConfig";

export default {
  data() {
    return {
      form: {
        id: "",
        searchModel: 0,
        editorModel: 0,
        uploadLocal: 0,
        localPicUrl: ""
      },
      loading: false,
      activeName: "one",
      rules: {
        localPicUrl: [
          {
            pattern: "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~/])" +
                "+(\\??(([A-Za-z0-9-~]+=?)([A-Za-z0-9-~]*)&?)*)$", message: '请输入正确的域名'
          },
        ]
      }
    };
  },
  watch: {},
  components: {},
  created() {
    // 获取系统配置
    this.getSystemConfig()
  },
  methods: {
    handleClick(tab, event) {

    },
    getSystemConfig() {
      getSystemConfig().then(response => {
        this.form = response.data;
      });
    },
    cleanRedis(key) {
      let params = []
      params.push(key)
      cleanRedisByKey(params).then(response => {
        this.$message.success(response.msg);
      })
    },
    submitForm() {
      this.loading = true;
      alterSystemConfig(this.form).then(response => {
        this.loading = false;
        this.$message.success(response.msg);
      }, () => {
        this.loading = false;
      })
    },

  }
};
</script>

<style lang="scss">
aside {
  background: #eef1f6;
  padding: 8px 24px;
  margin-bottom: 20px;
  border-radius: 2px;
  display: block;
  line-height: 32px;
  font-size: 16px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen, Ubuntu, Cantarell, "Fira Sans", "Droid Sans", "Helvetica Neue", sans-serif;
  color: #2c3e50;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;

  a {
    color: #337ab7;
    cursor: pointer;

    &:hover {
      color: rgb(32, 160, 255);
    }
  }
}
</style>
