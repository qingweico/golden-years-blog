import Vue from 'vue'

export function timeFix() {
    const time = new Date()
    const hour = time.getHours()
    return hour < 9 ? '早上好' : (hour <= 11 ? '上午好' : (hour <= 13 ? '中午好' : (hour < 20 ? '下午好' : '晚上好')))
}

export function welcome() {
    const arr = ['休息一会儿吧', '准备吃什么呢?', '要不要打一把 LOL', '我猜你可能累了']
    let index = Math.floor((Math.random() * arr.length))
    return arr[index]
}

/**
 * 截取URL中的参数
 * @returns {{}}
 */
export function getUrlVars() {
    const vars = {};
    window.location.href.replace(
        /[?&]+([^=&]+)=([^&#]*)/gi,
        function (m, key, value) {
            vars[key] = value;
        }
    );
    return vars;
}

/**
 * 触发 window.resize
 */
export function triggerWindowResizeEvent() {
    let event = document.createEvent('HTMLEvents')
    event.initEvent('resize', true, true)
    event.eventType = 'message'
    window.dispatchEvent(event)
}

/**
 * 过滤对象中为空的属性
 * @param obj
 * @returns {*}
 */
export function filterObj(obj) {
    if (!(typeof obj == 'object')) {
        return;
    }

    for (let key in obj) {
        if (obj.hasOwnProperty(key)
            && (obj[key] == null || obj[key] === undefined || obj[key] === '')) {
            delete obj[key];
        }
    }
    return obj;
}

/**
 * 时间格式化
 * @param value
 * @param fmt
 * @returns {*}
 */
export function formatDate(value, fmt) {
    let regPos = /^\d+(\.\d+)?$/;
    if (regPos.test(value)) {
        // 如果是数字
        let getDate = new Date(value);
        let o = {
            'M+': getDate.getMonth() + 1,
            'd+': getDate.getDate(),
            'h+': getDate.getHours(),
            'm+': getDate.getMinutes(),
            's+': getDate.getSeconds(),
            'q+': Math.floor((getDate.getMonth() + 3) / 3),
            'S': getDate.getMilliseconds()
        };
        if (/(y+)/.test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (getDate.getFullYear() + '').substr(4 - RegExp.$1.length))
        }
        for (let k in o) {
            if (new RegExp('(' + k + ')').test(fmt)) {
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)))
            }
        }
        return fmt;
    } else {
        value = value.trim();
        return value.substr(0, fmt.length);
    }
}

/**
 * 深度克隆对象、数组
 * @param obj 被克隆的对象
 * @return 克隆后的对象
 */
export function cloneObject(obj) {
    return JSON.parse(JSON.stringify(obj))
}

/**
 * 随机生成数字
 * @param1 最小值 | 长度
 * @param2 最大值
 * @return int 生成后的数字
 */
export function randomNumber() {
    // 生成最小值到最大值区间的随机数
    const random = (min, max) => {
        return Math.floor(Math.random() * (max - min + 1) + min)
    }
    if (arguments.length === 1) {
        let [length] = arguments
        // 生成指定长度的随机数字, 首位一定不是 0
        let nums = [...Array(length).keys()].map((i) => (i > 0 ? random(0, 9) : random(1, 9)))
        return parseInt(nums.join(''))
    } else if (arguments.length >= 2) {
        let [min, max] = arguments
        return random(min, max)
    } else {
        return Number.NaN
    }
}

/**
 * 随机生成字符串
 * @param length 字符串的长度
 * @param chats 可选字符串区间(只会生成传入的字符串中的字符)
 * @return string 生成的字符串
 */
export function randomString(length, chats) {
    if (!length) length = 1
    if (!chats) chats = '0123456789qwertyuioplkjhgfdsazxcvbnm'
    let str = ''
    for (let i = 0; i < length; i++) {
        let num = randomNumber(0, chats.length - 1)
        str += chats[num]
    }
    return str
}

/**
 * 随机生成uuid
 * @return string 生成的uuid
 */
export function randomUUID() {
    let chats = '0123456789abcdef'
    return randomString(32, chats)
}

/**
 * 下划线转驼峰
 * @param string
 * @returns {*}
 */
