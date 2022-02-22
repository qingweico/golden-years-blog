<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">评论管理</span>
      </div>
    </div>

    <div class="comment-list-wrapper">

      <div class="single-comment" v-for="(comment, index) in commentList" :key="index">
        <div class="comment-wrapper">
          <img :src="comment.commentUserFace" style="width: 40px; height: 40px; border-radius: 50%;" alt=""/>
          <div class="basic-wrapper">
            <div class="user-time">
              <div>{{comment.commentUserNickname}}</div>
<!--              <div class="publish-time">{{formatData(comment.createTime)}}</div>-->
            </div>
            <div class="comment-content" v-html="comment.content">
            </div>
            <div class="operation-wrapper" @click="deleteComment(comment.id)">
              <span class="delete-span" style="">删除</span>
            </div>
          </div>
        </div>

        <div class="article-basic-info" v-show="comment.articleCover != null">
          <img class="cover" :style="'background-image: url(' + comment.articleCover + ')'" alt="" src=""/>
          <div class="every-title">{{comment.articleTitle}}</div>
        </div>
      </div>

    </div>

  </div>
</template>

<script>
export default {
  name: "comment",
  data() {
    return {
      commentList: []
    }
  },
  methods: {
    deleteComment() {}
  }
}
</script>

<style scoped>
.main-page {
  width: 980px;
  margin-left: 20px;
  background-color: white;
}

.title-box {
  padding: 20px 0 10px 30px;
  border-bottom: 1px solid #e8e8e8;
}

.title-word {
  color: #c9394a;
  font-size: 20px;
}

.comment-list-wrapper {
  margin-top: 20px;
  margin-left: 30px;
}

.single-comment {
  margin-top: 10px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.comment-wrapper {
  margin-top: 20px;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;

  width: 600px;
  font-size: 16px;
}

.basic-wrapper {
  margin-left: 20px;
}
.user-time {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
}

.publish-time {
  color: gray;
  font-size: 14px;
  line-height: 23px;
  margin-left: 10px;
}

.comment-content {
  margin-top: 10px;
  font-size: 13px;
  flood-color: #484747;
}

.operation-wrapper {
  margin-top: 10px;
  display: flex;
  flex-direction: row;
}

.article-basic-info {
  margin-right: 30px;
}
.article-basic-info-null {
  margin-right: 30px;
  margin-bottom: 30px;
}

.cover {
  width: 175px;
  height: 110px;
  overflow: hidden;
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;
}

.every-title {
  width: 170px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  position: relative;
  top: -40px;
  margin-left: 5px;
  color: white;
  font-size: 12px;
}

.delete-span {
  color: gray;font-size: 14px;
  cursor: pointer;
}
.delete-span:hover {
  color: rgb(205, 202, 202);
}

</style>