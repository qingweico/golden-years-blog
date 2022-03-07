<template>
  <div>
    <div class="commentBox">
      <div v-if="isShowAvatar" class="left">
        <el-avatar icon="el-icon-user-solid" v-if="isShowAvatar" :src="getUserPhoto"
                   onerror="onerror=null;src=defaultAvatar"></el-avatar>
      </div>
      <span class="right">
      <textarea id="textpanel" class="textArea" placeholder="友善的评论是交流的起点" v-model="commentContent" @click="hideEmojiPanel"
                @input="validCount"></textarea>
    </span>
    </div>
    <div class="bottom">
      <el-button class="submit p2" type="primary" @click="handleSubmit">发送评论</el-button>
      <el-button class="cancel p2" type="info" @click="handleCancel">取消评论</el-button>

      <el-popover
          placement="top">
        <emoji-panel class="emojiPanel" @emojiClick="appendEmoji"></emoji-panel>
        <div class="emoji-panel-btn p2" @click="showEmojiPanel" slot="reference">
          <img src="../../../assets/img/face_logo.png" alt=""/>
        </div>
      </el-popover>
      <span class="allow p2" v-if="isShowAvatar">还能输入{{ count }}个字符</span>

    </div>
  </div>

</template>

<script>
import {dateFormat} from '@/utils/web'
import {mapGetters, mapMutations} from 'vuex';
import EmojiPanel from "@/components/EmojiPanel/EmojiPanel.vue";
import {getCommentList, publishComment} from "@/api/comment";

export default {
  name: 'CommentBox',
  props: {
    userInfo: {
      type: Object
    },
    // 回复的对象
    toInfo: {
      type: Object
    },
    // 博客信息
    commentInfo: {
      type: Object
    },
    showCancel: {
      type: Boolean,
      default: true
    }
  },
  components: {
    EmojiPanel
  },
  data() {
    return {
      commentReply: {
        articleId: "",
        fatherId: "",
        commentUserId: "",
        content: ""
      },
      articleId: "",
      submitting: false,
      commentContent: '',
      user: {},
      count: 1024,
      // 是否显示表情面板
      isShowEmojiPanel: false,
      // 是否显示头像
      isShowAvatar: true,
      defaultAvatar: this.$SysConf.defaultAvatar,
      nowReplyingFatherCommentId: 0,

    }
  },
  computed: {
    ...mapGetters(['getUserPhoto'])
  },
  mounted() {
    // 页面加载的时候调用监听
    this.hideEmojiPanel()

    // 获取宽高
    window.onresize = () => {
      return (() => {
        this.resizeWin();
      })();
    };
    this.resizeWin();
  },
  methods: {
    validCount: function () {
      let count = 1024 - this.value.length;
      if (count <= 0) {
        this.count = 0
      } else {
        this.count = count;
      }
    },
    handleSubmit() {
      let info = this.$store.state.user.userInfo
      let isLogin = this.$store.state.user.isLogin
      if (!isLogin) {
        this.$notify.info({
          title: '提示',
          message: '登录后才可以评论',
          offset: 100
        });
        return;
      }
      if (this.value === "") {
        this.$notify.info({
          title: '提示',
          message: '评论内容不能为空',
          offset: 100
        });
        return;
      }
      // 替换表情
      let content = this.value.replace(/:.*?:/g, this.emoji);
      this.value = '';
      this.count = 1024;
      //this.comments.createTime = dateFormat("YYYY-mm-dd HH:MM:SS", new Date());
      this.hideEmojiPanel()
      this.$emit("submit-box", this.comments)
    },
    emoji(word) {
      // 生成html
      const type = word.substring(1, word.length - 1);
      return `<span class="emoji-item-common emoji-${type} emoji-size-small"></span>`;
    },
    handleCancel() {
      this.value = '';
      this.count = 1024;
      this.isShowEmojiPanel = false
      if (this.toInfo) {
        this.$emit("cancel-box", this.toInfo.commentUid)
      }
      this.hideEmojiPanel()
    },
    showEmojiPanel() {
      this.isShowEmojiPanel = !this.isShowEmojiPanel;
    },
    hideEmojiPanel() {
      this.isShowEmojiPanel = false
    },
    appendEmoji(text) {
      this.value = this.value + ":" + text + ":";
    },
    resizeWin() {
      // 当前window 宽
      let centerWidth = document.documentElement.scrollWidth;
      this.isShowAvatar = centerWidth > 800;
    },
    getCommentList() {
      let params = new URLSearchParams();
      params.append("articleId", this.articleId);
      params.append("page", this.page);
      params.append("pageId", this.pageSize);
      getCommentList(params).then(response => {
        if (response.data.success) {

        } else {
          this.$message.error(response.data.msg);
        }
      })
    },
    // 点击回复出现回复框
    doReply(fatherCommentId) {
      // 如果用户点击的当前的评论回复id和nowReplyingFatherCommentId一致, 则隐藏, 如果不一致, 则显示
      if (fatherCommentId === this.nowReplyingFatherCommentId) {
        this.nowReplyingFatherCommentId = 0;
      } else {
        this.nowReplyingFatherCommentId = fatherCommentId;
      }
    },

    // 用户留言或回复
    doComment(fatherCommentId) {
      let replyContent = this.commentContent;
      this.commentDisplay(fatherCommentId, replyContent);
    },
    // 调用后端, 留言保存
    commentDisplay(fatherCommentId, replyContent) {
      this.commentReply.content = replyContent;
      publishComment(this.commentReply).then(response => {

        if (response.data.success) {
          // 清空评论框中内容
          this.commentContent = "";
          $("#reply-to-" + fatherCommentId).val("");

          // 重新查询评论与评论数
          this.getCommentList();
          this.$message.success(response.data.msg);
        } else {
          this.$message.error(response.data.msg);
        }
      });
    },
    // 用户回复其他用户的评论, 点击后保存到后端
    replyToComment(fatherCommentId) {
      let content = $("#reply-to-" + fatherCommentId).val();
      this.commentDisplay(fatherCommentId, content);
    },
  },
};
</script>


