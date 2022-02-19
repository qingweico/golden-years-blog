<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=1.0, user-scalable=0">

    <link rel="stylesheet" type="text/css" href="../css/app.css"/>
    <link rel="stylesheet" type="text/css" href="../css/index/topHeader.css"/>
    <link rel="stylesheet" type="text/css" href="../css/detail/detail.css"/>
    <link rel="stylesheet" type="text/css" href="../css/detail/comments.css"/>
    <link rel="stylesheet" type="text/css" href="../css/detail/middleBar.css"/>
    <link rel="stylesheet" type="text/css" href="../libs/JqueryShare/share.min.css"/>

    <!-- 分页css -->
    <link href="../libs/vuePaging/VuePaging/zpageNav.css" rel="stylesheet"/>

    <link rel="shortcut icon" href="../img/whale.svg"/>

    <style>
        .social-share .social-share-icon {
            width: 32px;
            height: 32px;
            line-height: 32px;
        }

        [v-cloak] {
            display: none;
        }
    </style>

    <title id="pageTitle">${articleDetail.title}</title>
</head>

<body>
<div id="detailContainer" v-cloak>
    <!-- 顶部导航头 header -->
    <div class="white-header">
        <div class="white-bar">
            <div class="left-part">
                <div class="logo-wrapper" @click="goIndex()">
                    <img src="../img/logo.png" class="big-logo" alt=""/>
                </div>

                <div class="menus">
                    <!-- single-selected -->
                    <div class="single-default" v-bind:class="{'single-selected': (cat.id === selectedCatId)}"
                         v-for="(cat, index) in catList" v-key="index">
                        <!-- menu-selected -->
                        <a :href="'/golden-years-ui/portal/index.html?catId='+cat.id" class="white-header-left"
                           v-bind:class="{'menu-selected': (cat.id === selectedCatId)}">{{cat.name}}</a>
                    </div>
                </div>
            </div>

            <div class="right-part">
                <!-- 如果登录则不显示 -->
                <span v-if="userInfo == null">
                    <a href="javascript:(0);" @click="doLogin()" target="_blank" class="white-header-right-start">登录 |
                        注册</a>
                </span>
                <a href="javascript:(0);" @click="goWriterCenter()" class="white-header-right">作家中心</a>
            </div>
        </div>
    </div>

    <!-- 中间主体部分 -->
    <div class="container">

        <div class="big-title">
            ${articleDetail.title}
        </div>
        <div class="read-counts">
            阅读量: {{readCounts}}
        </div>

        <div class="detail-really">

            <!-- 左侧分享 -->
            <div class="left-social">

                <div class="date-title">
                    <span class="year">${articleDetail.publishTime?string('yyyy')}</span>
                </div>
                <div class="back-year-line"></div>

                <div class="date-md">${articleDetail.publishTime?string('MM/dd')}</div>

                <div class="date-times">${articleDetail.publishTime?string('HH:mm:ss')}</div>

                <div class="writer-name" @click="showWriter('${articleDetail.publishUserId}')">
                    <span class="label label-info">${articleDetail.publishUserName}</span>
                </div>

                <div class="share-title">
                    <span class="share-words">分享</span>
                </div>
                <div class="back-line"></div>

                <div class="social-line" @click="shareWeiBo">
                    <img src="../img/social/weibo.png" class="social-pic"/>
                    <span class="social-words">微博</span>
                </div>
                <div class="social-line" @click="shareDouBan">
                    <img src="../img/social/douban.png" class="social-pic"/>
                    <span class="social-words">豆瓣</span>
                </div>
                <div class="social-line" @click="shareQQ">
                    <img src="../img/social/qq.png" class="social-pic"/>
                    <span class="social-words">QQ</span>
                </div>
                <div class="social-line" @click="shareQZone">
                    <img src="../img/social/qzone.png" class="social-pic"/>
                    <span class="social-words">QQ空间</span>
                </div>
                <div class="social-line" @click="shareWeChat">
                    <div class="social-share" data-wechat-qrcode-title="请打开微信扫一扫"
                         data-disabled="google,twitter,facebook,douban,qzone,qq,weibo,tencent,linkedin,diandian">
                    </div>
                    <span class="social-words">微信</span>
                </div>
            </div>

            <!-- 中间文章主体 -->
            <div class="container-middle">
                <div class="article-wrapper">

                    <div class="content">
                        ${articleDetail.content}
                    </div>

                    <div class="declare">

                    </div>
                </div>

                <div class="comments-wrapper">

                    <div class="comments-words">
                        <div class="comments-words-left">
                            <div class="comments-big">留言评论</div>
                            <div class="comments-warn">文明上网理性发言</div>
                        </div>
                        <div class="comments-counts">{{commentCounts}}条评论</div>

                    </div>


                    <div class="main-comment-content-wrapper">
                        <label class="main-comment">
                            <textarea class="form-control main-content" rows="3" @input="hasComment()"
                                      v-model="commentContent"></textarea>
                        </label>
                        <div>
                            <button class="reply-btn" @click="doComment(0)">发表评论</button>
                        </div>
                    </div>


                    <div class="all-comments-wrapper">
                        <div class="all-comments">所有评论</div>
                    </div>

                    <div class="all-comments-list-wrapper">
                        <div class="all-comments-list" v-for="(comment,index) in commentList" :key="index">

                            <div class="single-comment-wrapper">
                                <img :src="comment.commentUserFace" class="user-face" alt="commentUserFace"/>
                            </div>


                            <div class="comment-info-wrapper">

                                <div class="comment-info">
                                    <div>{{comment.commentUserNickname}}</div>
                                    <!-- 回复的内容,如果判断是回复别人的留言, 则再此引用一下 -->
                                    <div class="quote-wrapper" v-show="comment.quoteUserNickname != null">
                                        <span class="quote-content">
                                            <span><strong>{{comment.quoteUserNickname}}</strong>: </span><span
                                                    v-html="comment.quoteContent"></span>
                                        </span>
                                    </div>
                                    <div class="publish-content" v-html="comment.content"></div>
                                </div>


                                <div class="comment-property">
                                    <div class="publish-info">
                                        <div class="publish-date">{{formatData(comment.createTime)}}</div>
                                    </div>

                                    <div class="reply-wrapper" @click="doReply(comment.commentId)">
                                        <span class="icon-reply"></span>
                                        <div class="reply-words">回复</div>
                                    </div>

                                </div>

                                <!-- nowReplyingFatherCommentId: 根据当前用户正在回复的父commentId进行页面的留言看展示或隐藏 -->
                                <div class="reply-content-wrapper"
                                     v-show="nowReplyingFatherCommentId === comment.commentId">
                                    <label class="reply-comment">
                                        <textarea :id="'reply-to-' + comment.commentId" @input="hasComment()"
                                                  v-model="replyContent"
                                                  class="form-control"
                                                  rows="3"
                                                  :placeholder="'回复 @' + comment.commentUserNickname + ':'"></textarea>
                                    </label>
                                    <button class="reply-btn" @click="replyToComment(comment.commentId)">发表评论</button>
                                </div>
                            </div>
                        </div>

                        <!-- 分页 start-->
                        <div class="wrap" id="wrap">
                            <zpagenav v-bind:page="page" v-bind:page-size="pageSize" v-bind:total="total"
                                      v-bind:max-page="maxPage" v-on:pagehandler="doPaging">
                            </zpagenav>
                        </div>
                        <!-- 分页 end-->

                    </div>
                </div>
            </div>
            <div class="blank-right"></div>
        </div>
    </div>
