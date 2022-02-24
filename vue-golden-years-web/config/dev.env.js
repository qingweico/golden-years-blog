'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')
// 开发环境
module.exports = merge(prodEnv, {
    NODE_ENV: '"development"',
    ADMIN_API: '"http://localhost:8003"',
    USER_API: '"http://localhost:8001"',
    PIC_API: '"http://localhost:8002"',
    ARTICLE_API: '"http://localhost:8004"'
})
