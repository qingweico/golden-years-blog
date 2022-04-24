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
      <el-col :span="12">
        <css-doodle @click="click(e)">
          :doodle { @grid: 1x35 / 30vw 30vh; }
          :container {
          background: #ffcabb;
          background: linear-gradient(to top, #ffcabb 50%, #de93b6 100%);
          background-repeat: no-repeat;
          }

          position: relative;
          align-self: end;
          height: @rand(10%, 75%);
          background: linear-gradient(to
          top, #725392 0%, #b764ac 100%);
          margin-left: @rand(0.1, 1)vw;
          z-index: 1;
          transform: scaleX(@rand(.8, 1.9));

          ::before {
          content: "";
          position: absolute;
          bottom: 0;
          left: @rand(-20, 12)px;
          right: @rand(-20, 12)px;
          top: @rand(15, 55)%;
          background: linear-gradient(to
          top, #352864 0%, #4d4280 100%);
          z-index: 3;
          }

          ::after {
          content: "";
          position: absolute;
          width: .1vw;
          height: .12vw;
          top: @rand(15, 20)%;
          left: @rand(10, 20)%;
          z-index: 5;
          box-shadow:
          @rand(0.1, 2.1)vw @rand(0, 10)vh .5px rgba(246, 212, 0, .7),
          @rand(0.1, 2.1)vw @rand(10, 15)vh .5px rgba(246, 212, 0, .6),
          @rand(0.1, 2.1)vw @rand(15, 22)vh .5px rgba(246, 212, 0, .7),
          @rand(0.1, 2.1)vw @rand(22, 30)vh .5px rgba(246, 212, 0, .6),
          @rand(0.1, 2.1)vw @rand(30, 40)vh .5px rgba(246, 212, 0, .8);
          }
        </css-doodle>
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
