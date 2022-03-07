<template>
  <div>
    <div v-for="item in comments" :key="item.uid">
      <div class="commentList">
        <span class="left p1">
          <img v-if="item.user" :src="item.user.photoUrl ? item.user.photoUrl:defaultAvatar"
               onerror="onerror=null;src=defaultAvatar"  alt=""/>
          <img v-else :src="defaultAvatar"  alt=""/>
        </span>

        <span class="right p1">
          <div class="rightTop" v-if="item.user">
            <el-link class="userName" :underline="false">{{item.user.nickName}}</el-link>
            <span class="timeAgo" v-if="item.createTime">{{timeAgo(item.createTime)}}</span>
            <span class="timeAgo" v-else>刚刚</span>
          </div>

          <div class="rightCenter ck-content" v-highlight v-html="item.content"></div>

          <div class="rightBottom">
            <el-link class="b1" :underline="false" @click="replyTo(item)">回复</el-link>
            <el-link class="b1" v-if="$store.state.user.isLogin && $store.state.user.userInfo.uid === item.userUid"
                     :underline="false" @click="delComment(item)">删除</el-link>
          </div>

          <div class="rightCommentList">
            <CommentBox class="comment" :userInfo="userInfo" :toInfo="toInfo" :id="item.uid" :commentInfo="commentInfo"
                        @submit-box="submitBox" @cancel-box="cancelBox"></CommentBox>

            <CommentList class="commentStyle" :id="'commentStyle:' + item.uid" :comments="item.replyList"
                         :commentInfo="commentInfo"></CommentList>
          </div>
        </span>
      </div>
    </div>
  </div>

</template>

