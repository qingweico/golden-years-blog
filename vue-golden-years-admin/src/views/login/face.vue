<template>
  <div class="login-wrap">
    <div class="ms-login">
      <div class="ms-title">人脸登陆</div>
      <el-form :model="data" :rules="rules" ref="login" label-width="0px" class="ms-content">
        <el-form-item prop="username">
          <el-input v-model="data.username" placeholder="username">
            <template #prepend>
              <el-button icon="el-icon-user"></el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <div class="face-view">
            <i class="el-icon-camera logo-camera" v-show="!enablingCamera"></i>
            <video id="video" width="140" height="150" autoplay="autoplay"
                   style="border-radius: 8px;position: absolute"></video>
          </div>
          <div class="face-pic" v-show="enablingCamera">
            <i class="el-icon-picture logo-pic" v-show="!beginCapture"></i>
            <canvas id="canvas" width="140" height="150" style="border-radius: 8px;"></canvas>
          </div>
          <div class="tip">
            <el-checkbox v-model="enablingCamera"
                         @change="openOrCloseCamera()"
                         style="margin:0 0 25px 0;"><span style="color: #eee;font-size: 12px">开启人脸识别</span>
            </el-checkbox>
            <span class="login-tip">建议在Chrome下使用本系统</span>
          </div>
        </el-form-item>
        <div class="login-btn">
          <el-row :gutter="12">
            <el-col :span=12>
              <el-button type="primary" @click="captureFace()">{{ startCapture }}</el-button>
            </el-col>
            <el-col :span=12>
              <el-button type="primary" :loading="loading" @click="faceVerify()">{{ startFaceVerify }}</el-button>
            </el-col>
          </el-row>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>

export default {
  name: "face",
  data() {
    const validateUsername = (rule, value, callback) => {
      if (value.length === 0) {
        callback(new Error("请输入用户名"));
      } else {
        callback();
      }
    };
    return {
      enablingCamera: false,
      startCapture: "捕获人脸",
      startFaceVerify: "人脸信息校验",
      // 全局视频对象
      mediaStreamTrack: null,
      video: null,
      isCameraOpen: false,
      loading: false,
      data: {
        username: "",
        img64: ""
      },
      redirect: undefined,
      beginCapture: false,
      rules: {
        username: [
          {required: true, trigger: "blur", validator: validateUsername}
        ]
      }
    }
  },
  methods: {
    // ################################## 开始处理人脸识别逻辑 ###################################

    clearCameraStatus() {
      this.enablingCamera = false;
      this.isCameraOpen = false;
      this.startCapture = "捕获人脸";
      this.clearForm();
      let faceView = document.getElementsByClassName('face-view')[0];
      faceView.style.cssText = "width: 280;position: relative";
      let canvas = document.getElementById('canvas');
      let ctx = canvas.getContext('2d');
      ctx.clearRect(0, 0, 140, 150);
    },
    clearForm() {
      this.data = {
        username: "",
        img64: ""
      }
    },
    done() {
      this.loading = false;
      this.closeCamera();
      this.clearCameraStatus();
    },
    captureFace() {
      const that = this;
      if (that.enablingCamera && that.isCameraOpen) {
        this.video = document.getElementById('video');
        let canvas = document.getElementById('canvas');
        let ctx = canvas.getContext('2d');
        ctx.drawImage(this.video, 0, 0, 140, 150);
        this.startCapture = "重新捕获人脸";
        this.beginCapture = true;
        let img = document.getElementById('canvas').toDataURL();
        // 截取base64
        // base64的图片
        this.data.img64 = img.split("base64,")[1];
      } else {
        this.$message.error('请先打开摄像头!');
      }
    },
    faceVerify() {
      this.$refs.login.validate(valid => {
        if (valid) {
          this.loading = true;
          this.$store.dispatch("Face", this.data).then(response => {
            this.$message.success(response.msg);
            this.$router.push({path: this.redirect || "/"});
            this.done();
          }, () => {
            this.done();
          })
        }
      })
    },
    openOrCloseCamera() {
      // 如果true, 打开摄像头
      if (this.enablingCamera) {
        this.openCamera();
        let faceView = document.getElementsByClassName('face-view')[0];
        faceView.style.cssText = "width: 142px;position: absolute;";
      } else {
        this.closeCamera();
        this.clearCameraStatus();
      }
    },
    openCamera() {
      const that = this;
      // 如果摄像头已经打开了
      if (that.isCameraOpen) {
        return;
      }
      let constraints = {
        video: {width: 140, height: 150},
        audio: false
      };
      // 获得video摄像头
      that.video = document.getElementById('video');
      let promise = navigator.mediaDevices.getUserMedia(constraints);
      promise.then((mediaStream) => {
        that.mediaStreamTrack = mediaStream.getVideoTracks()
        that.video.srcObject = mediaStream;
        that.video.play();
      });
      that.video.onloadedmetadata = function () {
        that.isCameraOpen = true;
      };
    },
    closeCamera() {
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

<style scoped>
.login-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-image: url("../../assets/img/login-bg.png");
  background-size: 100%;
}

.ms-title {
  width: 100%;
  line-height: 50px;
  text-align: center;
  font-size: 20px;
  color: white;
  border-bottom: 1px solid #ddd;
}

.ms-login {
  position: absolute;
  left: 50%;
  top: 40%;
  width: 350px;
  margin: -190px 0 0 -175px;
  border-radius: 5px;
  background: rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.ms-content {
  padding: 30px 30px;
}

.login-btn {
  text-align: center;
}

.login-btn button {
  width: 100%;
  margin-top: 20px;
  margin-bottom: 10px;
}

/**人脸识别框*/
.face-view {
  position: relative;
  float: left;
  border: 1px dashed black;
  width: 290px;
  height: 150px;
  margin-top: 0;
  border-radius: 10px;
}

.logo-camera {
  color: #009688;
  font-size: 80px;
  position: absolute;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, -50%);
  -moz-transform: translate(-50%, -50%);
  -ms-transform: translate(-50%, -50%);
  -o-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
}

.face-pic {
  position: relative;
  border: 1px dashed black;
  float: right;
  width: 142px;
  height: 150px;
  margin-top: 0;
  border-radius: 10px;
}

.logo-pic {
  color: #009688;
  font-size: 80px;
  position: absolute;
  top: 50%;
  left: 50%;
  -webkit-transform: translate(-50%, -50%);
  -moz-transform: translate(-50%, -50%);
  -ms-transform: translate(-50%, -50%);
  -o-transform: translate(-50%, -50%);
  transform: translate(-50%, -50%);
}


.tip {
  display: block;
  clear: both;
  height: 10px;
  line-height: 16px;
  width: 290px;
  position: relative;
  top: 20px;
}

.login-tip {
  font-family: MicrosoftYaHei;
  font-size: 12px;
  font-weight: 400;
  font-stretch: normal;
  letter-spacing: 0;
  color: #9abcda;
  float: right;
  cursor: pointer;
}
</style>