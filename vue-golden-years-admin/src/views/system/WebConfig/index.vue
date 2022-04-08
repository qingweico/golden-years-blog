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
            ref="from"
        >
          <el-form-item label="LOGO">
            <div class="imgBody" v-if="form.photoList">
              <i
                  class="el-icon-error inputClass"
                  v-show="icon"
                  @click="deletePhoto()"
                  @mouseover="icon = true"
              ></i>
              <img @mouseover="icon = true" @mouseout="icon = false" v-bind:src="form.photoList[0]" alt>
            </div>
            <div v-else class="uploadImgBody" @click="checkPhoto">
              <i class="el-icon-plus avatar-uploader-icon"></i>
            </div>
          </el-form-item>

          <el-row :gutter="24">
            <el-col :span="10">
              <el-form-item label="网站名称" prop="oldPwd">
                <el-input v-model="form.name" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="标题" prop="newPwd1">
                <el-input v-model="form.title" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="10">
              <el-form-item label="关键字" prop="newPwd2">
                <el-input v-model="form.keyword" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="描述" prop="newPwd1">
                <el-input v-model="form.summary" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="10">
              <el-form-item label="作者" prop="newPwd2">
                <el-input v-model="form.author" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="备案号" prop="newPwd2">
                <el-input v-model="form.recordNum" style="width: 400px"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="10">
              <el-form-item label="登录方式">
                <el-checkbox-group v-model="form.loginTypeList">
                  <el-checkbox label="1" style="margin-left: 10px">账号密码</el-checkbox>
                  <el-checkbox label="2" style="margin-left: 10px">码云</el-checkbox>
                  <el-checkbox label="3" style="margin-left: 10px">Github</el-checkbox>
                  <el-checkbox label="4" style="margin-left: 10px">QQ</el-checkbox>
                  <el-checkbox label="5" style="margin-left: 10px">微信</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item>
            <el-button type="primary" @click="submitForm()">保 存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>


      <el-tab-pane>
        <el-form-item>
          <el-button type="primary" @click="submitForm()">保 存</el-button>
        </el-form-item>
      </el-tab-pane>


      <CheckPhoto
          v-if="!isFirstPhotoVisible"
          @choose_data="getChooseData"
          @cancelModel="cancelModel"
          :photoVisible="photoVisible"
          :photos="photoList"
          :files="fileIds"
          :limit="1"
      ></CheckPhoto>
  </div>
</template>

<script>
import CheckPhoto from "@/components/CheckPhoto";
import {getToken} from '@/utils/auth'
import {getWebConfig} from "@/api/webConfig";
import {Loading} from 'element-ui';
import {getSystemConfig} from "@/api/systemConfig";
import CKEditor from "@/components/CKEditor";
import MarkdownEditor from "@/components/MarkdownEditor";

export default {
  data() {
    return {
      form: {
        name: "",
        title: "",
        keyword: "",
        summary: "",
        author: "",
        logo: "",
        recordNum: "",
        openComment: "1",
        aliPay: "",
        showList: [],
        loginTypeList: []
      },
      systemConfig: {},
      loadingInstance: null, // loading对象
      fileList: [],
      photoVisible: false, //控制图片选择器的显示
      photoList: [],
      fileIds: "",
      icon: false, //控制删除图标的显示
      otherData: {},
      openDictList: [], //字典
      isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
      rules: {
        qqNumber: [
          {pattern: /[1-9]([0-9]{5,11})/, message: '请输入正确的QQ号码'},
        ],
        qqGroup: [
          {pattern: /-?[1-9]\d*/, message: '请输入正确的QQ群'},
        ],
        gitee: [
          {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入正确的Gitee地址'},
        ],
        github: [
          {pattern: /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入正确的Github地址'},
        ],
        email: [
          {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
        ]
      }
    };
  },
  watch: {
    "form.aliPay": {
      handler(newVal, oldVal) {
        console.log("value change", oldVal, newVal);
      },
      deep: true
    },
    "form.weixinPay": {
      handler(newVal, oldVal) {
        console.log("value change", oldVal, newVal);
      },
      deep: true
    }
  },
  components: {
    CheckPhoto,
    CKEditor,
    MarkdownEditor
  },
  created() {
    // 获取配置
    this.getWebConfigFun();

    //图片上传地址
    this.uploadPictureHost = process.env.PICTURE_API + "/file/cropperPicture";
    // 查询字典
    this.getDictList()
    //其它数据
    this.otherData = {
      source: "picture",
      userUid: "uid00000000000000000000000000000000",
      adminUid: "uid00000000000000000000000000000000",
      projectName: "blog",
      sortName: "admin",
      token: getToken()
    };

    // 获取系统配置
    this.getSystemConfigList();
  },
  methods: {
    handleClick(tab, event) {

    },
    getWebConfigFun() {
      getWebConfig().then(response => {
        if (response.data.success) {
          let data = response.data;
          if (data.showList) {
            let showList = JSON.parse(data.showList)
            let loginTypeList = JSON.parse(data.loginTypeList)
            data.showList = showList;
            data.loginTypeList = loginTypeList;
            this.form = data;
          } else {
            data.showList = []
            this.form = data;
          }
          this.fileIds = this.form.logo;
          this.photoList = this.form.photoList;
        }
      });
    },
    // 获取系统配置
    getSystemConfigList: function () {
      getSystemConfig().then(response => {
        if (response.data.success) {
          this.systemConfig = response.data;
        }
      });
    },
    getChooseData(data) {
      const that = this;
      this.photoVisible = false;
      this.photoList = data.photoList;
      this.fileIds = data.fileIds;
      let fileId = this.fileIds.replace(",", "");
      if (this.photoList.length >= 1) {
        this.fileIds = fileId;
        this.form.photoList = this.photoList;
      }
    },
    // 关闭模态框
    cancelModel() {
      this.photoVisible = false;
    },
    deletePhoto: function () {
      this.form.photoList = null;
      this.fileIds = "";
      this.icon = false;
    },
    checkPhoto() {
      this.photoList = [];
      this.fileIds = "";
      this.photoVisible = true;
      this.isFirstPhotoVisible = false
    },
    submitForm() {

    },

    beforeUpload(file) {
      this.loadingInstance = Loading.service({fullscreen: true, text: '正在努力上传中~'});
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