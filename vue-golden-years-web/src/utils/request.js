import axios from 'axios'
import router from '@/router/index'
import {getCookie} from "@/utils/cookie";
import {Loading, Message} from 'element-ui'
// 创建axios实例
const service = axios.create({
    // api 的 base_url
    baseURL: '',
    // 请求超时时间 10秒
    timeout: 10000
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
        if (loading === null) {
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
                router.push('/').then(() => {
                });
                return res
            } else if (response.status === 500) {
                router.push('/').then(() => {
                });
                return Promise.reject('error').then(()=>{});
            } else if (response.status === 502) {
                router.push('/').then(() => {
                });
                return Promise.reject('error').then(()=>{});
            } else {
                if(!response.data) {
                    return;
                }
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
        console.log('err' + error)
        let {message} = error;
        if (message === "Network Error") {
            message = "后端接口连接异常";
            router.push('500').then(() => {
            });
        } else if (message.includes("timeout")) {
            message = "网络超时";
            router.push('500').then(() => {
            });
        } else if (message === "Request failed with status code 502") {
            message = "请求频繁, 请稍后再试"
            router.push('502').then(() => {
            });
        } else if (message === "Request failed with status code 500") {
            message = "请求错误"
            router.push('500').then(() => {
            });
        }
        Message({
            message: message,
            type: 'error',
            duration: 5 * 1000
        })
        return Promise.reject(error)
    }
)

export default service
