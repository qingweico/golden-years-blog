<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-input
          @keyup.enter.native="handleFind"
          class="filter-item"
          clearable
          placeholder="请输入管理员名"
          style="width: 200px;"
          v-model="queryParams.name"
      ></el-input>

      <el-button @click="handleFind" class="filter-item" icon="el-icon-search" type="primary">查找
      </el-button>
      <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit">添加管理员</el-button>
      <el-button icon="el-icon-refresh"  @click="refresh">重置</el-button>
    </div>

    <el-table :data="tableData"
              style="width: 100%">
      <el-table-column type="selection"></el-table-column>
      <el-table-column align="center" label="序号" width="60">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="头像" width="120">
        <template slot-scope="scope">
          <img
              :src="scope.row.avatar ? scope.row.avatar:defaultAvatar" onerror="onerror=null;src=defaultAvatar"
              style="width: 100px;height: 100px;"
              alt="">
        </template>
      </el-table-column>

      <el-table-column align="center" label="昵称" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.username }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="手机号" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.mobile }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="邮箱" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.email }}</span>
        </template>
      </el-table-column>
      <el-table-column label="性别" width="100">
        <template slot-scope="scope">
          <template v-if="scope.row.gender === 1">
            <span>男</span>
          </template>
          <template v-if="scope.row.gender === 0">
            <span>女</span>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="160" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>

      <el-table-column fixed="right" label="操作" align="center" min-width="250">
        <template slot-scope="scope">
          <el-button @click="handleEdit(scope.row)" size="small" type="primary">编辑
          </el-button>
          <el-button @click="handleUpdatePassword(scope.row)" size="small" type="primary">重置密码
          </el-button>
          <el-button @click="handleDelete(scope.row)" size="small" type="danger">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <div class="block">
      <el-pagination
          :current-page.sync="currentPage"
          :page-size="pageSize"
          :total="records"
          @current-change="handleCurrentChange"
          layout="total, prev, pager, next, jumper"
      ></el-pagination>
    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item :label-width="formLabelWidth" label="用户头像">
          <div class="imgBody" v-if="form.photoUrl">
            <i @click="deletePhoto()" @mouseover="icon = true" class="el-icon-error inputClass" v-show="icon"></i>
            <img @mouseout="icon = false" @mouseover="icon = true" v-bind:src="form.photoUrl" alt=""/>
          </div>

          <div @click="checkPhoto" class="uploadImgBody" v-else>
            <i class="el-icon-plus avatar-uploader-icon"></i>
          </div>
        </el-form-item>

        <el-row :gutter="24">
          <el-col :span="9">
            <el-form-item :label-width="formLabelWidth" label="登录名" prop="userName">
              <el-input v-model="form.username"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="9">
            <el-form-item :label-width="formLabelWidth" label="密码" prop="qqNumber">
              <el-input auto-complete="off" v-model="form.password"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="9">
            <el-form-item :label-width="formLabelWidth" label="确认密码" prop="qqNumber">
              <el-input auto-complete="off" v-model="form.confirmPassword"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="9">
            <el-form-item :label-width="formLabelWidth" label="邮箱" prop="email">
              <el-input auto-complete="off" v-model="form.email"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="9">
            <el-form-item :label-width="formLabelWidth" label="手机号" prop="email">
              <el-input auto-complete="off" v-model="form.mobile"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
        <el-col :span="18">
          <el-form-item label="性别" :label-width="formLabelWidth" prop="gender">
            <el-radio border size="medium" v-model="form.gender" label="1">男</el-radio>
            <el-radio v-model="form.gender" label="0" border>女</el-radio>
          </el-form-item>
        </el-col>
        </el-row>
        <el-form-item label="启用人脸登录" :label-width="formLabelWidth">
        <el-switch
            v-model="form.isFaceLogin"
            active-color="#13ce66"
            inactive-color="#ff4949">
        </el-switch>
        </el-form-item>

      </el-form>
      <div class="dialog-footer" slot="footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button @click="submitForm" type="primary">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getAdminList} from "@/api/admin";

export default {
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      records: 0,
      totalPage: 0,
      isFaceLogin: false,
      title: "增加管理员",
      tableData: [],
      //控制弹出框
      dialogFormVisible: false,
      formLabelWidth: "120px",
      isEditForm: false,
      // 搜索条件
      queryParams: {
        name: ""

      },
      form: {},
      defaultAvatar: this.$SysConf.defaultAvatar, // 默认头像
      rules: {
        userName: [
          {required: true, message: '用户名不能为空', trigger: 'blur'},
          {min: 5, max: 30, message: '长度在5到30个字符'},
        ],
        nickName: [
          {required: true, message: '昵称不能为空', trigger: 'blur'},
          {min: 1, max: 30, message: '长度在1到30个字符'},
        ],
        gender: [
          {required: true, message: '性别不能为空', trigger: 'blur'},
        ],
        email: [
          {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
        ],
        qqNumber: [
          {pattern: /[1-9]([0-9]{5,11})/, message: '请输入正确的QQ号码'}
        ]
      }
    };
  },
  components: {

  },
  created() {
    this.adminList();
  },
  methods: {
    adminList() {
      let params = new URLSearchParams();
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getAdminList(params).then(response => {
        console.log(response);
        let content = response.data;
        this.tableData = content.rows;
        this.currentPage = content.currentPage;
        this.records = content.records;
        this.totalPage = content.totalPage;
      });
    },

    cropSuccess(resData) {
      this.imagecropperShow = false
      this.imagecropperKey = this.imagecropperKey + 1
      this.form.photoUrl = resData[0].url;
      this.form.avatar = resData[0].uid
    },
    close() {
      this.imagecropperShow = false
    },
    getFormObject () {
      return {
        username: null,
        password: null,
        confirmPassword: null,
        isFaceLogin: false
      };
    },
    deletePhoto: function () {
      console.log("删除图片", this.form)
      this.form.photoUrl = null;
      this.form.avatar = "";
      this.icon = false;
    },
    //弹出选择图片框
    checkPhoto() {
      this.imagecropperShow = true
    },
    refresh() {
      this.queryParams = {
        name: ""
      }
      this.adminList();
    },
    handleFind () {
      this.currentPage = 1
      this.userList();
    },
    handleAdd () {
      this.dialogFormVisible = true;
      this.form = this.getFormObject();
      this.isEditForm = false;
    },
    handleEdit (row) {
      this.title = "编辑用户";
      this.dialogFormVisible = true;
      this.isEditForm = true;
      this.form = row;
    },
    handleDelete (row) {
      this.$confirm("此操作将把用户删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {

      })
    },
    handleUpdatePassword (row) {
      this.$confirm("此操作将把用户密码重置为初始密码?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
          .then(() => {
            let params = {};
            params.uid = row.uid;

          })
          .catch(() => {
            this.$commonUtil.message.info("已取消重置")
          });
    },
    handleCurrentChange (val) {
      this.currentPage = val;
      this.userList();
    },
    submitForm () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.isEditForm) {

          } else {

          }
        }
      })
    }
  }
};
</script>
<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  margin: 0 0 0 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.imgBody {
  width: 100px;
  height: 100px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}

.uploadImgBody {
  margin-left: 5px;
  width: 100px;
  height: 100px;
  border: dashed 1px #c0c0c0;
  float: left;
  position: relative;
}

.uploadImgBody :hover {
  border: dashed 1px #00ccff;
}

.inputClass {
  position: absolute;
}

.img {
  width: 100%;
  height: 100%;
}

img {
  width: 100px;
  height: 100px;
}
</style>
