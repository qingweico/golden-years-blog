'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',

  ADMIN_API: '"http://localhost:8003"',
  ARTICLE_API: '"http://localhost:8004"',
  USER_API: '"http://localhost:8001"',
  PIC_API: '"http://localhost:8002"',
})
