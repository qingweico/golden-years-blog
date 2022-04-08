<template>
  <div class="app-container">
    <el-tabs type="border-card" @tab-click="handleClick" v-model="activeName">
      <el-tab-pane name="one">
        <span slot="label"><i class="el-icon-edit"></i>系统配置</span>
        <el-form style="margin-left: 20px;" label-position="left" label-width="140px">
          <aside>
            <br/>
          </aside>
          <el-form-item label="文本编辑器">
            <el-radio v-for="item in editorModalDictList" :key="item.uid" v-model="form.editorModel"
                      :label="item.dictValue" border size="medium">{{ item.dictLabel }}
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
            <el-radio v-for="item in searchModelDictList" :key="item.uid" v-model="form.searchModel"
                      :label="item.dictValue" border size="medium">{{ item.dictLabel }}
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

          <el-form-item label="本地文件域名" prop="localPictureBaseUrl">
            <el-input v-model="form.localPictureBaseUrl" auto-complete="new-password" style="width: 400px"></el-input>
          </el-form-item>
          <el-form-item label="文件上传本地">
            <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="form.uploadLocal"
                      :label="item.dictValue" border size="medium">{{ item.dictLabel }}
            </el-radio>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm()">保 存</el-button>
          </el-form-item>

        </el-form>
      </el-tab-pane>

      <el-tab-pane name="four">
        <span slot="label">
          <i class="el-icon-date"></i> 七牛云对象存储
        </span>
        <el-form
            style="margin-left: 20px;"
            label-position="left"
            :model="form"
            label-width="120px"
            :rules="rules"
            ref="form"
        >
          <aside>
            使用七牛云构建对象存储服务<br/>
          </aside>

          <el-form-item label="七牛云文件域名" prop="qiNiuPictureBaseUrl">
            <el-input v-model="form.qiNiuPictureBaseUrl" auto-complete="new-password" style="width: 400px"></el-input>
          </el-form-item>

          <el-form-item label="七牛云公钥">
            <el-input v-model="form.qiNiuAccessKey" auto-complete="new-password" style="width: 400px"></el-input>
          </el-form-item>

          <el-form-item label="七牛云私钥">
            <el-input type="password" v-model="form.qiNiuSecretKey" auto-complete="new-password"
                      style="width: 400px"></el-input>
          </el-form-item>

          <el-form-item label="上传空间">
            <el-input v-model="form.qiNiuBucket" style="width: 400px"></el-input>
          </el-form-item>

          <el-form-item label="存储区域">
            <el-select v-model="form.qiNiuArea" placeholder="请选择存储区域" clearable>
              <el-option v-for="item in areaDictList"
                         :key="item.dictValue"
                         :label="item.dictLabel"
                         :value="item.dictValue"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="文件上传七牛云">
            <el-radio v-for="item in yesNoDictList" :key="item.uid" v-model="form.uploadQiNiu" :label="item.dictValue"
                      border size="medium">{{ item.dictLabel }}
            </el-radio>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitForm()">保 存</el-button>
          </el-form-item>

        </el-form>
      </el-tab-pane>

      <el-tab-pane name="five">
        <span slot="label">
          <i class="el-icon-date"></i> Minio对象存储
        </span>

        <el-form
            style="margin-left: 20px;"
            label-position="left"
            :model="form"
            label-width="120px"
            :rules="rules"
            ref="form">
        </el-form>
      </el-tab-pane>


      <el-tab-pane name="seven">
        <span slot="label"><i class="el-icon-edit"></i> Redis管理</span>
        <el-form style="margin-left: 20px;" label-position="left" label-width="120px">

          <aside>
            Redis管理主要用于清空一些缓存数据<br/>
            用户首次部署时, 可以使用清空全部, 把Redis中的缓存一键清空<br/>
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
                <el-button type="primary" @click="cleanRedis('BLOG_CLICK')">清空点击量</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="success" @click="cleanRedis('BLOG_PRAISE')">清空点赞量</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="info" @click="cleanRedis('BLOG_LEVEL')">清空推荐博客</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="warning" @click="cleanRedis('HOT_BLOG')">清空热门博客</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="danger" @click="cleanRedis('NEW_BLOG')">清空最新博客</el-button>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item label="分类和归档相关">
            <el-row>
              <el-col :span="3">
                <el-button type="primary" @click="cleanRedis('MONTH_SET')">清空分类日期</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="success" @click="cleanRedis('BLOG_SORT_BY_MONTH')">清空分类数据</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="info" @click="cleanRedis('BLOG_SORT_CLICK')">清空分类点击量</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="warning" @click="cleanRedis('TAG_CLICK')">清空标签点击量</el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item label="系统相关">
            <el-row>
              <el-col :span="3">
                <el-button type="primary" @click="cleanRedis('REDIS_DICT_TYPE')">清空字典</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="success" @click="cleanRedis('ADMIN_VISIT_MENU')">清空角色访问菜单</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="info" @click="cleanRedis('userToken')">清空用户Token</el-button>
              </el-col>

              <el-col :span="3">
                <el-button type="warning" @click="cleanRedis('REQUEST_LIMIT')">清空接口请求限制</el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script>
import {getSystemConfig, cleanRedisByKey} from "@/api/systemConfig";

export default {
  data() {
    return {
      form: {},
      index: "0", // 当前激活页
      activeName: "one",
      areaDictList: [], //存储区域字典
      yesNoDictList: [], //是否字典
      openDictList: [], // 开启关闭字典
      picturePriorityDictList: [], //图片显示优先级字典
      editorModalDictList: [], // 文本编辑器字典列表
      searchModelDictList: [], // 搜索模式字典列表
      loadingInstance: null, // loading对象
      rules: {
        localPictureBaseUrl: [
          {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入正确的域名'},
        ],
        qiNiuPictureBaseUrl: [
          {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入正确的域名'},
        ],
        email: [
          {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
        ],
      }
    };
  },
  watch: {},
  components: {},
  created() {
    // 获取字典
    this.getDictList()
    // 获取系统配置
    this.getSystemConfigList()
  },
  methods: {
    handleClick(tab, event) {

    },
    getSystemConfigList: function () {
      getSystemConfig().then(response => {

      });
    },
    cleanRedis: function (key) {
      let params = []
      params.push(key)
      cleanRedisByKey(params).then(response => {

      })
    },
    submitForm: function () {

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