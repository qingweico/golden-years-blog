import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import 'ant-design-vue/dist/antd.css';

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import prototype from './utils/prototype'

import "../assets/iconfont/iconfont.css";
import '../static/css/ckeditor.css';
import '../static/css/index.css';

Vue.config.productionTip = false
Vue.use(prototype)
Vue.use(ElementUI)
Vue.directive('highlight', function (el) {
    let blocks = el.querySelectorAll('pre code');
    blocks.forEach((block) => hljs.highlightBlock(block));
})
new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')
