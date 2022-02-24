import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// A modern alternative to CSS resets
import 'normalize.css/normalize.css'

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

// icon
import '@/icons'
// 全局样式
import '@/styles/index.scss'
// 引入全局工具类
Vue.config.productionTip = false
// 添加粒子特效
import VueParticles from 'vue-particles'
Vue.use(VueParticles)
Vue.use(ElementUI)
// Vue.use(prototype)
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