<style>
@import "../../../assets/css/emoji.css";

.el-popover {
  height: 220px;
  width: 420px;
}

.emoji-panel-wrap {
  box-sizing: border-box;
  border: 1px solid #cccccc;
  border-radius: 5px;
  background-color: #ffffff;
  width: 423px;
  height: 220px;
  position: absolute;
  z-index: 99;
  top: 10px;
}

.emoji-size-small {
  zoom: 0.3;
  margin: 5px;
  vertical-align: middle;
}

.emoji-size-large {
  zoom: 0.5;
  margin: 5px;
}

.commentBox {
  width: 100%;
  height: 100px;
  margin: 0 auto;
}

.commentBox .left {
  float: left;
  padding-top: 3px;
  margin-left: 5px;
  margin-right: 5px;
  margin-top: 5px;
}

.commentBox .left img {
  cursor: pointer;
}

.commentBox .right {
  float: right;
  margin: 5px 2px 0 0;
  width: 92%;
  height: 100%;
}

textarea::-webkit-input-placeholder {
  color: #909399;
}

.commentBox .right textarea {
  color: #606266;
  padding: 10px 5px 5px 10px;
  resize: none;
  width: 98%;
  height: 100%;
}

.bottom {
  position: relative;
  width: 98%;
  height: 60px;
  line-height: 40px;
  margin-top: 30px;
}

.bottom .p2 {
  float: right;
  margin-top: 5px;
  margin-right: 10px;
}

.emoji-panel-btn img {
  height: 35px;
  width: 35px;
}

.emoji-panel-btn:hover {
  cursor: pointer;
  opacity: 0.8;
}

.emoji-item-common {
  background: url("../../../assets/img/emoji_sprite.png");
  display: inline-block;
}

.emoji-item-common:hover {
  cursor: pointer;
}

.emoji-size-small {
  zoom: 0.3;
}

@media only screen and (min-width: 300px) and (max-width: 767px) {
  .commentBox .right {
    display: inline-block;
    margin: 5px 2px 0 0;
    width: 99%;
    height: 100%;
  }

  .emoji-panel-wrap {
    box-sizing: border-box;
    border: 1px solid #cccccc;
    border-radius: 5px;
    background-color: #ffffff;
    width: 300px;
    height: 220px;
    position: absolute;
    z-index: 99;
    top: 10px;
  }

  .el-popover {
    height: 220px;
    width: 300px;
  }
}
</style>
