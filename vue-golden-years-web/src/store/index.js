import Vue from 'vue'
import Vuex from 'vuex'
import user from './user'

// 让vuex生效
Vue.use(Vuex)

const store = new Vuex.Store({

    // 将user放在store中
    modules: {
        user
    }
})

export default store