<script>

  import {mapMutations} from 'vuex';
  import CommentBox from "../CommentBox";
  import {deleteComment, getCommentList} from "@/api/comment";
  import {timeAgo} from "@/utils/web";
  export default {
    name: "CommentList",
    props: ['comments', 'userInfos', 'commentInfo'],
    data() {
      return {
        taggleStatue: true,
        submitting: false,
        value: '',
        toInfo: {
          uid: "",
          commentUid: ""
        },
        userInfo: {},
        defaultAvatar: this.$SysConf.defaultAvatar
      };
    },
    created() {

    },
    components: {
      CommentBox
    },
    mounted() {

    },
    compute: {},
    methods: {
      replyTo: function (item) {
        if(!this.validLogin()) {
          this.$notify.info({
            title: '提示',
            message: "登录后才能回复评论哦~",
            offset: 100
          });
          return
        }
        let userUid = item.userUid;
        let commentUid = item.uid;
        var lists = document.getElementsByClassName("comment");
        for (var i = 0; i < lists.length; i++) {
          lists[i].style.display = 'none';
        }
        document.getElementById(commentUid).style.display = 'block';
        this.toInfo.commentUid = commentUid
        this.toInfo.uid = userUid
      },
      submitBox(e) {
        console.log("添加内容", e)
        let params = {};
        params.userUid = e.userUid;
        params.content = e.content;
        params.blogUid = e.blogUid;
        params.toUid = e.toCommentUid;
        params.toUserUid = e.toUserUid;
        params.source = e.source
        addComment(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              let commentData = response.data
              document.getElementById(commentData.toUid).style.display = 'none'
              let comments = this.$store.state.app.commentList;
              commentData.user = this.userInfo;
              // 设置回复为空
              commentData.replyList = [];
              commentData.user = this.$store.state.user.userInfo
              this.updateCommentList(comments, commentData.toUid, commentData)
                this.$notify({
                  title: '成功',
                  message: "评论成功",
                  type: 'success',
                  offset: 100
                });
            }  else {
                this.$notify.error({
                  title: '错误',
                  message: response.data,
                  type: 'success',
                  offset: 100
                });
            }
          }
        )
        ;
      },
      getCommentList: function () {
        let params = {};
        params.currentPage = 0;
        params.pageSize = 10;
        getCommentList(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.comments = response.data;
          }
        });
      }
      ,
      cancelBox(toCommentUid) {
        document.getElementById(toCommentUid).style.display = 'none'
      }
      ,
      taggleAll: function (item) {

        this.taggleStatue = !this.taggleStatue;
        var lists = document.getElementsByClassName("commentStyle");
        for (var i = 0; i < lists.length; i++) {
          lists[i].style.display = 'block';
        }
        if (this.taggleStatue) {
          document.getElementById('commentStyle:' + item.uid).style.display = 'block';
        } else {
          document.getElementById(item.uid).style.display = 'none';
        }
      },
      delComment: function (item) {
        if(!this.validLogin()) {
          this.$notify.info({
            title: '提示',
            message: "登录后才能删除评论",
            offset: 100
          });
          return
        }

        this.$confirm("此操作将把本评论和子评论删除, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
            let params = {};
            params.uid = item.uid;
            params.userUid = this.$store.state.user.userInfo.uid
            deleteComment(params).then(response => {
              if (response.data.success) {
                this.$notify({
                  title: '成功',
                  message: response.data.msg,
                  type: 'success',
                  offset: 100
                });

              } else {
                this.$notify.error({
                  title: '错误',
                  message: response.data.msg,
                  offset: 100
                });
              }
              let comments = this.$store.state.app.commentList;
              this.deleteCommentList(comments, params.uid, null)
            });

          })
          .catch(() => {
            /*取消action**/
          });


      },
      // 校验是否登录
      validLogin() {
        let userInfo = this.$store.state.user.userInfo
        return userInfo === {};
      },

      timeAgo(dateTimeStamp) {
        return timeAgo(dateTimeStamp)
      },
      updateCommentList(commentList, uid, targetComment) {
        if (commentList === undefined || commentList.length <= 0) {
          return;
        }
        for (let item of commentList) {
          if (item.uid === uid) {
            var menu = item.replyList;
            menu.push(targetComment);
          } else {
            this.updateCommentList(item.replyList, uid, targetComment);
          }
        }
      },
      deleteCommentList(commentList, uid, parentList) {
        if (commentList === undefined || commentList.length <= 0) {
          return;
        }
        for (let item of commentList) {
          if (item.uid === uid) {
            commentList.splice(commentList.indexOf(item), 1);
          } else {
            this.deleteCommentList(item.replyList, uid, item);
          }
        }
      },
    },
  };
</script>

<style scoped>
  .commentStyle {
    display: block;
    margin-top: 10px;
    margin-left: 10px;
    border-left: 1px dashed SlateGray;
  }
  .comment {
    display: none;
  }
  .commentList {
    width: 100%;
    margin: 0 auto;
  }
  .commentList .p1 {
    float: left;
  }
  .commentList .left {
    display: inline-block;
    width: 5%;
    height: 100%;
  }
  .commentList .left img {
    margin: 0 auto;
    width: 100%;
    border-radius: 50%;
  }
  .commentList .right {
    display: inline-block;
    width: 93%;
    margin-left: 5px;
  }
  .commentList .rightTop {
    height: 30px;
    margin-top: 2px;
  }
  .commentList .rightTop .userName {
    color: #303133;
    margin-left: 10px;
    font-size: 16px;
    font-weight: bold;
  }
  .commentList .rightTop .timeAgo {
    color: #909399;
    margin-left: 10px;
    font-size: 15px;
  }
  .commentList .rightCenter {
    margin-left: 20px;
    min-height: 50px;
    margin-top: 15px;
  }
  .commentList .rightBottom {
    margin-left: 10px;
    height: 30px;
  }
  .commentList .rightBottom el-link {

  }
  .commentList .rightBottom .b1 {
    margin-left: 10px;
  }

  @media only screen and (min-width: 300px) and (max-width: 767px) {
    .commentList .left {
      display: inline-block;
      width: 30px;
      height: 100%;
    }
    .commentList .right {
      display: inline-block;
      width: calc(100% - 35px);
      margin-left: 5px;
    }
  }
</style>
