/**
 * 全局配置文件
 */
const SysConf = {
  defaultAvatar: "https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png", // 默认头像
}
/**
 * 通用工具类
 */
const FUNCTIONS = {

  /**
   * 标签转字符串
   * @param tags
   * @returns {string}
   */
  tagsToString: tags => {
    let str = ''
    for (let i = 0; i < tags.length; i++) {
      str += tags[i] + ','
    }
    return str.substr(0, str.length - 1)
  },
  // 切割字符串
  splitStr: (str, flagCount) => {
    if (str == null || str === '') {
      return ""
    } else if(str.length > flagCount) {
      return str.substring(0, flagCount) + "..."
    } else {
      return str
    }
  },
  /**
   * 切割字符串
   * @param str
   * @param count
   * @returns {string|*}
   */
  strSubstring: (str, count) => {
    if (str == null || str === '') {
      return ""
    } else if(str.length > count) {
      return str.substring(0, count) + "..."
    } else {
      return str
    }
  },
  /**
   * 复制文字到剪切板
   * @param text
   */
  copyText: text => {
    const oInput = document.createElement('input')
    oInput.value = text
    document.body.appendChild(oInput)
    // 选择对象
    oInput.select()
    // 执行浏览器复制命令
    document.execCommand('Copy')
    oInput.className = 'oInput'
    oInput.style.display = 'none'
  }
}

export default {
  SysConf,
  FUNCTIONS
}
