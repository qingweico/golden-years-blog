import axios from 'axios'
import {Message, MessageBox, Loading} from 'element-ui'
import store from '../store'
import {getToken} from '@/utils/auth'
// 导入NProgress进度条
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
// 创建axios实例
const service = axios.create({
    baseURL: '',
    withCredentials: true,
    timeout: 5000
})

// 传递token
service.defaults.headers.common['Authorization'] = getToken()

// request拦截器
service.interceptors.request.use(config => {
        // 请求时开启进度条
        NProgress.start()

        if (store.getters.token) {
            // 让每个请求携带自定义token 请根据实际情况自行修改
            config.headers.Authorization = getToken()
        }
        return config
    },
    error => {
        // Do something with request error
        console.log(error)
        Promise.reject(error).then(() => {
            Message.error("网络错误");
        })
        NProgress.done();
    }
)

// response 拦截器
service.interceptors.response.use(response => {
        // 响应时结束进度条
        NProgress.done()
        const res = response.data;
        if (res.success) {
            // 请求完毕
            return res;
        } else {
            if (response.status === 401) {
                NProgress.done()
                MessageBox.confirm(
                    'token已过期, 可以取消继续留在该页面, 或者重新登录',
                    '确定登出',
                    {
                        confirmButtonText: '重新登录',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }
                ).then(() => {
                    store.dispatch('FedLogOut').then(() => {
                        // 为了重新实例化vue-router对象 避免bug
                        location.reload()
                    })
                })
                return Promise.reject('error')
            } else if (response.status === 402) {
                NProgress.done()
                // 接口没有权限访问时
                Message({
                    message: res.msg,
                    type: 'error',
                    duration: 5 * 1000
                })
                return Promise.reject('error')
            } else {
                NProgress.done()
                console.log("错误信息", res.msg)
                Message({
                    message: res.msg,
                    type: 'error',
                    duration: 5 * 1000
                })
                return Promise.reject(res.msg)
            }
        }
    },
    error => {
        NProgress.done();
        console.log('错误码', error)
        Message({
            message: error,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

export default service
