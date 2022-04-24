<template>
  <div class="app-container">
    <el-tabs type="border-card" @tab-click="handleClick">
      <el-tab-pane>
        <span slot="label">
          <i class="el-icon-date"></i> 网站信息
        </span>
        <el-form
            style="margin-left: 20px;"
            label-position="left"
            :model="form"
            label-width="80px"
            ref="from">
          <el-form-item label="LOGO">
            <div class="imgBody" v-if="form.logo">
              <i
                  class="el-icon-error inputClass"
                  v-show="icon"
                  @click="deletePhoto()"
                  @mouseover="icon = true"
              ></i>
              <img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="form.logo" alt>
            </div>
            <div v-else class="uploadImgBody" @click="checkPhoto">
              <i class="el-icon-plus avatar-uploader-icon"></i>
            </div>
          </el-form-item>

          <el-row :gutter="24">
            <el-col :span="10">
              <el-form-item label="网站名称" prop="name">
                <el-input v-model="form.name" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="标题" prop="title">
                <el-input v-model="form.title" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item label="描述" prop="description">
                <el-input v-model="form.description" type="textarea" style="width: 400px"
                          :autosize="{ minRows: 3, maxRows: 10}"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="10">
              <el-form-item label="作者" prop="author">
                <el-input v-model="form.author" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="备案号" prop="recordNum">
                <el-input v-model="form.recordNum" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="登录方式">
                <el-checkbox-group v-model="loginTypeList">
                  <el-checkbox label="1" style="margin-left: 10px">账号密码</el-checkbox>
                  <el-checkbox label="2" style="margin-left: 10px">Github</el-checkbox>
                  <el-checkbox label="3" style="margin-left: 10px">QQ</el-checkbox>
                  <el-checkbox label="4" style="margin-left: 10px">微信</el-checkbox>
                  <el-checkbox label="5" style="margin-left: 10px">微博</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button type="primary" @click="submitForm()" :loading="loading">保 存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>


    <avatar-cropper
        :height="300"
        :key="imageCropperKey"
        :url="url"
        :width="300"
        @close="close"
        @crop-upload-success="cropSuccess"
        lang-type="zh"
        v-show="imageCropperShow">
    </avatar-cropper>

  </div>
</template>
<script>

import {getWebConfig,alterWebConfig} from "@/api/system/webConfig";
import AvatarCropper from '@/components/AvatarCropper'

export default {
  data() {
    return {
      form: {
        id: "",
        name: "",
        title: "",
        description: "",
        author: "",
        logo: "",
        recordNum: ""
      },
      loading: false,
      loginTypeList: [],
      imageCropperShow: false,
      url: process.env.GATEWAY_API + "/fs/uploadFace",
      imageCropperKey: 0,
      systemConfig: {},
      photoVisible: false,
      icon: false,
    };
  },
  watch: {},
  components: {
    AvatarCropper
  },
  created() {
    getWebConfig().then(response => {
      this.form = response.data;
      this.loginTypeList = this.form.loginTypeList.replace("[", "")
          .replace("]", "")
          .split(",");
    });
  },
  methods: {
    handleClick(tab, event) {

    },
    // 关闭模态框
    cancelModel() {
      this.photoVisible = false;
    },
    deletePhoto() {
      this.form.logo = "";
      this.icon = false;
    },
    close() {
      this.imageCropperShow = false
    },
    // 弹出选择图片框
    checkPhoto() {
      this.imageCropperShow = true
    },
    cropSuccess(resData) {
      this.imageCropperShow = false
      this.imageCropperKey = this.imageCropperKey + 1
      this.form.logo = resData;
      this.$notify({
        title: "成功",
        message: '上传成功',
        type: "success"
      });
    },
    submitForm() {
      this.loading = true;
      this.form.loginTypeList = String(this.loginTypeList);
      alterWebConfig(this.form).then((response) => {
        this.$message.success(response.msg);
        this.loading = false;
      }, () => {
        this.loading = false;
      })
    }
  }
};
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  margin: 0 0 0 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.imgBody {
  width: 100px;
  height: 100px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}

.uploadImgBody {
  margin-left: 5px;
  width: 100px;
  height: 100px;
  border: dashed 1px #c0c0c0;
  float: left;
  position: relative;
}

.uploadImgBody :hover {
  border: dashed 1px #00ccff;
}

.inputClass {
  position: absolute;
}

img {
  width: 100px;
  height: 100px;
}
</style>