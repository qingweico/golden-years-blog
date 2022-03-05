import axios from 'axios'
import router from '@/router/index'
import {getCookie} from "@/utils/cookie";
// 创建axios实例
const service = axios.create({
    // api 的 base_url
    baseURL: '',
    // 请求超时时间 5秒
    timeout: 5000
})

service.defaults.headers.common['Authorization'] = getCookie("token")

// request拦截器
service.interceptors.request.use(
    config => {
        console.log(getCookie("token"))
        if (getCookie("token") !== null) {
            // 让每个请求携带自定义token
            config.headers.Authorization = getCookie("token")
        }
        return config
    },
    error => {
        // Do something with request error
        console.log(error) // for debug
        Promise.reject(error).then(r => {
        });
    }
)

// response 拦截器
service.interceptors.response.use(
    response => {
        const res = response.data
        if (res.success) {
            return res
        } else if (res.status === 401 || res.status === 400) {
            router.push('404').then(r => {
            });
            return res
        } else if (res.status === 500) {
            router.push('500').then(r => {
            });
            return Promise.reject('error')
        } else if (res.status === 502) {
            router.push('502').then(r => {
            });
            return Promise.reject('error')
        } else {
            return Promise.reject('error')
        }
    },
    error => {
        // 出现网络超时
        router.push('500').then(r => {
        });
        return Promise.reject(error)
    }
)

export default service
