import showdownKatex from 'showdown-katex'
//*********************************************************
//                      全局状态码
//*********************************************************
const Code = {
    SUCCESS: "200",
    ERROR: "error",
}

/**
 * 全局配置文件
 */
const SysConf = {
    // 默认头像
    defaultAvatar: "https://cdn.qingweico.cn/defaultAvatar.png",
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
    /**
     * 字符串转标签
     * @param str
     * @returns {Array}
     */
    stringToTags: str => {
        if (str !== null && str !== '') {
            return str.split(',')
        } else {
            return []
        }
    },
    // 切割字符串
    splitStr: (str, flagCount) => {
        if (str == null || str === '') {
            return ""
        } else if (str.length > flagCount) {
            return str.substring(0, flagCount) + "..."
        } else {
            return str
        }
    },
    /**
     * 将Markdown转成Html
     * @param text
     */
    markdownToHtml: text => {
        let converter = new showdown.Converter({
            tables: true,
            extensions: [
                showdownKatex({
                    // maybe you want katex to throwOnError
                    throwOnError: true,
                    // disable displayMode
                    displayMode: false,
                    // change errorColor to blue
                    errorColor: '#1500ff',
                }),
            ],
        });
        return converter.makeHtml(text);
    },
    /**
     * 将Html转成Markdown
     * @param text
     */
    htmlToMarkdown: text => {
        let turndownService = new TurndownService();

        // 用于提取代码语言
        turndownService.addRule('CodeBlock', {
            filter: function (node, options) {
                return (
                    node.nodeName === 'PRE' &&
                    node.firstChild &&
                    node.firstChild.nodeName === 'CODE'
                )
            },
            replacement: function (content, node, options) {
                var className = node.firstChild.getAttribute('class') || ''
                var language = (className.match(/language-(\S+)/) || [null, ''])[1]
                return (
                    '\n\n' + options.fence + language + '\n' +
                    node.firstChild.textContent +options.fence
                )
            }
        })

        // 提取数学公式进行转换
        turndownService.addRule('multiplemath', {
            filter (node, options) {
                return node.classList.contains('vditor-math')
            },
            replacement (content, node, options) {
                console.log("中间内容", node.firstChild.textContent)
                return `$$ \n${node.firstChild.textContent}\n $$`
            }
        })

        var turndownPluginGfm = require('turndown-plugin-gfm')
        var gfm = turndownPluginGfm.gfm
        var tables = turndownPluginGfm.tables
        var strikethrough = turndownPluginGfm.strikethrough
        turndownService.use(gfm)
        turndownService.use([tables, strikethrough])

        console.log("转换后", turndownService.turndown(text))
        return turndownService.turndown(text)
    },
    /**
     * 将Html转成Markdown文件
     * @param title：标题
     * @param text：正文
     */
    htmlToMarkdownFile: (title, text) => {
        title = title || "默认标题"
        let turndownService = new TurndownService()
        let markdown = turndownService.turndown(text)
        //创建一个blob对象,file的一种
        let blob = new Blob([markdown])
        let link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        //配置下载的文件名
        link.download = title + '.md'
        link.click()
    },
}

export default {
    Code,
    SysConf,
    FUNCTIONS
}
