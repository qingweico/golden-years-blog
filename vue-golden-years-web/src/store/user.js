import {SET_USER_INFO, SET_LOGIN_STATE} from "./mutation-types";

const app = {
    // 全局状态
    state: {
        // 用户信息
        userInfo: {},
        isLogin: false,
    },
    // getters是对数据的包装, 例如对数据进行拼接, 或者过滤
    getters: {
        // 类似于计算属性
        getUserPhoto(state) {
            if (state.isLogin) {
                if (state.userInfo.face) {
                    return state.userInfo.face
                } else {
                    // 用户没有设定头像, 使用默认头像
                    return "https://gitee.com/moxi159753/wx_picture/raw/master/picture/favicon.png";
                }
            } else {
                // 用户未登录, 使用defaultAvatar
                return "../../static/images/defaultAvatar.png"
            }
        },
        getUserInfo(state) {
            if (state.isLogin) {
                return state.userInfo
            }
            return state.userInfo;
        }

    },
    // 如果我们需要更改store中的状态, 一定要通过mutations来进行操作
    mutations: {

        // 传入自定义参数
        [SET_USER_INFO](state, userInfo) {
            state.userInfo = userInfo
        },
        [SET_LOGIN_STATE](state, isLogin) {
            state.isLogin = isLogin
        }
    },

    // actions是我们定义的一些操作, 正常情况下, 我们很少会直接调用mutation方法来改变state
    actions: {}
}
export default app
