## 项目介绍

流金岁月博客系统是前后端分离的博客博客平台，项目采用Spring Cloud + Vue 开发，项目加入常见的企业级应用所涉及到的技术点，例如 Redis、RabbitMQ 、ElasticSearch、MongoDB、FastDFS等。

- 项目线上地址: http://blog.qingweico.cn
- 项目后台地址：http://106.12.136.221/
- 项目源码地址： https://github.com/qingweico/golden-years-blog

### 项目技术栈

#### 后端技术栈

- Spring Cloud
- MyBatis
- MySQL
- Redis
- RabbitMQ
- ElasticSearch
- 阿里云API(人脸识别)

#### 前端技术栈

- Vue
- ElementUI
- axios
- vue-router
- vuex
- vue-cli

#### 项目效果图

项目主站效果图

![image-20220520193405096](https://cdn.qingweico.cn/image-20220520193405096.png)

用户创作中心效果图

![image-20220520193429193](https://cdn.qingweico.cn/image-20220520193429193.png)

管理员登录界面

![image-20220520193739894](https://cdn.qingweico.cn/image-20220520193739894.png)

管理员人脸登录界面

![image-20220520193916820](https://cdn.qingweico.cn/image-20220520193916820.png)

管理员文章管理界面

![image-20220520193846764](https://cdn.qingweico.cn/image-20220520193846764.png)

...

## 快速部署

- clone 项目到本地 `https://github.com/qingweico/golden-years-blog`
- 导入数据库脚本`golden-years-blo.sql`
- 提前准备好 Redis，在项目的 application.yml 文件中，将 Redis 配置改为自己的地址和密码
- MySQL采用MySQL 8.0，同样在application.yml 文件中将MySQL配置信息修改为自己的
- 其他中间件比如RabbitMQ、ElasticSearch、MongoDB、FastDFS也需要修改配置信息
- 具体安装和启动中间件请参考[我的笔记](https://github.com/qingweico/back-end-notes)
- 项目启动时需要启动的jar包括gateway、admin、files、article和user

OK, 至此, 服务端就启动成功了, 接下来需要启动项目前端

进入vue-golden-years-web目录

```shell
# 安装依赖
npm install

# 在 localhost:9527 启动项目
npm run dev

# 打包项目
npm run build
```

后端项目启动一样的过程, 进入vue-golden-years-admin目录, 参照前面的过程即可

## 参考

[蘑菇博客](https://gitee.com/moxi159753/mogu_blog_v2?_from=gitee_search)