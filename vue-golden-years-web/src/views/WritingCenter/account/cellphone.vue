<template>
  <div class="main-page">
    <div class="title-box">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/center/account' }">账户设置</el-breadcrumb-item>
        <el-breadcrumb-item><a>手机号变更</a></el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-form ref="smsCodeForm" :model="form" :rules="smsCodeRules" label-width="80px" style="width: 40%;">
      <el-form-item>
        验证码将发送到手机{{ mobile }}
      </el-form-item>
      <el-form-item label="验证码" prop="smsCode">
        <el-input v-model="form.smsCode" placeholder="6位手机验证码"
                  oninput="value = value.replace(/[^\d]/g,'')"
                  maxlength="6"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" @click="sendSmsCode" :disabled="sendDisabled">{{ sendCodeBtn }}
        </el-button>
        <el-button type="primary" size="mini" @click="next" :disabled="form.smsCode === ''">下一步</el-button>
      </el-form-item>
    </el-form>


    <el-dialog title="变更手机号" :visible.sync="dialogAlterMobile"
               :center=true
               width="30%"
               :before-close="closeAlterMobile">
      <el-form :model="form" :rules="mobileRules" ref="mobileForm">
        <el-form-item prop="mobile">
          <el-input v-model="form.mobile" placeholder="请输入您新的手机号码"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-row>
          <el-col :span="12">
            <el-button @click="cancelAlterMobile" style="width: 90%">取消</el-button>
          </el-col>
          <el-col :span="12">
            <el-button type="primary" @click="submitForm" style="width: 90%" :disabled="!form.mobile">确认</el-button>
          </el-col>
        </el-row>


      </div>
    </el-dialog>
  </div>
</template>

<script>
import {mapGetters} from "vuex";
import {alterMobile, getSmsCode, verifySmsCode} from "@/api/user";

export default {
  data() {
    // 校验手机号
    const validateMobile = (rule, value, callback) => {
      if (!value || new RegExp(/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/).test(value)) {
        callback();
      } else {
        callback(new Error("您的手机号码格式不正确!"));
      }
    };
    return {
      form: {
        smsCode: "",
        mobile: "",
      },
      dialogAlterMobile: false,
      sendDisabled: false,
      isChange: false,
      time: 60,
      sendCodeBtn: "发送手机验证码",
      // 验证码校验
      smsCodeRules: {
        smsCode: [
          {required: true, message: "请输入验证码", trigger: "blur"},
        ]
      },
      // 手机号码校验
      mobileRules: {
        mobile: [
          {required: true, message: '请输入手机号码!', trigger: "blur"},
          {validator: validateMobile}
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['getUserInfo']),
  },
  methods: {
    sendSmsCode() {
      getSmsCode(this.getUserInfo.mobile).then((response) => {
        let timer = setInterval(() => {
          this.time--;
          this.sendCodeBtn = `${this.time}s后重发`;
          this.sendDisabled = true;
          if (this.time === 0) {
            this.sendDisabled = false;
            this.sendCodeBtn = "发送验证码";
            this.time = 60;
            clearTimeout(timer);
          }
        }, 1000);
        this.$message.success(response.msg);
        this.$notify.success({
          title: '验证码',
          message: response.data,
          offset: 100
        });
      })

    },
    closeAlterMobile() {
      this.dialogAlterMobile = false;
      this.form.mobile = "";
      // 清除本次的校验信息
      this.$refs.mobileForm.clearValidate();
    },
    cancelAlterMobile() {
      this.dialogAlterMobile = false;
      this.form.mobile = "";
      // 清除本次的校验信息
      this.$refs.mobileForm.clearValidate();
    },
    submitForm() {
      this.$refs.mobileForm.validate((valid) => {
        if (valid) {
          let params = {};
          params.newMobile = this.form.mobile;
          params.userId = this.getUserInfo.id;
          alterMobile(params).then((response) => {
            this.$message.success(response.msg);
            this.dialogAlterMobile = false;
            this.form = {
              mobile: "",
              smsCode: ""
            };
          })
        }
      })
    },
    next() {
      let params = {};
      params.mobile = this.getUserInfo.mobile;
      params.smsCode = this.form.smsCode;
      verifySmsCode(params).then((response) => {
        if (response.success) {
          this.dialogAlterMobile = true;
        } else {
          this.$message.error(response.msg);
        }
      })

    },
    close() {
      this.$router.push("/center/cellphone");
    }
  }, created() {
    let mobile = this.getUserInfo.mobile;
    this.mobile = mobile.replace(mobile.substring(3, 7), "****")
  }
}
</script>

<style scoped>
.main-page {
  width: 980px;
  height: 400px;
  margin-left: 20px;
  background-color: white;
  position: relative;
  padding-bottom: 40px;
}

.title-box {
  padding: 20px 0 10px 30px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.el-breadcrumb-item:hover {
  color: #920784;
}

.el-form {
  position: absolute;
  left: 30%;
  top: 30%;

}
</style>