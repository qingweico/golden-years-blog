<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">个人资料</span>
      </div>
    </div>

    <div class="account-wrapper">
      <div class="middle-wrapper">
        <div class="every-line">
          <div class="pre-label">作者昵称</div>
          <div class="info-words">
            <el-input v-model="userAccountInfo.nickname" placeholder="请输入作家昵称" maxlength="8"></el-input>
          </div>
        </div>
        <div class="every-line">
          <div class="pre-label">作家头像</div>
          <div class="info-face">
            <div class="choose-face-wrapper">
              <img id="user-face" :src="userAccountInfo.face" class="user-face"
                   alt="user-face"/>
              <div class="choose-face">选择头像</div>
              <input type="file"
                     id="inputPic"
                     class="inputPic"
                     value="重新选择"
                     @change="uploadFace"
                     @mouseover="mouseOver"
                     @mouseout="mouseOut"
                     accept="image/jpeg,image/jpg,image/png">
            </div>
            <div class="upload-suggest">请上传尺寸200X200像素(大小不能超过2M), 能够代表形象的图片, 请勿使用低俗/二维码/广告的头像</div>
          </div>
        </div>
        <div class="every-line">
          <div class="pre-label">真实姓名</div>
          <div class="info-words">
            <el-input id="realname" name="realname" v-model="userAccountInfo.realName" placeholder="请输入真实姓名"
                      maxlength="8"></el-input>
          </div>
        </div>
        <div class="every-line">
          <div class="pre-label">生日</div>
          <div class="info-words">
            <el-date-picker
                v-model="userAccountInfo.birthday"
                type="date"
                placeholder="选择日期">
            </el-date-picker>
          </div>
        </div>
        <div class="every-line">
          <div class="pre-label">性别</div>
          <div class="info-words">
            <el-radio-group v-model="userAccountInfo.sex">
              <el-radio :label="0">男生</el-radio>
              <el-radio :label="1">女生</el-radio>
              <el-radio :label="2">保密</el-radio>
            </el-radio-group>
          </div>
        </div>
        <div class="every-line">
          <div class="pre-label">所在城市</div>
          <div style="position: relative;">
            <el-input id="city-picker"
                      name="city-picker"
                      data-toggle="city-picker"
                      class="pick-city"></el-input>
          </div>
        </div>
        <div class="every-line">
          <div class="pre-label">注册手机</div>
          <div class="info-words">{{ userAccountInfo.mobile }}</div>
        </div>
        <div class="every-line">
          <div class="pre-label">联系邮箱</div>
          <div class="info-words">
            <el-input id="email" name="email" v-model="userAccountInfo.email" placeholder="请输入邮箱"
                      maxlength="20"></el-input>
          </div>
        </div>
      </div>
      <div class="submit-wrapper">
        <el-button type="primary" @click="handleSubmit" class="submit-btn" :loading="loading">提交信息</el-button>
      </div>
    </div>
  </div>

</template>

<script>
import 'city-picker/cityPicker.css'
import {mapGetters} from "vuex";
import {updateUserInfo, uploadFace} from "@/api/user";

export default {
  name: "account",
  data() {
    return {
      userAccountInfo: {},
      loading: false,
    }
  },
  created() {
    this.userAccountInfo = this.getUserInfo();

  },
  methods: {
    ...mapGetters(['getUserInfo']),
    // 上传头像
    uploadFace() {
      let f = document.getElementById('inputPic').files[0];
      //创建一个form对象
      let multiForm = new FormData();
      // append 向form表单添加数据
      multiForm.append('file', f, f.name);
      uploadFace(multiForm).then((response) => {
        this.$notify.success({
          title: '提示',
          message: response.msg,
          offset: 100
        });
        this.userAccountInfo.face = response.data;
      });
    },
    handleSubmit() {
      this.loading = true;
      setTimeout(() => {
      }, 2000);
      let userAccountInfo = this.userAccountInfo;
      userAccountInfo.province = "河南省";
      userAccountInfo.city = "信阳市";
      userAccountInfo.district = "固始县";
      updateUserInfo(userAccountInfo).then((response) => {
        this.$message.success(response.msg);
        this.loading = false;
      }, () => {
        this.loading = false;
        this.$message.error('网络超时');
      });
    },

    // 鼠标移动到上传组件上发生css变化
    mouseOver() {
      $("#user-face").css({"background-color": "#f0efef", "border": "1px dashed #a7a7a7"});
      $(".choose-face").css({"opacity": 0.6});
    },
    // 鼠标离开上传组件上发生css变化
    mouseOut() {
      $("#user-face").css({"background-color": "#f7f7f7", "border": "1px dashed #d8d8d8"});
      $(".choose-face").css({"opacity": 0});
    },
  }
}
</script>

<style scoped>
.main-page {
  width: 980px;
  margin-left: 20px;
  background-color: white;
  padding-bottom: 40px;
}

.account-wrapper {
  padding: 10px 0 10px 30px;
}

.middle-wrapper {
  margin-top: 10px;
}

.every-line {
  display: flex;
  flex-direction: row;
  margin-bottom: 40px;
}

.pre-label {
  color: #666;
  width: 120px;
  font-size: 16px;
}

.info-words {
  font-size: 16px;
}

.info-face {
  display: flex;
  flex-direction: column;
}

.choose-face-wrapper {
  display: flex;
  flex-direction: row;
}

.user-face {
  width: 120px;
  height: 120px;
  border: 1px solid #dbdbdb;
}

.user-face:hover {
  border: 1px solid #e8e8e8;
  cursor: pointer;
}

.user-face:hover .choose-face {
  opacity: 0.6;
}

.inputPic {
  cursor: pointer;
  opacity: 0;
  width: 120px;
  height: 120px;
  position: relative;
  /* left: -121px; */
  left: -243px;
  z-index: 9;
}

.choose-face {
  color: white;
  background-color: #666;
  width: 122px;
  height: 35px;
  border-bottom: 1px solid #e8e8e8;
  border-left: 1px solid #e8e8e8;
  border-right: 1px solid #e8e8e8;
  opacity: 0;
  text-align: center;
  vertical-align: middle;
  line-height: 36px;
  position: relative;
  top: 86px;
  left: -122px;
  z-index: 6;
}

.choose-face:hover {
  opacity: 0.6;
  cursor: pointer;
}

.upload-suggest {
  width: 450px;
  color: #8a939e;
  font-size: 14px;
  line-height: 24px;
}

.pick-city {
  width: 300px;
}

.submit-wrapper {
  text-align: center;
}

.submit-btn {

  /*#800080*/
  /*#920784*/
  background-color: #800080;
  color: white;
  font-weight: 400;
  font-size: 16px;
  outline: none;
  border: 0;
  padding: 8px 26px;
}

.submit-btn:hover {
  background-color: #9F5F9F;
  cursor: pointer;
}
</style>