export function underLine2CamelCase(string) {
    return string.replace(/_([a-z])/g, function (all, letter) {
        return letter.toUpperCase();
    });
}


/**
 * 增强CSS, 可以在页面上输出全局css
 * @param css 要增强的css
 * @param id style标签的id, 可以用来清除旧样式
 */
export function cssExpand(css, id) {
    let style = document.createElement('style')
    style.type = "text/css"
    style.innerHTML = `@charset "UTF-8"; ${css}`
    // 清除旧样式
    if (id) {
        let $style = document.getElementById(id)
        if ($style != null) $style.outerHTML = ''
        style.id = id
    }
    // 应用新样式
    document.head.appendChild(style)
}


/** 用于js增强事件，运行JS代码，可以传参 */
// options 所需参数：
//    参数名         类型            说明
//    vm             VueComponent    vue实例
//    event          Object          event对象
//    jsCode         String          待执行的js代码
//    errorMessage   String          执行出错后的提示（控制台）
export function jsExpand(options = {}) {

    // 绑定到window上的keyName
    let windowKeyName = 'J_CLICK_EVENT_OPTIONS'
    if (typeof window[windowKeyName] != 'object') {
        window[windowKeyName] = {}
    }

    // 随机生成JS增强的执行id，防止冲突
    let id = randomString(16, 'qwertyuioplkjhgfdsazxcvbnm'.toUpperCase())
    // 封装按钮点击事件
    let code = `
    (function (o_${id}) {
      try {
        (function (globalEvent, vm) {
          ${options.jsCode}
        })(o_${id}.event, o_${id}.vm)
      } catch (e) {
        o_${id}.error(e)
      }
      o_${id}.done()
    })(window['${windowKeyName}']['EVENT_${id}'])
  `
    // 创建script标签
    const script = document.createElement('script')
    // 将需要传递的参数挂载到window对象上
    window[windowKeyName]['EVENT_' + id] = {
        vm: options.vm,
        event: options.event,
        // 当执行完成时, 无论如何都会调用的回调事件
        done() {
            // 执行完后删除新增的 script 标签不会撤销执行结果（已产生的结果不会被撤销）
            script.outerHTML = ''
            delete window[windowKeyName]['EVENT_' + id]
        },
        // 当js运行出错的时候调用的事件
        error(e) {
            console.group(`${options.errorMessage || '用户自定义JS增强代码运行出错'}（${new Date()}）`)
            console.error(e)
            console.groupEnd()
        }
    }
    // 将事件挂载到document中
    script.innerHTML = code
    document.body.appendChild(script)
}

/**
 * 如果值不存在就 push 进数组, 反之不处理
 * @param array 要操作的数据
 * @param value 要添加的值
 * @param key 可空, 如果比较的是对象, 可能存在地址不一样但值实际上是一样的情况,
 * 可以传此字段判断对象中唯一的字段;
 * @returns {boolean} 成功 push 返回 true, 不处理返回 false
 */
export function pushIfNotExist(array, value, key) {
    for (let item of array) {
        if (key && (item[key] === value[key])) {
            return false
        } else if (item === value) {
            return false
        }
    }
    array.push(value)
    return true
}

/**
 * 可用于判断是否成功
 * @type {symbol}
 */
export const succeedSymbol = Symbol()
/**
 * 可用于判断是否失败
 * @type {symbol}
 */
export const failedSymbol = Symbol()

/**
 * 使 promise 无论如何都会 resolve, 除非传入的参数不是一个Promise对象或返回Promise对象的方法
 * 一般用在 Promise.all 中
 *
 * @param promise 可传Promise对象或返回Promise对象的方法
 * @returns {Promise<any>}
 */
export function alwaysResolve(promise) {
    return new Promise((resolve, reject) => {
        let p = promise
        if (typeof promise === 'function') {
            p = promise()
        }
        if (p instanceof Promise) {
            p.then(data => {
                resolve({type: succeedSymbol, data})
            }).catch(error => {
                resolve({type: failedSymbol, error})
            })
        } else {
            reject('alwaysResolve: 传入的参数不是一个Promise对象或返回Promise对象的方法')
        }
    })
}

