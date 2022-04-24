import common from './common'

export default {
  install(Vue, options) {
    Vue.prototype.$commom = common.FUNCTIONS
    Vue.prototype.$SysConf = common.SysConf
  }
}
