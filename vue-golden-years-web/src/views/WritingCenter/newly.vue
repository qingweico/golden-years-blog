<template>
  <div id="editor" class="editor-container">

    <div class="article-title-wrapper">
      <input class="article-title" placeholder="请输入文字标题(6-30长度)" v-model="articleTitle"
             maxlength="30"/>
    </div>

    <div class="other-info">
      <div class="cover-wrapper">
        <div class="cover">文章类别</div>
        <div class="choose-type">

          <label>
            <select v-model="articleCategory">
              <option :value="category.id" v-for="(category, index) in categoryList" v-key="index">{{
                  category.name
                }}
              </option>
            </select>
          </label>

        </div>
      </div>
      <div class="cover-wrapper">
        <div class="cover">文章封面</div>
        <div class="choose-type">
          <div><label>
            <input type="radio" name="articleType" v-model="articleType" value="1" checked/>
          </label><span
              class="choose-words">单封面</span></div>
          <div style="margin-left: 30px;"><label>
            <input type="radio" v-model="articleType" value="2"
                   name="articleType"/>
          </label><span class="choose-words">无封面</span>
          </div>
        </div>
      </div>
      <div class="cover-wrapper" v-show="articleType === '1'">
        <div class="cover"></div>
        <div class="choose-cover">
          <div class="uploader-comp">
            <div id="block-choose" class="block-choose" :style="coverStyle">
              <img src="@/assets/icon-go-upload.png" style="width: 20px; height: 20px; align-self: center;"
                   v-show="articleCover === '' || articleCover === null" alt=""/>
            </div>
            <input type="file" @change="uploadCover" @mouseover="mouseOver" @mouseout="mouseOut"
                   id="inputPic" class="inputPic" accept="image/jpeg,image/jpg,image/png">
          </div>
          <div style="margin-top: 10px; color: #9b9d9e;">请上传JPG、JPEG、PNG格式的封面图</div>
        </div>
      </div>

<!--      <el-form>-->
<!--      <el-form-item label="内容" :label-width="'120'" prop="content">-->
<!--        <MarkdownEditor :content="articleContent" ref="editor" :height="465"></MarkdownEditor>-->
<!--      </el-form-item>-->
<!--      </el-form>-->
    </div>

    <div class="publish-bottom">
      <div class="buttons">
        <button class="white-btn" type="button" @click="goBack">返回</button>
        <button class="white-btn" type="button" @click="preview">预览</button>
        <button class="white-btn" type="button" @click="doTiming">{{ appointWords }}</button>
        <el-date-picker
            v-model="timingDate"
            type="datetime"
            id="choose-date"
            class="timing-date-picker"
            v-show="isAppoint ===  1"
            placeholder="定时日期">
        </el-date-picker>
        <button class="red-btn" type="button" @click="publish">发布文章</button>
      </div>
    </div>
  </div>
</template>

<script>
import MarkdownEditor from "@/components/MarkdownEditor";
import {uploadBlogCover} from "@/api/blog";
import $ from 'jquery';

