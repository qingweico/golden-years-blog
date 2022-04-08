import {login, logout, getInfo, getMenu} from '@/api/login'
import {getToken, setToken, removeToken} from '@/utils/auth'

const user = {
    state: {
        token: getToken(),
        name: '',
        avatar: '',
        roles: [],
        menu: {},
        buttonMap: {},
        userInfo: {}
    },

    mutations: {
        SET_TOKEN: (state, token) => {
            state.token = token
        },
        SET_NAME: (state, name) => {
            state.name = name
        },
        SET_AVATAR: (state, avatar) => {
            state.avatar = avatar
        },
        SET_ROLES: (state, roles) => {
            state.roles = roles
        },
        SET_MENU: (state, menu) => {
            state.menu = menu
        },
        SET_USER_INFO: (state, user) => {
            state.userInfo = user
        },
    },

    actions: {
        // 登录
        Login({commit}, userInfo) {
            const username = userInfo.username.trim()
            const password = userInfo.password.trim()
            const isRememberMe = userInfo.isRememberMe
            return new Promise((resolve, reject) => {
                let params = {username, password};
                login(params).then(response => {
                    if (response.success) {
                        const token = response.data
                        // 向cookie中设置token
                        setToken(token);
                        // 向store中设置cookie
                        commit('SET_TOKEN', token);
                    }
                    resolve(response)
                }).catch(error => {
                    reject(error)
                })
            })
        },

        // 获取菜单列表
        GetMenu({commit, state}) {
            return new Promise((resolve, reject) => {
                getMenu(state.name).then(response => {
                    const menu = response.data;
                    commit('SET_MENU', menu)
                    resolve(response)
                }).catch(error => {
                    reject(error)
                })
            })
        },

        // 获取用户信息
        GetInfo({commit}) {
            return new Promise((resolve, reject) => {
                getInfo().then(response => {
                    const data = response.data;
                    commit('SET_NAME', data.username);
                    commit('SET_AVATAR', data.avatar);
                    commit('SET_USER_INFO', data)
                    resolve(response)
                }).catch(error => {
                    reject(error)
                })
            })
        },

        // 登出
        LogOut({commit, state}) {
            return new Promise((resolve, reject) => {
                logout(state.token).then((response) => {
                    commit('SET_TOKEN', '')
                    commit('SET_ROLES', [])
                    removeToken()
                    resolve(response)
                }).catch(error => {
                    reject(error)
                })
            })
        },

        // 前端 登出
        FedLogOut({commit}) {
            return new Promise(resolve => {
                commit('SET_TOKEN', '')
                removeToken()
                resolve()
            })
        }
    }
}

export default user
