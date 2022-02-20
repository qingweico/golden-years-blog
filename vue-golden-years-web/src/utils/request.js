import axios from 'axios'
import router from '@/router/index'
import {getCookie} from "@/utils/cookie";
// 创建axios实例
const service = axios.create({
    // api 的 base_url
    baseURL: '',
    // 请求超时时间 10秒
    timeout: 20000
})

service.defaults.headers.common['Authorization'] = getCookie("token")

// request拦截器
service.interceptors.request.use(
    config => {
        if (getCookie("token") !== undefined) {
            // 让每个请求携带自定义token 请根据实际情况自行修改
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
    if (res.code === 'success' || res.code === 'error') {
      return res
    } else if (res.code === 401 || res.code === 400) {
      console.log('返回错误内容', res)
      router.push('404')
      return res
    } else if (res.code === 500) {
      router.push('500')
      return Promise.reject('error')
    } else if (res.code === 502) {
      router.push('502')
      return Promise.reject('error')
    } else {
      return Promise.reject('error')
    }
  },
  error => {
    // 出现网络超时
    router.push('500')
    return Promise.reject(error)
  }
)

export default service
