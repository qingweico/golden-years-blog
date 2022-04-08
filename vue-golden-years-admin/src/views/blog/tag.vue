<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-input
          clearable
          @keyup.enter.native="handleFind"
          class="filter-item"
          style="width: 200px;"
          v-model="tagName"
          placeholder="请输入标签名"
      ></el-input>
      <el-select v-model="queryParams.status" clearable placeholder="标签状态" style="width:110px">
        <el-option value="0" label="全部" checked></el-option>
        <el-option value="1" label="正常"></el-option>
        <el-option value="2" label="不可用"></el-option>
      </el-select>
      <el-select v-model="queryParams.sys" clearable placeholder="标签归属" style="width:110px">
        <el-option value="0" label="用户标签" checked></el-option>
        <el-option value="1" label="系统标签"></el-option>
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit">添加标签</el-button>
      <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete">删除选中</el-button>
      <el-button icon="el-icon-refresh" @click="refresh">刷新</el-button>
    </div>

    <el-table :data="tableData"
              style="width: 100%"
              @selection-change="handleSelectionChange">
      <el-table-column type="selection"></el-table-column>
      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.$index + 1 }}</span>
        </template>
      </el-table-column>

      <el-table-column label="标签名" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column label="标签状态" width="100" align="center">
        <template slot-scope="scope">
          <template v-if="scope.row.status === 1">
            <el-tag type="success">正常</el-tag>
          </template>
          <template v-if="scope.row.status === 0">
            <el-tag type="danger">不可用</el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="标签归属" width="100" align="center">
        <template slot-scope="scope">
          <template v-if="scope.row.sys === 1">
            <span>系统标签</span>
          </template>
          <template v-if="scope.row.sys === 0">
            <span>用户标签</span>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="标签颜色" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.color }}</span>
        </template>
      </el-table-column>
      <el-table-column label="样式" width="150" align="center">
        <template slot-scope="scope">
          <el-tag
              :hit="false"
              :color="scope.row.color"
              effect="plain" style="color: white">
            {{ scope.row.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.updateTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" min-width="230" align="center">
        <template slot-scope="scope">
          <el-button @click="handleEdit(scope.row)" type="primary" size="small">编辑</el-button>
          <el-button @click="handleDelete(scope.row)" type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <div class="block">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="records"
      ></el-pagination>
    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="dialogFormVisible">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item label="标签名称" :label-width="formLabelWidth" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>

        <el-form-item label="标签颜色" :label-width="formLabelWidth" prop="color">
          <div class="block">
            <el-color-picker v-model="form.color"></el-color-picker>
          </div>
        </el-form-item>
        <el-form-item label="标签状态" :label-width="formLabelWidth">
          <el-switch
              v-model="form.status"
              active-color="#13ce66"
              inactive-color="#ff4949">
          </el-switch>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>


import {saveOrUpdateTag, getTagList, deleteTag} from "@/api/blog";

export default {
  data() {
    return {
      // 多选, 用于批量删除
      multipleSelection: [],
      tableData: [],
      keyword: "",
      currentPage: 1,
      pageSize: 10,
      records: 0,
      totalPage: 0,
      title: "增加标签",
      // 控制弹出框
      dialogFormVisible: false,
      formLabelWidth: "120px",
      isEditForm: false,
      form: {
        id: null,
        name: null,
        color: null,
        status: false
      },
      queryParams: {
        tagName: "",
        status: "",
        sys: ""
      },
      rules: {
        name: [
          {required: true, message: '标签名称不能为空', trigger: 'blur'},
          {min: 1, max: 20, message: '长度在1到20个字符'},
        ],
        color: [
          {required: true, message: '请选择标签颜色', trigger: 'blur'},
        ]
      }
    };
  },
  created() {
    this.tagList();
  },
  methods: {

    tagList() {
      let params = new URLSearchParams;
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getTagList(params).then((response) => {
        console.log(response);
        let content = response.data;
        this.tableData = content.rows;
        this.currentPage = content.currentPage;
        this.totalPage = content.totalPage;
        this.records = content.records;
      })
    },
    handleFind() {
      // TODO
    },
    handleAdd() {
      this.title = "增加标签"
      this.dialogFormVisible = true;
      this.clearForm();
      this.isEditForm = false;
    },
    handleEdit(row) {
      this.title = "编辑标签"
      this.dialogFormVisible = true;
      this.isEditForm = true;
      this.form.name = row.name;
      this.form.color = row.color;
      this.form.id = row.id;
      this.form.status = !!row.status;
    },
    handleDelete(row) {
      this.$confirm("此操作将把标签删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteTag(row.id).then((response) => {
          this.tagList();
          this.$message.success(response.msg);
        })
      })
    },
    handleDeleteBatch() {
      const that = this;
      if (that.multipleSelection.length <= 0) {
        this.$message.error("请先选中需要删除的内容!")
        return;
      }
      this.$confirm("此操作将把选中的标签删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {

      })

    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.tagList();
    },
    clearForm() {
      this.form = {
        name: null,
        color: null,
        status: false
      }
    },
    refresh() {
      this.queryParams = {
        tagName: "",
        status: "",
        sys: ""
      }
      this.tagList();
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 编辑模式
          if (this.isEditForm) {
            this.form.status = Number(this.form.status);
            saveOrUpdateTag(this.form).then((response) => {
              this.dialogFormVisible = false;
              this.clearForm();
              this.tagList();
              this.$message.success(response.msg);
            })
          }
          // 添加模式
          else {
            this.form.status = Number(this.form.status);
            saveOrUpdateTag(this.form).then(response => {
              this.dialogFormVisible = false;
              this.clearForm();
              this.tagList();
              this.$message.success(response.msg);
            })
          }
        }
      })
    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val;
    }
  }
};
</script>
