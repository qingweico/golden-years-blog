import common from './common'

export default {
  install(Vue, options) {
    Vue.prototype.$commonUtil = common.FUNCTIONS
    Vue.prototype.$SysConf = common.SysConf
  }
}
