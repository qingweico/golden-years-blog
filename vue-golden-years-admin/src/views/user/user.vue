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
      <el-input
          @keyup.enter.native="handleFind"
          class="filter-item"
          clearable
          placeholder="请输入手机号"
          style="width: 200px;"
          v-model="queryParams.mobile"
      ></el-input>
      <el-select clearable placeholder="账号状态" style="width:140px" v-model="queryParams.status">
        <el-option value="0" label="未激活"></el-option>
        <el-option value="1" label="正常"></el-option>
        <el-option value="2" label="已冻结"></el-option>
      </el-select>
      <el-date-picker
          v-model="dateRange"
          @change="dateChoice"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="截止日期"
      ></el-date-picker>

      <el-button @click="handleFind" class="filter-item" icon="el-icon-search" type="primary">查找
      </el-button>
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
          <el-button @click="look(scope.row)" size="small" type="primary">查看
          </el-button>
          <el-button @click="resetPassword(scope.row)" size="small" type="primary">重置密码
          </el-button>
          <el-button @click="inactiveOrNot(scope.row)" size="small" type="danger"
                     v-if="scope.row.activeStatus === 1">冻结
          </el-button>
          <el-button @click="inactiveOrNot(scope.row)"
                     v-if="scope.row.activeStatus === 2" size="small">解冻
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
    <el-dialog title="查看用户" :visible.sync="dialogFormVisible">
      <el-form :model="form" ref="form">
        <el-form-item :label-width="formLabelWidth" label="用户头像">
          <div class="imgBody" v-if="form.face">
            <img v-bind:src="form.face" alt=""/>
          </div>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="真实姓名">
          <el-input v-model="form.realName" readonly></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="昵称">
          <el-input v-model="form.nickname" readonly></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="手机号">
          <el-input v-model="form.mobile" readonly></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="邮箱">
          <el-input v-model="form.email" readonly></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="生日">
          <el-input v-model="form.birthday" readonly></el-input>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="省份">
          <el-input v-model="form.province" readonly></el-input>
        </el-form-item>
        <el-form-item label="性别" :label-width="formLabelWidth">
          <span v-if="form.sex === 1">男</span>
          <span v-if="form.sex === 0">女</span>
          <span v-if="form.sex === 2">保密</span>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {getUserList, resetPassword} from "@/api/user";
import {freezeOrNot} from "../../api/user";

export default {
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      records: 0,
      totalPage: 0,
      tableData: [],
      dialogFormVisible: false,
      formLabelWidth: "120px",
      isEditForm: false,
      dateRange: "",
      // 搜索条件
      queryParams: {
        keyword: "",
        status: "",
        mobile: "",
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
    }
  },
  created() {
    this.userList();
  },
  methods: {
    refresh() {
      this.queryParams = {
        keyword: "",
        status: "",
        mobile: "",
        startDateStr: "",
        endDateStr: "",
      };
      this.dateRange = "";
      this.userList();
    },
    dateChoice() {
      let dateArray = String(this.dateRange).split(",");
      this.queryParams.startDateStr = dateArray[0];
      this.queryParams.endDateStr = dateArray[1];
    },
    userList() {
      let params = new URLSearchParams();
      let queryParams = this.queryParams;
      params.append("nickname", queryParams.keyword.trim());
      params.append("status", queryParams.status);
      params.append("mobile", queryParams.mobile.trim());
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      params.append("startDate", queryParams.startDateStr);
      params.append("endDate", queryParams.endDateStr);
      getUserList(params).then(response => {
        let content = response.data;
        this.tableData = content.rows;
        this.currentPage = content.currentPage;
        this.records = content.records;
        this.totalPage = content.totalPage;
      });
    },
    handleFind() {
      this.currentPage = 1
      this.userList();
    },
    look(row) {
      this.dialogFormVisible = true;
      this.isEditForm = true;
      this.form.face = row.face;
      this.form.nickname = row.nickname;
      this.form.mobile = row.mobile;
      this.form.sex = row.sex;
      this.form.email = row.email;
      this.form.birthday = row.birthday;
      this.form.province = row.province;
      this.form.realName = row.realName;
    },
    inactiveOrNot(row) {
      let params = new URLSearchParams();
      params.append("userId", row.id)
      if (row.activeStatus === 1) {
        params.append("doStatus", 2);
        this.inactive(params);
      } else {
        params.append("doStatus", 1);
        this.active(params);
      }
    },
    active(params) {
      freezeOrNot(params).then((response) => {
        this.$message.success(response.msg);
        this.userList();
      })
    },
    inactive(params) {
      freezeOrNot(params).then((response) => {
        this.$message.success(response.msg);
        this.userList();
      })
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


.imgBody {
  width: 100px;
  height: 100px;
  border: solid 2px #ffffff;
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
