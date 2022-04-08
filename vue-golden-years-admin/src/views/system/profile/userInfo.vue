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
        <el-radio :label = 1>男</el-radio>
        <el-radio :label = 0>女</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>


import {updateUserProfile} from "@/api/system";
import {mapGetters} from "vuex";

export default {
  props: {
    user: {
      type: Object
    }
  },
  data() {
    return {
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
  computed: {
    ...mapGetters(["avatar"])
  },
  methods: {
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
          updateUserProfile(data).then(response => {
            this.$message.success(response.msg);
          });
        }
      });
    },
    close() {
    }
  }
};
</script>
