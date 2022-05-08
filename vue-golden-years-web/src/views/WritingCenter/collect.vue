<template>
  <div class="main-page">
    <div class="title-box">
      <div class="title-wrapper">
        <span class="title-word">我的收藏</span>
      </div>
    </div>

    <div class="collect-list-wrapper" v-if="favorites.length" v-for="favorite in favorites">

      <el-descriptions :title="favorite.name" :colon=false :column=6>
        <el-descriptions-item v-if="favorite.open === 0">私密</el-descriptions-item>
        <el-descriptions-item v-if="favorite.open === 1">公开</el-descriptions-item>
        <el-descriptions-item>{{ favorite.createTime }} 创建</el-descriptions-item>
        <el-descriptions-item>{{ favorite.updateTime }} 更新</el-descriptions-item>
        <el-descriptions-item>{{ favorite.favoritesCollectCount }} 条内容</el-descriptions-item>
        <el-descriptions-item>
          <div class="operate" @click="editFavorites(favorite)"><i class="el-icon-edit-outline">编辑</i></div>
        </el-descriptions-item>
        <el-descriptions-item>
          <div class="operate" @click="deleteFavorites(favorite.id)"><i class="el-icon-delete"> 删除</i></div>
        </el-descriptions-item>

      </el-descriptions>

      <el-dialog
          title="删除收藏夹" :center=true
          :visible.sync="dialogDeleteFavorites"
          width="30%"
          :before-close="closeDeleteFavoritesDialog">
        <div style="text-align: center">
          <span>你确认要删除这个收藏夹吗?</span>
          <br>
          <span>删除收藏夹后,里面收藏的内容也一并删除</span>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-row>
          <el-col :span="12">
            <el-button @click="dialogDeleteFavorites = false" style="width: 90%">取 消</el-button>
          </el-col>
          <el-col :span="12">
            <el-button type="primary" @click="confirmDeleteFavorites" style="width: 90%">确 定</el-button>
          </el-col>
        </el-row>
        </span>
      </el-dialog>

      <!--编辑收藏夹-->
      <el-dialog title="编辑收藏夹" :visible.sync="dialogEditFavorites"
                 :center=true
                 width="30%"
                 :before-close="closeEditFavorites">
        <el-form :model="form" :rules="rules" ref="form">
          <el-form-item prop="name">
            <el-input v-model="form.name" @input="isChange = true" placeholder="收藏夹名称"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.description" @input="isChange = true" placeholder="收藏描述(可选)"
                      :autosize="{ minRows: 3, maxRows: 10}"
                      type="textarea"></el-input>
          </el-form-item>
          <el-form-item>
            <el-radio v-model="form.open" :label=1>公开</el-radio>
            <el-radio v-model="form.open" :label=0>私密</el-radio>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-row>
            <el-col :span="12">
              <el-button @click="cancelEditFavorites" style="width: 90%">取消</el-button>
            </el-col>
            <el-col :span="12">
              <el-button type="primary" @click="submitForm" style="width: 90%" :disabled="!form.name">确认</el-button>
            </el-col>
          </el-row>


        </div>
      </el-dialog>
    </div>
    <el-empty description="你暂时还没有收藏任何文章" v-else style=" background-color: white;"></el-empty>
  </div>

</template>

<script>
import {alterFavorites, deleteFavorites, getFavorites} from "@/api/center";
import {mapGetters} from "vuex";

export default {
  data() {
    return {
      favorites: [],
      userId: "",
      isChange: false,
      dialogDeleteFavorites: false,
      dialogEditFavorites: false,
      pendingDeleteFavorites: "",
      form: {
        id: "",
        name: "",
        description: "",
        open: 0
      },
      rules: {
        name: [
          {required: true, message: '收藏夹名称不能为空', trigger: 'blur'},
          {min: 1, max: 10, message: '收藏夹名称长度在1到10个字符'},
        ]
      }
    }
  },
  methods: {
    getFavorites() {
      let params = new URLSearchParams();
      params.append("userId", this.userId);
      getFavorites(params).then(response => {
        this.favorites = response.data;
      })
    },
    editFavorites(favorites) {
      this.dialogEditFavorites = true;
      this.form.id = favorites.id;
      this.form.name = favorites.name;
      this.form.open = favorites.open;
      this.form.description = favorites.description;

    },
    clearForm() {
      this.form = {
        id: "",
        name: "",
        description: "",
        open: 0
      }
    },
    deleteFavorites(favoriteId) {
      this.dialogDeleteFavorites = true;
      this.pendingDeleteFavorites = favoriteId;
    },
    confirmDeleteFavorites() {
      let params = {};
      params.favoritesId = this.pendingDeleteFavorites;
      params.userId = this.userId;
      deleteFavorites(params).then((response) => {
        this.$message.success(response.msg);
        this.dialogDeleteFavorites = false;
        this.getFavorites();
      })
    },
    closeDeleteFavoritesDialog() {
      this.dialogDeleteFavorites = false;
    },
    closeEditFavorites() {
      this.dialogEditFavorites = false;
    },
    cancelEditFavorites() {
      this.dialogEditFavorites = false;
    },
    submitForm() {
      alterFavorites(this.form).then((response) => {
        this.$message.success(response.msg);
        this.dialogEditFavorites = false;
        this.getFavorites();
      })
    }
  },
  computed: {
    ...mapGetters(['getUserInfo']),
  },
  created() {
    this.userId = this.getUserInfo.id;
    this.getFavorites();
  }

}
</script>

<style scoped>
.collect-list-wrapper {
  background-color: white;
  margin-top: 20px;
}

.el-descriptions {
  margin-left: 30px;
  padding-top: 10px;
  font-weight: bold;
  color: black;
  font-size: 16px;
  cursor: pointer;
}

.el-descriptions-item {
  float: left;
  font-size: 16px;
  padding: 0 0 0 10px;
  color: #748594;
  line-height: 1.5;
  display: inline-block;
}

.operate {
  cursor: pointer;
}

</style>