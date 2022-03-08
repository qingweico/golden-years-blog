<template>
  <div>
    <div v-for="item in comments" :key="item.commentId">
      <div class="commentList">
        <span class="left p1">
          <img v-if="item.commentUserFace" :src="item.commentUserFace ? item.commentUserFace:defaultAvatar"
               onerror="onerror=null;src=defaultAvatar" alt=""/>
          <img v-else :src="defaultAvatar" alt=""/>
        </span>

        <span class="right p1">
          <div class="rightTop" v-if="item.commentUserNickname">
            <el-link class="userName" :underline="false">{{ item.commentUserNickname }}</el-link>
            <span class="timeAgo" v-if="item.createTime">{{ timeAgo(item.createTime) }}</span>
            <span class="timeAgo" v-else>刚刚</span>
          </div>

          <div class="rightCenter ck-content" v-highlight v-html="item.content"></div>

          <div class="rightBottom">
            <el-link class="b1" :underline="false" @click="replyTo(item)">回复</el-link>
            <el-link class="b1" v-if="$store.state.user.isLogin && $store.state.user.userInfo.id === item.commentUserId"
                     :underline="false" @click="delComment(item)">删除</el-link>
          </div>
          <!-- 回复的内容, 如果判断是回复别人的留言则展示-->
          <div class="reply-wrapper" v-show="item.quoteUserNickname != null">
                  <span><strong>{{ item.commentUserNickname }}</strong> 回复: {{ item.quoteUserNickname }}</span><span
              v-html="item.quoteContent"></span>
          </div>


          <div class="rightCommentList">
            <CommentBox class="comment" :userInfo="userInfo" :replyToInfo="replyToInfo" :id="item.commentId"
                        :commentInfo="commentInfo"
                        :articleId="articleId"
                        @submit-box="submitBox" @cancel-box="cancelBox"></CommentBox>
          </div>
        </span>
      </div>
    </div>
  </div>

</template>

<script>
import CommentBox from "../CommentBox";
import {deleteComment, getCommentList, publishComment} from "@/api/comment";
import {timeAgo} from "@/utils/web";

export default {
  name: "CommentList",
  props: ['comments', 'commentInfo'],
  data() {
    return {
      commentReply: {
        articleId: "",
        fatherId: "",
        commentUserId: "",
        content: ""
      },
      articleId: this.$route.query.id,
      taggleStatue: true,
      submitting: false,
      value: '',
      replyToInfo: {
        userId: "",
        commentId: ""
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
    replyTo(item) {
      if (!this.validLogin()) {
        this.$notify.info({
          title: '提示',
          message: "登录后才能回复评论"
        });
        return
      }
      let commentUserNickname = item.commentUserNickname;
      let commentId = item.commentId;
      let lists = document.getElementsByClassName("comment");
      for (let i = 0; i < lists.length; i++) {
        lists[i].style.display = 'none';
        lists[i].style.placeholder = '回复: ' + commentUserNickname;
      }
      document.getElementById(commentId).style.display = 'block';
      this.replyToInfo.commentId = commentId;
      this.replyToInfo.userId = item.commentUserId;
    },
    submitBox(e) {
      let params = {};
      params.articleId = e.articleId;
      params.fatherId = e.fatherId;
      params.commentUserId = e.commentUserId;
      params.content = e.content;
      publishComment(params).then(response => {
            console.log(response.data);
            if (response.data.success) {
              document.getElementById(e.fatherId).style.display = 'none';
              this.$notify({
                title: '成功',
                message: "评论成功",
                type: 'success',
              });
            } else {
              this.$notify.error({
                title: '错误',
                message: response.data,
                type: 'success',
              });
            }
          }
      )
      ;
    },
    cancelBox(ToCommentId) {
      document.getElementById(ToCommentId).style.display = 'none'
    }
    ,
    // taggleAll: function (item) {
    //
    //   this.taggleStatue = !this.taggleStatue;
    //   var lists = document.getElementsByClassName("commentStyle");
    //   for (var i = 0; i < lists.length; i++) {
    //     lists[i].style.display = 'block';
    //   }
    //   if (this.taggleStatue) {
    //     document.getElementById('commentStyle:' + item.uid).style.display = 'block';
    //   } else {
    //     document.getElementById(item.uid).style.display = 'none';
    //   }
    // },
    delComment: function (item) {
      if (!this.validLogin()) {
        this.$notify.info({
          title: '提示',
          message: "登录后才能删除评论",
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
            });

          } else {
            this.$notify.error({
              title: '错误',
              message: response.data.msg,
            });
          }
          //    this.deleteCommentList(comments, params.uid, null)
        });

      })
          .catch(() => {
            /*取消action**/
          });


    },
    // 校验是否登录
    validLogin() {
      return !!this.$store.state.user.userInfo;
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
