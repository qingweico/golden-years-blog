<template>
  <div>
    <a-form-model ref="form" :model="model" :rules="validatorRules">
      <a-form-model-item required prop="authName">
        <a-input v-model="model.authName" size="large" placeholder="请输入用户名/邮箱/手机号">
          <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }"/>
        </a-input>
      </a-form-model-item>
      <a-form-model-item required prop="password">
        <a-input v-model="model.password" size="large" type="password" autocomplete="false" placeholder="请输入密码">
          <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }"/>
        </a-input>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>

<script>
import {localLogin} from '@/api/user'

export default {
  name: 'LoginAccount',
  data() {
    return {
      loginType: 0,
      model: {
        authName: '',
        password: '',
      },
      validatorRules: {
        authName: [
          {required: true, message: '请输入用户名!'},
          {validator: this.handleUsernameOrEmail}
        ],
        password: [{
          required: true, message: '请输入密码!', validator: 'click'
        }]
      }

    }
  },
  created() {

  },
  methods: {

    // 判断登录类型
    handleUsernameOrEmail(rule, value, callback) {
      const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
      if (regex.test(value)) {
        this.loginType = 0
      } else {
        this.loginType = 1
      }
      callback()
    },
    /**
     * 验证字段
     * @param arr
     * @param callback
     */
    validateFields(arr, callback) {
      let promiseArray = []
      for (let item of arr) {
        let p = new Promise((resolve, reject) => {
          this.$refs['form'].validateField(item, (err) => {
            if (!err) {
              resolve();
            } else {
              reject(err);
            }
          })
        });
        promiseArray.push(p)
      }
      Promise.all(promiseArray).then(() => {
        callback()
      }).catch(err => {
        callback(err)
      })
    },
    acceptUsername(username) {
      this.model['authName'] = username
    },
    // 账号密码登录
    handleLogin() {
      this.validateFields(['authName', 'password'], (err) => {
        if (!err) {
          let loginParams = {
            auth: this.model.authName,
            password: this.model.password,
          }
          // TODO
          localLogin(loginParams).then((res) => {
            if (res.data.success) {
              this.$emit('success', res.data)
            } else {
              this.$emit('fail', res.data)
            }
          }, () => {
            this.$emit('fail', '网络超时')
          }).catch((err) => {
            this.$emit('fail', err)
          });
        } else {
          this.$emit('validateFail')
        }
      })
    }
  }
}
</script>

<style scoped>

</style>