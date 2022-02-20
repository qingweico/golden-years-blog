import common from './common'

export default {
    install(Vue, options) {
        Vue.prototype.$common = common.FUNCTIONS
        Vue.prototype.$Code = common.Code
        Vue.prototype.$SysConf = common.SysConf
    }
}
