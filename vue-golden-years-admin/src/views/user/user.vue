<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-input
          @keyup.enter.native="handleFind"
          class="filter-item"
          clearable
          placeholder="请输入用户名"
          style="width: 200px;"
          v-model="queryParams.keyword"
      ></el-input>
      <el-select clearable placeholder="账号状态" style="width:140px" v-model="queryParams.status">
      </el-select>
      <el-button @click="handleFind" class="filter-item" icon="el-icon-search" type="primary">查找
      </el-button>
      <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit">添加用户</el-button>
      <el-button icon="el-icon-refresh" @click="refresh">重置</el-button>
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
              :src="scope.row.face ? scope.row.face:defaultAvatar" onerror="onerror=null;src=defaultAvatar"
              style="width: 100px;height: 100px;"
              alt="">
        </template>
      </el-table-column>

      <el-table-column align="center" label="昵称" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.nickname }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="手机号" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.mobile }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="邮箱" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.email }}</span>
        </template>
      </el-table-column>
      <el-table-column label="性别" width="100">
        <template slot-scope="scope">
          <template v-if="scope.row.sex === 2">
            <span>保密</span>
          </template>
          <template v-if="scope.row.sex === 1">
            <span>男</span>
          </template>
          <template v-if="scope.row.sex === 0">
            <span>女</span>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <template v-if="scope.row.activeStatus === 1">
            <el-tag type="success">正常</el-tag>
          </template>
          <template v-if="scope.row.activeStatus === 2">
            <el-tag type="danger">冻结</el-tag>
          </template>
          <template v-if="scope.row.activeStatus === 0">
            <el-tag type="info">未激活</el-tag>
          </template>
        </template>
      </el-table-column>

      <el-table-column label="注册日期" width="160" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ scope.row.createdTime }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" align="center" label="操作" min-width="250">
        <template slot-scope="scope">
          <el-button @click="handleEdit(scope.row)" size="small" type="primary">编辑
          </el-button>
          <el-button @click="resetPassword(scope.row)" size="small" type="primary">重置密码
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
          <div class="imgBody" v-if="form.face">
            <i @click="deletePhoto()" @mouseover="icon = true" class="el-icon-error inputClass" v-show="icon"></i>
            <img @mouseout="icon = false" @mouseover="icon = true" v-bind:src="form.face" alt=""/>
          </div>
          <div @click="checkPhoto" class="uploadImgBody" v-else>
            <i class="el-icon-plus avatar-uploader-icon"></i>
          </div>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="昵称" prop="nickname">
          <el-input v-model="form.nickname"></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="手机号" prop="mobile">
          <el-input v-model="form.mobile"></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="邮箱" prop="email">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="性别" :label-width="formLabelWidth">
          <el-radio-group v-model="form.sex">
            <el-radio border size="medium" :label=1> 男
            </el-radio>
            <el-radio border size="medium" :label=0> 女
            </el-radio>
            <el-radio border size="medium" :label=2> 保密
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <el-form-item :label-width="formLabelWidth" label="用户状态" prop="status">
        <el-switch
            v-model="form.status"
            active-color="#13ce66"
            inactive-color="#ff4949">
        </el-switch>
      </el-form-item>
      <el-form-item :label-width="formLabelWidth" label="用户状态" prop="status">
        <el-date-picker
            v-model="form.birthday"
            type="date"
            placeholder="请选择生日">
        </el-date-picker>
      </el-form-item>
      <div class="dialog-footer" slot="footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button @click="submitForm" type="primary">确 定</el-button>
      </div>
    </el-dialog>

    <avatar-cropper
        :height="300"
        :key="imageCropperKey"
        :url="url"
        :width="300"
        @close="close"
        @crop-upload-success="cropSuccess"
        lang-type="zh"
        v-show="imageCropperShow">
    </avatar-cropper>
  </div>
</template>

<script>
import {deleteUser, getUserList, resetPassword} from "@/api/user";
import {saveOrUpdate} from "@/api/user";
import AvatarCropper from '@/components/AvatarCropper'

