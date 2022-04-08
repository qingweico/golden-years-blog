import axios from 'axios'
import router from '@/router/index'
import {getCookie} from "@/utils/cookie";
import {Loading, Message} from 'element-ui'
// 创建axios实例
const service = axios.create({
    // api 的 base_url
    baseURL: '',
    // 请求超时时间 5秒
    timeout: 5000
})

// 请求计数器(限制请求数)
let requestNum = 0;
let loading = null;

service.defaults.headers.common['Authorization'] = getCookie("token")

// request拦截器
service.interceptors.request.use(
    config => {
        if (getCookie("token") !== null) {
            // 让每个请求携带自定义token
            config.headers.Authorization = getCookie("token")
        }
        requestNum++;
        if (loading == null) {
            loading = Loading.service({fullscreen: true, text: '正在努力加载中'});
        } else if (requestNum > 0) {
            loading = Loading.service({fullscreen: true, text: '正在努力加载中'});
        }
        return config
    },
    error => {
        // Do something with request error
        console.log(error) // for debug
        Promise.reject(error).then(() => {
            // 出错了直接关闭loading
            requestNum = 0
            if (loading) {
                loading.close();
            }
        });
    }
)

// response 拦截器
service.interceptors.response.use(
    response => {
        const res = response.data;
        // 请求数减1
        requestNum--;
        if (loading == null || requestNum <= 0) {
            loading.close()
        }
        if (res.success) {
            return res
        } else {
            // 出错了直接关闭loading
            requestNum = 0
            loading.close();
            if (response.status === 401 || response.status === 400) {
                router.push('404').then(() => {});
                return res
            } else if (response.status === 500) {
                router.push('500').then(() => {});
                return Promise.reject('error')
            } else if (response.status === 502) {
                router.push('502').then(() => {});
                return Promise.reject('error')
            } else {
                Message({
                    message: res.msg,
                    type: 'error',
                    duration: 5 * 1000
                })
                return Promise.reject('error')
            }
        }
    },
    error => {
        requestNum = 0
        loading.close();
        // 出现网络超时
        router.push('500').then(() => {});
        return Promise.reject(error)
    }
)

export default service
