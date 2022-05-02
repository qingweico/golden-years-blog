<template>
  <div class="errPage-container">
    <el-button @click="back" icon='arrow-left' class="pan-back-btn">返回</el-button>
    <el-row>
      <el-col :span="12">
        <h1 class="text-jumbo text-ginormous">Oops!</h1>
        <h2>网站正在维护中...</h2>
        <ul class="list-un-styled">
          <li class="link-type">
            <router-link to="/">回首页</router-link>
          </li>

          <li><a @click.prevent="dialogVisible=true" href="#">点我看图</a></li>
        </ul>
      </el-col>
    </el-row>
    <el-dialog title="随便看" :visible.sync="dialogVisible">
      <img class="pan-img" :src="ewiClap" alt>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: 'page401',
  data() {
    return {
      ewiClap: 'https://wpimg.wallstcn.com/007ef517-bafd-4066-aae4-6883632d9646',
      dialogVisible: false
    }
  },
  methods: {
    click() {
      document.addEventListener('click', function (e) {
        e.target.update && e.target.update();
      });
    },
    back() {
      if (this.$route.query.noGoBack) {
        this.$router.push({path: '/'})
      } else {
        this.$router.go(-1)
      }
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
body {
  line-height: 2;
}

.errPage-container {
  width: 800px;
  margin: 100px auto;

  .pan-back-btn {
    background: #008489;
    color: #fff;
    border: none !important;
  }

  .pan-gif {
    margin: 0 auto;
    display: block;
  }

  .pan-img {
    display: block;
    margin: 0 auto;
    width: 100%;
  }

  .text-jumbo {
    font-size: 60px;
    font-weight: 700;
    color: #484848;
  }

  .list-un-styled {
    font-size: 14px;

    li {
      padding-bottom: 5px;
    }

    a {
      color: #008489;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