export default {
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      records: 0,
      totalPage: 0,
      title: "增加用户",
      tableData: [],
      //控制弹出框
      icon: false,
      url: process.env.PIC_API + "/fs/uploadFace",
      imageCropperKey: 0,
      imageCropperShow: false,
      dialogFormVisible: false,
      formLabelWidth: "120px",
      isEditForm: false,
      // 搜索条件
      queryParams: {
        keyword: "",
        status: "",
        startDateStr: "",
        endDateStr: "",
      },
      form: {
        id: null,
        nickname: null,
        face: null,
        mobile: null,
        email: null,
        sex: null,
        status: false,
        birthday: null,
      },
      defaultAvatar: this.$SysConf.defaultAvatar, // 默认头像
      rules: {
        nickname: [
          {required: true, message: '用户昵称不能为空', trigger: 'blur'},
          {min: 1, max: 20, message: '长度在1到20个字符'},
        ],
        mobile: [
          {
            required: true, message: '手机号不能为空', trigger: 'blur',
            pattern: /^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/
          },
        ],
        email: [
          {pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/, message: '请输入正确的邮箱'},
        ]
      }
    };
  },
  components: {
    AvatarCropper
  },
  created() {
    this.userList();
  },
  methods: {
    refresh() {
      this.queryParams = {
        keyword: "",
        status: "",
        startDateStr: "",
        endDateStr: "",
      };
      this.userList();
    },
    clearData() {
      this.form = {
        nickname: null,
        face: null,
        mobile: null,
        email: null,
        sex: null,
      }
    },
    userList() {
      let params = new URLSearchParams();
      let queryParams = this.queryParams;
      params.append("nickname", queryParams.keyword);
      params.append("state", queryParams.state);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      params.append("startDateStr", queryParams.startDateStr);
      params.append("endDateStr", queryParams.endDateStr);
      getUserList(params).then(response => {
        let content = response.data;
        this.tableData = content.rows;
        this.currentPage = content.currentPage;
        this.records = content.records;
        this.totalPage = content.totalPage;
      });
    },

    cropSuccess(resData) {
      this.imageCropperShow = false
      this.imageCropperKey = this.imageCropperKey + 1
      this.form.face = resData;
      this.$notify({
        title: "成功",
        message: '上传成功',
        type: "success"
      });
    },
    close() {
      this.imageCropperShow = false
    },
    deletePhoto() {
      this.form.face = "";
      this.icon = false;
    },
    // 弹出选择图片框
    checkPhoto() {
      this.imageCropperShow = true
    },
    handleFind() {
      this.currentPage = 1
      this.userList();
    },
    handleAdd() {
      this.dialogFormVisible = true;
      this.form = this.getFormObject();
      this.isEditForm = false;
    },
    handleEdit(row) {
      this.title = "编辑用户";
      this.dialogFormVisible = true;
      this.isEditForm = true;
      this.form.sex = row.sex;
      this.form.face = row.face;
      this.form.nickname = row.nickname;
      this.form.mobile = row.mobile;
      this.form.email = row.email;
    },
    handleDelete(row) {
      this.$confirm("此操作将把用户删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        let params = {};
        params.uid = row.uid;
        deleteUser(params).then(response => {
          this.$message.success(response.msg)
          this.userList();
        });
      }).catch(() => {
        this.$message.info("已取消删除")
      });
    },
    resetPassword(row) {
      this.$confirm("此操作将把用户密码重置为初始密码?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        resetPassword(row.id).then(response => {
          this.$message.success(response.msg)
        });
      }).catch(() => {
        this.$message.info("已取消重置")
      });
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.userList();
    },
    clearStatus() {
      this.dialogFormVisible = false;
      this.clearData();
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.isEditForm) {
            saveOrUpdate(this.form).then(response => {
              if (response.data.success) {
                this.$notify({
                  title: "成功",
                  message: response.msg,
                  type: "success"
                });
                this.clearStatus();
                this.userList();
              } else {
                this.$notify.error({
                  title: "失败",
                  message: response.msg
                });
              }
            });
          } else {
            saveOrUpdate(this.form).then(response => {
              if (response.data.success) {
                this.$notify({
                  title: "成功",
                  message: response.msg,
                  type: "success"
                });
                this.clearStatus();
                this.userList();
              } else {
                this.$notify.error({
                  title: "失败",
                  message: response.msg
                });
              }
            });
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
