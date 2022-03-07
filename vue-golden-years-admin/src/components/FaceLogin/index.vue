<template>
  <div>
    <div class="box loginBox" v-if="showLogin === true">
      <div class="title">
        <span class="t1">
          人脸登录
        </span>
        <div class="t2" @click="closeLogin()">
          X
        </div>
      </div>
      <!-- 分割线 -->
      <el-divider></el-divider>

      <el-form :label-position="labelPosition" :rules="loginRules" :model="loginForm" ref="loginForm">


        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item>
          <div class="face-view">
            <i class="el-icon-camera" v-show="!isFaceLogin"></i>
            <video id="video" width="140" height="150" autoplay="autoplay"
                   style="border-radius: 8px;position: absolute"></video>
          </div>
          <div class="face-pic" v-show="isFaceLogin">
            <i class="el-icon-picture" v-show="!beginCapture"></i>
            <canvas id="canvas" width="140" height="150" style="border-radius: 8px;"></canvas>
          </div>
          <div class="tip">
            <el-checkbox style="margin:0 0 25px 0;"><span style="color: #eee" @change="useFaceLogin()"
                                                          @click="openOrClose()">开启人脸识别</span>
            </el-checkbox>
            <span style="color: gray;font-size: 12px; float: right;">建议在Chrome下使用本系统</span>
          </div>

        </el-form-item>

        <el-row class="btn">
          <el-button class="loginBtn" type="primary" @click="startLogin">登录</el-button>
        </el-row>


      </el-form>


    </div>
    <div class="mask"></div>

  </div>
</template>

<script>
import {Loading} from 'element-ui';


export default {
  name: "FaceLogin",
  data() {
    return {
      loading: null,
      isFaceLogin: false,
      beginCapture: false,
      showPasswordLogin: true,
      option: {
        fullscreen: true,
        lock: true
      },
      // 显示登录页面
      showLogin: true,
      isLogin: false,
      table: false,
      dialog: false,
      labelPosition: "right",
      loginForm: {
        username: "",
        password: ""
      },
      loginRules: {
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 2, message: "用户名长度大于等于 2 个字符", trigger: "blur"},
          {max: 10, message: "用户名长度不能大于 10 个字符", trigger: "blur"}
        ]
      },
    };
  },
  components: {},
  created() {
  },
  methods: {
    startLogin: function () {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
        }
      });
    },

    goAuth: function (source) {
      this.loading = Loading.service({
        lock: true,
        text: '加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    closeLogin: function () {
      this.$emit("closeLoginBox", "");
    },
    // ##################################开始处理人脸登陆逻辑##################################


    // 打开或者关闭摄像头
    openOrClose() {
      if (this.isFaceLogin === true) {
        this.clearCameraStatus();
      } else {
        this.isFaceLogin = true;
        this.useFaceLogin();
        this.btnValue = "捕获人脸";
        $(".face-view").css({"width": "138px", "position": "absolute"});
      }
    },
    clearCameraStatus() {
      this.isFaceLogin = false;
      this.isCameraOpen = false;
      this.useFaceLogin();
      this.btnValue = "立即登陆"
      $(".face-view").css({"width": "277px", "position": "relative"});
      let canvas = document.getElementById('canvas');
      let ctx = canvas.getContext('2d');
      ctx.clearRect(0, 0, 140, 150);
    },

    switchStatus() {
      this.clearCameraStatus();
    },
    useFaceLogin() {
      // 如果true, 打开摄像头
      if (this.isFaceLogin) {
        this.openFace();
      } else if (!this.isFaceLogin) {
        this.closeFace();
      }
    },
    openFace() {
      // 如果摄像头已经打开了, return
      if (this.isCameraOpen) {
        return;
      }

      let constraints = {
        video: {width: 138, height: 150},
        audio: false
      };
      // 获得video摄像头
      this.video = document.getElementById('video');
      let promise = navigator.mediaDevices.getUserMedia(constraints);
      promise.then((mediaStream) => {
        this.mediaStreamTrack = mediaStream.getVideoTracks()
        this.video.srcObject = mediaStream;
        this.video.play();
        this.video();
        console.log(this);
      });
      this.video.onloadedmetadata = function () {
        this.isCameraOpen = true;
      };
    },
    closeFace() {
      let stream = document.getElementById('video').srcObject;
      if (stream === null) {
        return;
      }
      let tracks = stream.getTracks();
      tracks.forEach(function (track) {
        track.stop();
      });
      document.getElementById('video').srcObject = null;
      this.beginCapture = false;
    }
  }
};
</script>
<style>
.box {
  width: 400px;
  height: 420px;
  background: white;
  position: fixed;
  margin: auto;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  z-index: 101;
}

.box .title {
  height: 48px;
  font-size: 22px;
  font-weight: bold;
  text-align: center;
  line-height: 48px;
}

.box .title .t2 {
  font-size: 16px;
  float: right;
  margin-right: 6px;
  margin-top: -6px;
  cursor: pointer;
}

.box .btn {
  text-align: center;
}

.box .loginBtn {
  width: 40%;
}


/* 遮罩层 */
.mask {
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 100;
}
</style>
