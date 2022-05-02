<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">个人资料</span>
      </div>
    </div>

    <div class="account-wrapper">
      <el-form :model="form" ref="form" :rules="infoRules">
        <el-form-item :label-width="formLabelWidth" label="用户头像">
          <div class="info-face">
            <div class="choose-face-wrapper">
              <img id="user-face" :src="form.face" class="user-face"
                   alt/>
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
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="真实姓名" prop="realName">
          <el-input v-model="form.realName" style="width: 200px"></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="昵称" prop="nickname">
          <el-input v-model="form.nickname" style="width: 200px"></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="手机号" prop="mobile">
          <span>{{form.mobile}}</span>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="邮箱" prop="email">
          <el-input v-model="form.email" style="width: 200px"></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="生日" prop="birthday">
          <el-date-picker v-model="form.birthday"
                          type="date" style="width: 200px"
                          placeholder="选择日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="省份" prop="province">
          <CitySelector :province="form.province" style="width: 200px"
                        @province="getProvince"></CitySelector>
        </el-form-item>
        <el-form-item label="性别" :label-width="formLabelWidth" prop="sex">
          <el-radio-group v-model="form.sex">-->
            <el-radio :label=1>男生</el-radio>
            <el-radio :label=0>女生</el-radio>
            <el-radio :label=2>保密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

    </div>
    <div class="submit-wrapper">
      <el-button type="primary" @click="handleSubmit" class="submit-btn" :loading="loading">提交信息</el-button>
    </div>
  </div>

</template>

<script>
import {mapGetters} from "vuex";
import CitySelector from "@/components/CitySelect"
import {updateUserInfo, uploadFace} from "@/api/user";

export default {
  name: "account",
  components: {
    CitySelector
  },
  data() {
    return {
      loading: false,
      formLabelWidth: '100px',
      form: {},
      infoRules: {
        email: [
          {
            required: true,
            trigger: "blur",
            message: "请输入邮箱"
          },
          {
            pattern: "^([a-z0-9A-Z]+[-|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$",
            message: "请输入合法的邮箱"
          }
        ],
        realName: [
          {
            required: true,
            trigger: "blur",
            message: "请输入真实姓名"
          },
          {min: 1, max: 5, message: "真实姓名长度在 1 到 5 个字符", trigger: "blur"}
        ],
        nickname: [
          {
            required: true,
            trigger: "blur",
            message: "请输入昵称"
          },
          {min: 1, max: 10, message: "昵称长度在 1 到 10 个字符", trigger: "blur"}
        ]
      },
    }
  },
  created() {
    this.form = this.getUserInfo();
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
        this.form.face = response.data;
      });
    },
    getProvince(data) {
      this.form.province = data;
    },
    handleSubmit() {
      // 信息校验
      this.$refs.form.validate(valid => {
        if(valid) {
          let params = {};
          params.id = this.form.id;
          params.realName = this.form.realName;
          params.face = this.form.face;
          params.nickname = this.form.nickname;
          params.sex = this.form.sex;
          if(!this.form.province) {
            this.$message.error("请选择省份");
            return;
          }
          params.province = this.form.province;
          params.mobile = this.form.mobile;
          params.email = this.form.email;
          params.birthday = this.form.birthday;
          this.loading = true;
          setTimeout(() => {
          }, 2000);
          updateUserInfo(params).then((response) => {
            this.$message.success(response.msg);
            this.loading = false;
          }, () => {
            this.loading = false;
          });
        }
      })
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