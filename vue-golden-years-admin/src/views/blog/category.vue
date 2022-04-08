<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;">
      <el-input
          clearable
          @keyup.enter.native="handleFind"
          class="filter-item"
          style="width: 200px;"
          v-model="keyword"
          placeholder="请输入分类名"
      ></el-input>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFind">查找</el-button>
      <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit">添加分类</el-button>
      <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete">删除选中</el-button>
      <el-button
          class="filter-item"
          type="info"
          @click="handleBlogSortByQuantity"
          icon="el-icon-document">博客数量排序
      </el-button>
      <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
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

      <el-table-column label="分类名" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column label="分类介绍" width="150" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.description }}</span>
        </template>
      </el-table-column>

      <el-table-column label="文章数" width="150" align="center" prop="clickCount">
        <template slot-scope="scope">
          <span>{{ scope.row.eachCategoryArticleCount }}</span>
        </template>
      </el-table-column>


      <el-table-column label="创建时间" width="150" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>


      <el-table-column label="操作" fixed="right" align="center" min-width="250">
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
        <el-form-item label="分类名称" :label-width="formLabelWidth" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>

        <el-form-item label="分类介绍" :label-width="formLabelWidth" prop="description">
          <el-input v-model="form.description"
                    :autosize="{ minRows: 3, maxRows: 10}"
                    type="textarea"></el-input>
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

import {deleteCategory, getBlogCategory, saveOrUpdateCategory} from "../../api/blog";

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
      title: "增加分类",
      // 控制弹出框
      dialogFormVisible: false,
      formLabelWidth: "120px",
      isEditForm: false,
      queryParams: {
        name: ""
      },
      form: {
        id: null,
        name: null,
        oldName: null,
        description: null
      },
      rules: {
        name: [
          {required: true, message: '分类名称不能为空', trigger: 'blur'},
          {min: 1, max: 10, message: '长度在1到10个字符'},
        ],
        description: [
          {required: true, message: '分类描述不能为空', trigger: 'blur'},
          {min: 1, max: 100, message: '长度在1到100个字符'},
        ]
      }
    };
  },
  created() {
    this.categoryList();
  },
  methods: {
    categoryList() {
      let params = new URLSearchParams();
      params.append("page", this.currentPage);
      params.append("pageSize", this.pageSize);
      getBlogCategory(params).then(res => {
        this.tableData = res.data.rows;
        this.records = res.data.records;
        this.totalPages = res.data.totalPages;
      })
    },
    clearData() {
      this.form = {
        id: null,
        name: null,
        oldName: null,
        description: null
      }
    },
    handleFind() {
      this.currentPage = 1
    },
    handleAdd() {
      this.title = "增加分类"
      this.dialogFormVisible = true;
      this.clearData();
      this.isEditForm = false;
    },
    // 通过博客数量排序
    handleBlogSortByQuantity() {
      this.$confirm(
          "此操作将根据博博客数量进行排序, 是否继续?",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          }
      ).then(() => {
      }).catch(() => {
        this.$message.info("已取消批量排序")
      });
    },
    handleEdit(row) {
      this.title = "编辑分类"
      this.dialogFormVisible = true;
      this.isEditForm = true;
      this.form.id = row.id;
      this.form.oldName = row.name;
      this.form.name = row.name;
      this.form.description = row.description;
    },
    handleDelete(row) {
      this.$confirm("此操作将把分类删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteCategory(row.id).then(result => {
          this.categoryList();
          this.$message.success(result.msg);
        })
      }).catch(() => {
        this.$message.info("已取消删除")
      });
    },
    handleDeleteBatch() {
      if (this.multipleSelection.length <= 0) {
        this.$message.error("请先选中需要删除的内容!")
        return;
      }
      this.$confirm("此操作将把选中的分类删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {

      }).catch(() => {
        this.$message.info("已取消删除")
      });
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.categoryList();
    },
    resetQuery() {
      this.queryParams = {
        name: "",
        isDelete: ""
      }
      this.categoryList();
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          if (this.isEditForm) {
            saveOrUpdateCategory(this.form).then(response => {
              this.$message.success(response.msg)
              this.dialogFormVisible = false;
              this.clearData();
              this.categoryList();
            });
          } else {
            saveOrUpdateCategory(this.form).then(response => {
              this.$message.success(response.msg)
              this.dialogFormVisible = false;
              this.categoryList();
            });
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