export default {
  name: "newly",
  components: {
    MarkdownEditor,
  },
  data() {
    return {
      articleTitle: "",
      articleCategory: "",
      articleType: '1',
      articleCover: "",
      isAppoint: 0,
      appointWords: "定时发布",
      showUploadCover: true,
      coverStyle: "",
      articleContent: "",
      categoryList: [],
      timingDate: ""
    }
  },
  methods: {
    goBack() {
      window.history.go(-1);
    },
    preview() {
    },
    doTiming() {
      const me = this;
      if (me.isAppoint === 1) {
        me.isAppoint = 0;
        me.appointWords = "定时发布";
        me.timingDate = "";
        $("#choose-date").val("");
      } else if (me.isAppoint === 0) {
        me.isAppoint = 1;
        me.appointWords = "取消定时";
      }
    },
    publish() {
    },
    // 上传封面
    uploadCover() {
      const that = this;

      let f = document.getElementById('inputPic').files[0];
      //创建一个form对象
      let multiForm = new FormData();
      //append 向form表单添加数据
      multiForm.append('files', f, f.name);

      let params = new URLSearchParams();
      let userId = "";
      params.append("userId", userId);
      uploadBlogCover(params).then(response => {
        if (response.data.success) {
          let imagesList = response.data.data;
          if (imagesList.length < 1) {
            this.$notify.error({
              title: '错误',
              message: '图片上传失败,请保证图片不能为空,并且符合 jpg/png/jpeg 的后缀格式!'
            });
          } else {
            that.articleCover = app.fsHost + imagesList[0];
            that.coverStyle = "background-image: url(" + that.articleCover + ");";
            that.coverStyle += "background-repeat: no-repeat;";
            that.coverStyle += "background-position: center center;";
            that.coverStyle += "background-size: cover;";
          }
        } else {
          this.$notify.info({
            title: '消息',
            message: response.data.msg
          });
        }
      });
    },
    // 鼠标移动到上传组件上发生css变化
    mouseOver() {
      $("#block-choose").css({"background-color": "#f0efef", "border": "1px dashed #a7a7a7"});
    },
    // 鼠标离开上传组件上发生css变化
    mouseOut() {
      $("#block-choose").css({"background-color": "#f7f7f7", "border": "1px dashed #d8d8d8"});
    },
  }
}
</script>

<style scoped>


.editor-container {
  margin: 0 30px;
  width: 888px;
  background-color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.06);
}

.article-title-wrapper {
  padding: 10px 0;
  align-self: center;
}

.article-title {
  height: 50px;
  width: 800px;
  outline: none;
  border: 0;
  min-height: 35px;
  font-weight: 688;
  font-size: 24px;
  line-height: 35px;
  color: #222222;
}

.article-title::placeholder {
  color: #b5b3b3;
}

.other-info {
  width: 800px;
  align-self: center;
  padding: 10px 0;
}

.cover-wrapper {
  margin-bottom: 20px;
  display: flex;
  flex-direction: row;
}

.cover {
  width: 120px;
}

.choose-type {
  display: flex;
  flex-direction: row;
}

.choose-words {
  margin-left: 10px;
}

.uploader-comp {
  display: flex;
  flex-direction: row;
}
.block-choose {
  width: 158px;
  height: 108px;
  background-color: #f7f7f7;
  border-radius: 4px;
  border: 1px dashed #d8d8d8;
  display: flex;
  flex-direction: row;
  justify-content: center;
}
.inputPic {
  cursor: pointer;
  opacity: 0;
  width: 158px;
  height: 108px;
  position: relative;
  left: -158px;
  z-index: 9;
}

.block-choose:hover {
  width: 158px;
  height: 108px;
  background-color: #f0efef;
  border-radius: 4px;
  border: 1px dashed #a7a7a7;
  display: flex;
  flex-direction: row;
  justify-content: center;
  cursor: pointer;
}

/* 底部固定 */
.publish-bottom {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 66px;
  line-height: 40px;
  z-index: 88;
  background-color: white;
  border-top: 1px solid #dfdede;
  display: flex;
  flex-direction: row;
  justify-content: center;
}

.buttons {
  width: 888px;
  align-self: center;
}

.white-btn {
  background-color: white;
  color: #222222;
  outline: none;
  border: 1px solid rgb(201, 201, 201);
  height: 40px;
  margin-right: 10px;
  padding: 0 20px;
  border-radius: 5px;
}

.white-btn:hover {
  background-color: rgb(240, 239, 239);
  border: 1px solid rgb(180, 179, 179);
}

.timing-date-picker {
  height: 40px;
  padding-left: 10px;
  padding-right: 10px;
  width: 160px;
  margin-right: 10px;
}

.red-btn {
  background-color: #c9394a;
  color: white;
  outline: none;
  border: 1px solid #c9394a;
  height: 40px;
  margin-right: 10px;
  padding: 0 20px;
  border-radius: 5px;
}

.red-btn:hover {
  background-color: #dd4456;
  border: 1px solid #dd4456;
}
</style>