/**
 * 简单实现防抖方法
 *
 * 防抖(debounce)函数在第一次触发给定的函数时, 不立即执行函数, 而是给出一个期限值(delay), 比如100ms
 * 如果100ms内再次执行函数, 就重新开始计时, 直到计时结束后再真正执行函数
 * 这样做的好处是如果短时间内大量触发同一事件, 只会执行一次函数
 *
 * @param fn 要防抖的函数
 * @param delay 防抖的毫秒数
 * @returns {Function}
 */
export function simpleDebounce(fn, delay = 100) {
    let timer = null
    return function () {
        let args = arguments
        if (timer) {
            clearTimeout(timer)
        }
        timer = setTimeout(() => {
            fn.apply(this, args)
        }, delay)
    }
}

/**
 * 不用正则的方式替换所有值
 * @param text 被替换的字符串
 * @param checker  替换前的内容
 * @param replacer 替换后的内容
 * @returns {String} 替换后的字符串
 */
export function replaceAll(text, checker, replacer) {
    let lastText = text
    text = text.replace(checker, replacer)
    if (lastText !== text) {
        return replaceAll(text, checker, replacer)
    }
    return text
}

/**
 * 获取事件冒泡路径, 兼容 IE11, Edge, Chrome, Firefox, Safari
 * 目前使用的地方: JEditableTable Span模式
 */
export function getEventPath(event) {
    let target = event.target
    let path = (event.composedPath && event.composedPath()) || event.path

    if (path != null) {
        return (path.indexOf(window) < 0) ? path.concat(window) : path
    }

    if (target === window) {
        return [window]
    }

    let getParents = (node, memo) => {
        memo = memo || []
        const parentNode = node.parentNode

        if (!parentNode) {
            return memo
        } else {
            return getParents(parentNode, memo.concat(parentNode))
        }
    }
    return [target].concat(getParents(target), window)
}

/**
 * 根据组件名获取父级
 * @param vm
 * @param name
 * @returns {Vue | null|null|Vue}
 */
export function getVmParentByName(vm, name) {
    let parent = vm.$parent
    if (parent && parent.$options) {
        if (parent.$options.name === name) {
            return parent
        } else {
            let res = getVmParentByName(parent, name)
            if (res) {
                return res
            }
        }
    }
    return null
}

/**
 * 使一个值永远不会为（null | undefined）
 *
 * @param value 要处理的值
 * @param def 默认值, 如果value为（null | undefined）则返回的默认值, 可不传, 默认为''
 */
export function neverNull(value, def) {
    return value == null ? (neverNull(def, '')) : value
}

/**
 * 根据元素值移除数组中的一个元素
 * @param array 数组
 * @param prod 属性名
 * @param value 属性值
 * @returns {string}
 */
export function removeArrayElement(array, prod, value) {
    let index = -1
    for (let i = 0; i < array.length; i++) {
        if (array[i][prod] === value) {
            index = i;
            break;
        }
    }
    if (index >= 0) {
        array.splice(index, 1);
    }
}

/** 判断是否是OAuth2APP环境 */
export function isOAuth2AppEnv() {
    return /wxwork|dingtalk/i.test(navigator.userAgent)
}

/**
 * JS实现AOP切面
 *
 * @param obj 包含函数的对象
 * @param funcName 要切面的函数名
 * @param callback 执行方法前的回调, 用于切面, callback的返回值就是funcName最终的返回值
 */
export function aspectAroundFunction(obj, funcName, callback) {
    if (typeof callback !== 'function' || !obj) {
        console.warn('[aspectAroundFunction]obj或callback格式不正确')
        return
    }
    // 保存原来的函数
    let func = obj[funcName]
    if (typeof func !== 'function') {
        console.warn('[aspectAroundFunction]' + funcName + '不是一个方法')
        return
    }
    // 赋值新方法
    // 实现当外部调用 funcName 时, 首先调用我定义的新方法
    // 然后调用传入的callback方法, 以决定是否执行 funcName, 以及更改参数、返回值
    obj[funcName] = function (...args) {
        return callback({
            args,
            // 只有执行 proceed 才会真正执行给定的 funcName 方法
            proceed() {
                try {
                    return func.apply(obj, args)
                } catch (e) {
                    console.error(e)
                }
            },
        })
    }
}