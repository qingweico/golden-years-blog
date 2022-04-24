<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="80px">
    <el-form-item label="用户昵称" prop="username">
      <el-input v-model="user.username" maxlength="30"/>
    </el-form-item>
    <el-form-item label="手机号码" prop="mobile">
      <el-input v-model="user.mobile" maxlength="11"/>
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="user.email" maxlength="50"/>
    </el-form-item>
    <el-form-item label="生日" prop="birthday">
      <el-date-picker
          v-model="user.birthday"
          type="date"
          value-format="yyyy-MM-dd HH:mm:ss"
          format="yyyy-MM-dd HH:mm:ss"
          placeholder="选择日期">
      </el-date-picker>
    </el-form-item>
    <el-form-item label="性别" prop="gender">
      <el-radio-group v-model="user.gender">
        <el-radio :label=1>男</el-radio>
        <el-radio :label=0>女</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="人脸登陆">

      <el-row>
        <el-col :span="16">
          <el-radio v-model="isFaceLogin" :label=true @change="enableOrDisableFaceLogin">启用</el-radio>
          <el-radio v-model="isFaceLogin" :label=false @change="enableOrDisableFaceLogin">未启用</el-radio>
          <el-button class="face-btn" @click="seeFace(user.faceId)" size="mini"
                     v-show="isFaceLogin && user.faceId">查看人脸
          </el-button>
          <el-button @click="takeFace" size="mini"
                     v-show="isFaceLogin && takeFaceStatus === 1">获取人脸
          </el-button>
          <el-button @click="refreshFace" size="mini"
                     v-show="isFaceLogin && takeFaceStatus===2">重新获取
          </el-button>
          <el-button @click="startUpload" size="mini"
                     v-show="isFaceLogin && takeFaceStatus===2">上传人脸信息
          </el-button>
          <el-button v-show="isFaceLogin" type="primary" size="mini"
                     @click="openOrCloseCamera()"
                     style="margin:0 0 25px 0;">开启/关闭摄像头
          </el-button>
        </el-col>
        <el-col :span="12">
          <div v-show="isFaceLogin && takeFaceStatus === 1">
            <video id="video" width="140px" height="150px"></video>
          </div>
          <div v-show="isFaceLogin && takeFaceStatus === 2">
            <canvas id="canvas" width="140px" height="150px"></canvas>
          </div>
        </el-col>
      </el-row>

    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close">关闭</el-button>
    </el-form-item>


    <el-dialog
        title="人脸信息"
        align="center"
        :visible.sync="dialogVisible"
        :before-close="handleClose"
        width="20%">
      <div class="block">
        <el-image :src="base64Image" style="width: 200px;" fit="contain"></el-image>
      </div>

      <span slot="footer" class="dialog-footer">
  </span>
    </el-dialog>
  </el-form>
</template>

<script>


import {updateUserProfile, uploadToGridFs} from "@/api/system/profile";
import {mapGetters} from "vuex";
import {deleteFaceInfo, seeFace} from "../../../api/system/profile";
import store from "../../../store";

