'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')
// 开发环境
module.exports = merge(prodEnv, {
    NODE_ENV: '"development"',
    WEB_API: '"http://localhost:7000"'
})