</div>
<!-- footer -->
<div class="footer">
    <div class="all-words-wrapper">
        <span class="copyright-info">Copyright © 2020 流金岁月 All Rights Reserved</span>
    </div>
</div>


<script src="../libs/vue.min.js"></script>
<script src="../libs/axios.min.js"></script>

<link href="../libs/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="../libs/jquery-3.4.1.min.js"></script>
<script src="../libs/bootstrap/js/bootstrap.min.js"></script>
<link href="../libs/summernote/dist/summernote.css" rel="stylesheet">
<script src="../libs/summernote/dist/summernote.js"></script>


<script src="../libs/summernote/lang/summernote-zh-CN.js"></script>
<script src="../libs/vuePaging/VuePaging/zpageNav.js"></script>
<script src="../js/app.js"></script>
<script src="../libs/JqueryShare/jquery.share.min.js"></script>
<script src="../libs/moment-with-locales.min.js"></script>
<script src="../libs/layer/layer.js"></script>

<script type="text/javascript">

    const articleList = new Vue({
        el: "#detailContainer",
        data: {
            // 根据当前用户正在回复的父commentId进行页面的留言看展示或隐藏
            nowReplyingFatherCommentId: 0,
            userInfo: null,

            articleId: "",
            articleDetail: {},
            readCounts: 0,

            // 文章分类列表
            catList: [],
            // 被选中的当前的分类id
            selectedCatId: "",


            // 评论列表
            commentList: [],
            commentCounts: 0,
            commentContent: "",
            replyContent: "",

            // 分页页数
            page: 1,
            // 分页每页显示数量
            pageSize: 10,
            // 总页数
            maxPage: 1,
            // 总记录数
            total: 1,
        },
        created() {
            const me = this;
            // 通过cookie判断用户是否登录
            app.judgeUserLoginStatus(me);

            let articleId = app.getPageName();
            this.articleId = articleId;

            // 文章阅读数累加
            this.readArticle(articleId);
            // 获得文章阅读数
            this.getArticleReadCounts(articleId);

            // 获得分类
            this.getAllCategory();

            // 获得文章的评论列表
            this.getAllComments(me.page, me.pageSize);
            // 获得文章评论数
            this.getCommentCounts();
        },
        mounted() {
        },
        methods: {
            // 文章阅读数累加
            readArticle(articleId) {

                let articleServerUrl = app.articleServerUrl;
                axios.defaults.withCredentials = true;
                axios.post(articleServerUrl + "/portal/article/readArticle?articleId=" + articleId)
            },

            // 获得文章阅读数
            getArticleReadCounts(articleId) {
                let articleServerUrl = app.articleServerUrl;
                axios.defaults.withCredentials = true;
                axios.get(articleServerUrl + "/portal/article/readCounts?articleId=" + articleId)
                    .then(res => {
                        this.readCounts = res.data;
                    });
            },

            // 跳转作家页面
            showWriter(writerId) {
                window.open("../writer.html?writerId=" + writerId);
            },
            // 查询文章详情
            getArticleDetail(articleId) {
                const me = this;
                let articleServerUrl = app.articleServerUrl;
                axios.defaults.withCredentials = true;
                axios.get(articleServerUrl + "/portal/article/detail?articleId=" + articleId)
                    .then(res => {
                        if (res.data.status === 200) {
                            let result = res.data.data;
                            me.articleDetail = result;
                            $("#pageTitle").html(result.title);
                        } else {
                            layer.msg(res.data.msg, {icon: 2});
                        }
                    });
            },

            // 获得文章评论数
            getCommentCounts() {
                const me = this;
                const articleServerUrl = app.articleServerUrl;
                axios.defaults.withCredentials = true;
                axios.get(articleServerUrl + "/comment/counts?articleId=" + me.articleId)
                    .then(res => {
                        if (res.data.status === 200) {
                            me.commentCounts = res.data.data;
                        } else {
                            layer.msg(res.data.msg, {icon: 2});
                        }
                    });
            },

            // 获得文章的评论列表
            getAllComments(page, pageSize) {
                const me = this;
                const articleServerUrl = app.articleServerUrl;
                axios.defaults.withCredentials = true;
                axios.get(articleServerUrl + "/comment/list?articleId=" + me.articleId +
                    "&page=" + page +
                    "&pageSize=" + pageSize)
                    .then(res => {
                        if (res.data.status === 200) {
                            let grid = res.data.data;
                            me.commentList = grid.rows;

                            me.page = grid.page;      // 当前页数累加1,用于后续页面滚动分页
                            let maxPage = grid.total; // 获得总页数
                            let total = grid.records; // 获得总记录数

                            this.maxPage = maxPage;
                            this.total = total;

                        } else {
                            layer.msg(res.data.msg, {icon: 2});
                        }
                    });
            },

            // 点击回复出现回复框
            doReply(fatherCommentId) {
                // 如果用户点击的当前的评论回复id和nowReplyingFatherCommentId一致,则隐藏,如果不一致,则显示
                if (fatherCommentId === this.nowReplyingFatherCommentId) {
                    this.nowReplyingFatherCommentId = 0;
                } else {
                    this.nowReplyingFatherCommentId = fatherCommentId;
                }
            },

            // 用户留言或回复
            doComment(fatherCommentId) {
                this.commentDisplay(fatherCommentId, this.commentContent);
            },
            // 调用后端,留言保存
            commentDisplay(fatherCommentId, articleContent) {

                const me = this;
                let u_token = app.getCookie("user_token");
                let u_id = app.getCookie("user_id");


                let commentReplyBO = {
                    articleId: me.articleId,
                    fatherId: fatherCommentId,
                    commentUserId: u_id,
                    content: articleContent
                };

                const articleServerUrl = app.articleServerUrl;
                axios.defaults.withCredentials = true;
                axios.post(articleServerUrl + '/comment/publish',
                    commentReplyBO,
                    {
                        headers: {
                            'headerUserId': u_id,
                            'headerUserToken': u_token
                        }
                    }
                )
                    .then(res => {
                        if (res.data.status === 200) {
                            // 清空评论框中内容
                            if (fatherCommentId === 0) {
                                this.commentContent = "";
                            } else {
                                this.replyContent = "";
                            }

                            // 重新查询评论与评论数
                            me.getAllComments(this.page, this.pageSize);
                            me.getCommentCounts();
                            layer.msg("评论成功!", {icon: 6});
                        } else {
                            if(res.data.data.commentUserId !== undefined) {
                                layer.msg(res.data.data.commentUserId, {icon: 5});
                                return;
                            }
                            if(res.data.data.content !== undefined) {
                                layer.msg(res.data.data.content, {icon: 5});
                                if (fatherCommentId === 0) {
                                    $(".main-comment").addClass("has-error");
                                } else {
                                    $(".reply-comment").addClass("has-error");
                                }
                            }
                        }
                    });
            },
            // 用户回复其他用户的评论,点击后保存到后端
            replyToComment(fatherCommentId) {
                let content = $("#reply-to-" + fatherCommentId).val();
                this.commentDisplay(fatherCommentId, content);
            },
            hasComment() {
                if (this.commentContent !== '') {
                    $(".main-comment").removeClass("has-error");
                }
                if (this.replyContent !== '') {
                    $(".reply-comment").removeClass("has-error");
                }
            },

            // 获得所有文章领域
            getAllCategory() {
                const me = this;
                const adminServerUrl = app.adminServerUrl;
                axios.defaults.withCredentials = true;
                axios.get(adminServerUrl + "/category/getCategoryListForUser")
                    .then(res => {
                        if (res.data.status === 200) {
                            me.catList = res.data.data;
                        } else {
                            layer.msg(res.data.msg, {icon: 2});
                        }
                    });
            },
            // 根据分类Id获得tag颜色
            getCatTagColor(catId) {
                let catList = this.catList;
                for (let i = 0; i < catList.length; i++) {
                    if (catId === catList[i].id) {
                        return catList[i].tagColor;
                    }
                }
            },
            // 根据分类Id获得名称
            getCatName(catId) {
                let catList = this.catList;
                for (let i = 0; i < catList.length; i++) {
                    if (catId === catList[i].id) {
                        return catList[i].name;
                    }
                }
            },
            // 格式化日期
            formatData(times) {
                return moment(times).format('YYYY-MM-DD hh:mm:ss');
            },
            formatDataYear(times) {
                return moment(times).format('YYYY');
            },
            formatDataMonthDay(times) {
                return moment(times).format('MM/DD');
            },
            formatDataTime(times) {
                return moment(times).format('hh:mm:ss');
            },

            // 分页实现方法 跳转到page页
            doPaging: function (page) {
                this.page = page;
                this.getAllComments(page, this.pageSize);
            },
            // 跳转登录
            doLogin() {
                window.location = app.writerLoginUrl;
            },
            goWriterCenter() {
                window.open(app.writerIndexUrl);
            },
            goAdminCenter() {
                window.open(app.adminCenterUrl);
            },
            // 以下都是社交分享
            shareWeiBo() {
                let weiBoShareUrl = 'http://v.t.sina.com.cn/share/share.php';
                weiBoShareUrl += '?url=' + encodeURIComponent(document.location);   //参数url设置分享的内容链接|默认当前页location,可选参数
                weiBoShareUrl += '&title=' + encodeURIComponent(document.title);    //参数title设置分享的标题|默认当前页标题,可选参数
                weiBoShareUrl += '&source=' + encodeURIComponent('');
                weiBoShareUrl += '&sourceUrl=' + encodeURIComponent('');
                weiBoShareUrl += '&content=' + 'utf-8';                             //参数content设置页面编码gb2312|utf-8,可选参数
                weiBoShareUrl += '&pic=' + encodeURIComponent('');                  //参数pic设置图片链接|默认为空,可选参数
                window.open(weiBoShareUrl, '_blank');
            },
            shareDouBan() {
                let douBanShareUrl = 'http://shuo.douban.com/!service/share?';
                douBanShareUrl += 'href=' + encodeURIComponent(location.href);      //分享的链接
                douBanShareUrl += '&name=' + encodeURIComponent(document.title);    //分享的标题
                window.open(douBanShareUrl, '_blank');
            },
            shareQQ() {
                let qqShareUrl = 'https://connect.qq.com/widget/shareqq/iframe_index.html?';
                qqShareUrl += 'url=' + encodeURIComponent(location.href);           //分享的链接
                qqShareUrl += '&title=' + encodeURIComponent(document.title);       //分享的标题
                window.open(qqShareUrl, '_blank');
            },
            shareQZone() {
                let qzoneShareUrl = 'https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?';
                qzoneShareUrl += 'url=' + encodeURIComponent(document.location);    // 参数url设置分享的内容链接|默认当前页location
                qzoneShareUrl += '&title=' + encodeURIComponent(document.title);    // 参数title设置分享标题,可选参数
                window.open(qzoneShareUrl, '_blank');
            },
            shareWeChat2() {
                let target_url = 'http://zixuephp.net/inc/qrcode_img.php?url=' + document.location.href;
                target_url = 'http://zixuephp.net/inc/qrcode_img.php?url=http://www.itzixi.com';
                window.open(target_url, 'weixin', 'height=320, width=320');
            },
            shareWeChat() {
            },
            // 前往首页
            goIndex() {
                window.location = app.portalIndexUrl;
            }
        },

    });
</script>
</body>

</html>