export default {
  props: {
    user: {
      type: Object
    }
  },
  data() {
    return {
      isFaceLogin: false,
      enablingCamera: false,
      img64: "",
      faceId: "",
      base64Image: "",
      dialogVisible: false,
      mediaStreamTrack: null,
      video: null,
      isCameraOpen: false,
      // 0: 不使用人脸,
      // 1: 使用人脸,并初始化,
      // 2: 获取人脸在网页展示
      takeFaceStatus: 0,
      // 人脸数据,用于数据层交互,入库
      faceData64: "",
      // 表单校验
      rules: {
        username: [
          {required: true, message: "用户昵称不能为空", trigger: "blur"}
        ],
        email: [
          {required: true, message: "邮箱地址不能为空", trigger: "blur"},
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: ["blur", "change"]
          }
        ],
        mobile: [
          {required: true, message: "手机号码不能为空", trigger: "blur"},
          {
            pattern: /^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/,
            message: "请输入正确的手机号码",
            trigger: "blur"
          }
        ],
        gender: [
          {required: true, message: "请选择性别", trigger: "blur"},
        ]
      }
    };
  },
  created() {
    this.isFaceLogin = !!this.user.faceId;
  },
  computed: {
    ...mapGetters(["avatar"])
  },
  methods: {
    // 查看人脸信息
    seeFace(faceId) {
      let params = new URLSearchParams();
      params.append("faceId", faceId);
      seeFace(params).then((response) => {
        this.base64Image = `data:image/png;base64, ${response.data}`;
        this.dialogVisible = true;

      })
    },
    handleClose() {
      this.base64Image = "";
      this.dialogVisible = false;
    },
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          let data = {};
          let user = this.user;
          data.username = user.username;
          data.mobile = user.mobile;
          data.email = user.email;
          data.birthday = user.birthday;
          data.gender = user.gender;
          data.avatar = this.avatar;
          if (this.faceId !== "") {
            data.faceId = this.faceId;
          }
          updateUserProfile(data).then(response => {
            this.$message.success(response.msg);
            // 更新管理员信息
            this.$store.dispatch('GetInfo');
          });
        }
      });
    },
    openCamera() {
      this.initCameraStatus();
    },
    closeCamera() {
      this.clearCameraStatus();
    },
    startUpload() {
      if (this.isFaceLogin) {
        let faceData64 = this.faceData64;
        if (faceData64 === "") {
          this.$message.error("\"请点击`获取人脸`按钮获得您的人脸信息\"");
        } else {
          this.img64 = faceData64.split("base64,")[1];
          let data = {};
          data.username = this.user.username;
          data.img64 = this.img64;
          // 上传人脸信息
          uploadToGridFs(data).then((response) => {
            this.faceId = response.data;
            this.$message.success(response.msg);
          })
        }
      }
    },
    // 获取人脸照片信息
    takeFace() {
      let isFaceLogin = this.isFaceLogin;
      if (isFaceLogin) {
        this.video = document.getElementById('video');
        let canvas = document.getElementById('canvas');
        let ctx = canvas.getContext('2d');
        ctx.drawImage(this.video, 0, 0, 140, 150);
        // base64的图片
        this.faceData64 = document.getElementById('canvas').toDataURL();
        // 拍照,标记装为2,表示拍过了
        this.takeFaceStatus = 2;
      }
    },
    // 使用人脸登录,人脸入库
    enableOrDisableFaceLogin() {
      let isFaceLogin = this.isFaceLogin;
      if (isFaceLogin) {
        this.initCameraStatus();
      } else {
        // 如果人脸信息为空, 则什么也不做
        if (this.user.faceId) {
          // 删除人脸信息
          this.deleteFaceInfo();
        } else {
          this.$message.info("nothing to do!");
        }
        this.clearCameraStatus();

      }
    },
    openOrCloseCamera() {
      if (this.isCameraOpen) {
        this.clearCameraStatus();
      } else {
        this.initCameraStatus();
      }
    },
    deleteFaceInfo() {
      deleteFaceInfo(this.user.id).then((response) => {
        this.$message.success(response.msg);
        // 更新管理员信息
        this.$store.dispatch('GetInfo');
      })
    },
    // 重新刷新,重新获取人脸
    refreshFace() {
      // 之前存入的人脸信息清除
      this.faceData64 = "";
      // 重拍,回到状态1
      this.takeFaceStatus = 1;
    },

    initCameraStatus() {
      // 如果勾选人脸登录,则需要人脸入库
      this.openFace();
      // 使用人脸,初始化
      this.takeFaceStatus = 1;
    },
    clearCameraStatus() {
      this.closeFace();
      this.isCameraOpen = false;
      // 不使用人脸
      this.takeFaceStatus = 0;
    },
    // 打开人脸摄像头
    openFace() {
      const _this = this;
      // 如果摄像头已经打开了,就不再继续下方代码业务
      if (_this.isCameraOpen) {
        return;
      }

      let constraints = {
        video: {width: 140, height: 150},
        audio: false
      };
      // 获得video摄像头
      this.video = document.getElementById('video');
      let promise = navigator.mediaDevices.getUserMedia(constraints);
      promise.then((mediaStream) => {
        _this.mediaStreamTrack = mediaStream.getVideoTracks()
        _this.video.srcObject = mediaStream;
        _this.video.play();
      });
      _this.video.onloadedmetadata = function () {
        _this.isCameraOpen = true;
      };
    },
    // 关闭人脸摄像头
    closeFace() {
      let stream = document.getElementById('video').srcObject;
      if (stream == null) {
        return;
      }
      let tracks = stream.getTracks();

      tracks.forEach(function (track) {
        track.stop();
      });
      document.getElementById('video').srcObject = null;
    },
    close() {
      this.clearCameraStatus();
      this.$router.push("/dashboard");
    }
  }
};
</script>
<style>
</style>
