<template>
  <div class="app-container">
    <textarea id="editor" rows="10" cols="80"></textarea>
  </div>
</template>

<script>
import CKEDITOR from 'CKEDITOR';

export default {
  props: ["content", "height"],
  mounted() {
    let that = this;
    // 配置ckeditor插件
    CKEDITOR.plugins.addExternal('codesnippet', '/static/ckeditor/plugins/codesnippet/', 'plugin.js');
    CKEDITOR.plugins.addExternal('panelbutton', '/static/ckeditor/plugins/panelbutton/', 'plugin.js');
    CKEDITOR.plugins.addExternal('floatpanel', '/static/ckeditor/plugins/floatpanel/', 'plugin.js');
    CKEDITOR.plugins.addExternal('colorbutton', '/static/ckeditor/plugins/colorbutton/', 'plugin.js');
    CKEDITOR.plugins.addExternal('markdown', '/static/ckeditor/plugins/markdown/', 'plugin.js');
    CKEDITOR.plugins.addExternal('colordialog', '/static/ckeditor/plugins/colordialog/', 'plugin.js');
    CKEDITOR.plugins.addExternal('dialog', '/static/ckeditor/plugins/dialog/', 'plugin.js');
    CKEDITOR.plugins.addExternal('dialogui', '/static/ckeditor/plugins/dialogui/', 'plugin.js');
    CKEDITOR.plugins.addExternal('smiley', '/static/ckeditor/plugins/smiley/', 'plugin.js');
    CKEDITOR.plugins.addExternal('widget', '/static/ckeditor/plugins/widget/', 'plugin.js');
    CKEDITOR.plugins.addExternal('lineutils', '/static/ckeditor/plugins/lineutils/', 'plugin.js');
    CKEDITOR.plugins.addExternal('eqneditor', '/static/ckeditor/plugins/eqneditor/', 'plugin.js');
    CKEDITOR.plugins.addExternal('justify', '/static/ckeditor/plugins/justify/', 'plugin.js');
    CKEDITOR.plugins.addExternal('tableresize', '/static/ckeditor/plugins/tableresize/', 'plugin.js');
    CKEDITOR.plugins.addExternal('wordcount', '/static/ckeditor/plugins/wordcount/', 'plugin.js');
    CKEDITOR.plugins.addExternal('notification', '/static/ckeditor/plugins/notification/', 'plugin.js');
    CKEDITOR.plugins.addExternal('video', '/static/ckeditor/plugins/video/', 'plugin.js');
    CKEDITOR.plugins.addExternal('fakeobjects', '/static/ckeditor/plugins/fakeobjects/', 'plugin.js');
    CKEDITOR.plugins.addExternal('liststyle', '/static/ckeditor/plugins/liststyle/', 'plugin.js');
    CKEDITOR.plugins.addExternal('pasteUploadImage', '/static/ckeditor/plugins/pasteUploadImage/', 'plugin.js');
    CKEDITOR.replace('editor',
        {
          height: this.height,
          width: '100%',
          toolbar: 'toolbar_Full',
          codeSnippet_theme: 'zenburn',
          customConfig: '/static/ckeditor/config.js',
          filebrowserImageUploadUrl: process.env.PIC_API + '/fs/uploadSomeFiles?',
          filebrowserUploadUrl: process.env.PIC_API + '/fs/uploadSomeFiles?',
          pasteUploadFileApi: process.env.PIC_API + '/fs/uploadSomeFiles?',
          extraPlugins: 'codesnippet,panelbutton,floatpanel,colorbutton,markdown,colordialog,dialog,dialogui,smiley,widget,lineutils,eqneditor,justify,tableresize,wordcount,notification,video,fakeobjects,liststyle,pasteUploadImage'
        });
    this.editor = CKEDITOR.instances.editor;
    //初始化内容
    this.editor.setData(this.content);
    // 一秒钟通知子组件, ckeditor中内容改变
    that.editor.on('change', function (event) {
      that.timeout = setTimeout(function () {
        that.fun();
      }, 1000);
    });

  },
  created() {
    this.textData = this.content;
  },
  watch: {
    content: function () {
      this.textData = this.content;
    }
  },
  data() {
    return {
      //编辑器对象
      editor: null,
      //初始化内容
      textData: this.content,
    }
  },
  methods: {
    //获取data
    getData: function () {
      return this.editor.getData();
    },
    setData: function (data) {
      return this.editor.setData(data);
    },
    initData: function () {
      try {
        this.editor.setData("");
      } catch (error) {
        console.log("CKEditor还未加载");
      }
    },
    fun: function () {
      this.$emit("contentChange", "");
    }
  }
}

</script>
