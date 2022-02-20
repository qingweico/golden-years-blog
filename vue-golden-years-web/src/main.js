import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import {Button} from 'ant-design-vue';
Vue.config.productionTip = false
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n
Vue.use(ElementUI, { locale })
Vue.use(Antd);
Vue.use(Button);
// 引入公共JS
import '../static/css/index.css';
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
