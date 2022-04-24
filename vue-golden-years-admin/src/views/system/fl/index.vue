<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-form :inline="true" label-width="68px" style="margin-bottom: 8px;">
        <el-input
            clearable
            class="filter-item"
            style="width: 250px;"
            v-model="queryParams.linkName"
            placeholder="请输入友情链接名称"
            @keyup.enter.native="handleSearch">
        </el-input>
        <el-select
            v-model="queryParams.isDelete"
            style="width: 140px"
            clearable
            placeholder="状态"
            @keyup.enter.native="handleSearch">
          <el-option value="0" label="可用"></el-option>
          <el-option value="1" label="不可用"></el-option>
        </el-select>
        <el-button style="margin-left: 10px;" class="filter-item" type="primary" icon="el-icon-search"
                   @click="handleSearch">查找
        </el-button>
        <el-button style="margin-left: 10px;" icon="el-icon-edit" class="filter-item" type="primary"
                   @click="handleAdd">添加
        </el-button>
        <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete">删除选中</el-button>
        <el-button icon="el-icon-refresh" @click="refresh">重置</el-button>
      </el-form>
    </div>
    <el-table :data="tableData"
              ref="articleTable"
              style="width: 100%"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection"></el-table-column>

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="链接名称" width="160" align="center">
        <template slot-scope="scope">
          <span @click="onClick(scope.row)" style="cursor:pointer;">{{ scope.row.linkName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="链接地址" width="200" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.linkUrl }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.isDelete === 0">可用</el-tag>
          <el-tag type="danger" v-else>不可用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.updateTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" min-width="150" align="center">
        <template slot-scope="scope">
          <el-button @click="handleEdit(scope.row)" type="primary" size="small">编辑</el-button>
          <el-button @click="handleDelete(scope.row)" type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title"
               :visible.sync="dialogFormVisible"
               :before-close="closeDialog" width="30%">
      <el-form label-width="80px" :model="form" :rules="rules" ref="form">
        <el-form-item label="链接名称" prop="linkName">
          <el-input v-model="form.linkName" @input="isChange = true"></el-input>
        </el-form-item>
        <el-form-item label="链接地址" prop="linkUrl">
          <el-input v-model="form.linkUrl" @input="isChange = true"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
              v-model="status"
              active-color="#13ce66"
              inactive-color="#ff4949">
          </el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancel">取 消</el-button>
        <el-button type="primary" @click="saveEdit">确 定</el-button>
      </div>
    </el-dialog>

    <!--分页-->
    <div class="block">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="records"></el-pagination>
    </div>
  </div>

</template>

<script>
import {deleteFl, getFlList, saveOrUpdate} from "@/api/system/fl";
import {batchDelete} from "../../../api/system/fl";

export default {
  name: "fl",
  data() {
    // 校验url
    const validateUrl = (rule, value, callback) => {
      if (!value || new RegExp("^[A-Za-z]+://[A-Za-z0-9-_]+\\.[A-Za-z0-9-_%&\?\/.=]+$").test(value)) {
        callback();
      } else {
        callback(new Error("Url不合法!"));
      }
    };

    return {
      tableData: [],
      multipleSelection: [],
      dialogFormVisible: false,
      title: "添加友情链接",
      queryParams: {
        linkName: "",
        isDelete: ""
      },
      status: false,
      form: {
        // 为空则新增,不为空则修改
        id: "",
        linkName: "",
        linkUrl: "",
        isDelete: 0
      },
      // 监听表单内容是否改变
      isChange: false,
      currentPage: 1,
      pageSize: 10,
      totalPage: 0,
      records: 0,
      rules: {
        linkName: [
          {required: true, message: '链接名称不能为空', trigger: 'blur'}
        ],
        linkUrl: [
          {required: true, message: '链接地址不能为空', trigger: 'blur'},
          {validator: validateUrl}
        ],
      }
    }
  },
  created() {
    this.getFriendLinkList();
  },
  methods: {
    getFriendLinkList() {
      let params = new URLSearchParams();
      params.append("linkName", this.queryParams.linkName);
      params.append("isDelete", this.queryParams.isDelete);
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getFlList(params).then((response) => {
        this.tableData = response.data.rows;
        this.records = response.data.records;
        this.totalPage = response.data.totalPage;
        // start 0 not 1
        this.currentPage = response.data.currentPage + 1;
      })
    },
    handleSearch() {
      this.getFriendLinkList();
    },
    handleCancel() {
      // 清除本次的校验信息
      this.$refs.form.clearValidate();
      this.dialogFormVisible = false;
      this.clearData();
    },
    // 关闭窗口
    closeDialog(done) {
      // 清除本次的校验信息
      this.$refs.form.clearValidate();
      if (this.isChange) {
        this.$confirm("是否放弃本次操作", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() => {
          this.clearStatus();
          done();
        }).catch(() => {
          /**cancel**/
        });
      } else {
        this.clearStatus();
      }
    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    clearStatus() {
      this.dialogFormVisible = false;
      this.clearData();
      this.isChange = false;
    },
    handleDeleteBatch() {
      const that = this;
      if (that.multipleSelection.length <= 0) {
        that.$message.error("请先选中需要删除的内容")
        return;
      }
      this.$confirm("此操作将把选中的内容删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        let ids = [];
        for (let item of that.multipleSelection) {
          ids.push(item.id);
        }
        batchDelete(ids).then((response) => {
          this.$message.success(response.msg);
          that.multipleSelection = [];
          this.getFriendLinkList();
        })
      })
    },
    handleEdit(row) {
      this.dialogFormVisible = true;
      if (row.id !== "") {
        this.title = "编辑友情链接";
      }
      this.form.id = row.id;
      this.form.linkName = row.linkName;
      this.form.linkUrl = row.linkUrl;
    },
    saveEdit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.form.isDelete = Number(!this.status);
          saveOrUpdate(this.form).then(response => {
            this.clearStatus();
            // 添加完毕以后重新查询友情链接列表
            this.getFriendLinkList();
            this.$message.success(response.msg);
          });
        }
      });
    },
    handleAdd() {
      this.dialogFormVisible = true;
      this.title = "添加友情链接";
    },
    handleDelete(row) {
      this.$confirm("此操作将把该友情链接删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteFl(row.id).then((response) => {
          this.getFriendLinkList();
          this.$message.success(response.msg);
        })
      })
    },
    refresh() {
      this.queryParams = {
        linkName: "",
        isDelete: ""
      }
      this.getFriendLinkList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getFriendLinkList();
    },
    clearData() {
      this.form = {
        id: "",
        linkName: "",
        linkUrl: "",
        isDelete: 0
      }
    }
  }
}
</script>

<style scoped>

</style>