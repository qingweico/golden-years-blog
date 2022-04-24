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
          @click="handleArticleSortByCount"
          icon="el-icon-document">博客数量排序
      </el-button>
      <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
    </div>
    <el-table :data="tableData"
              @selection-change="handleSelectionChange"
              style="width: 100%">
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
      <el-table-column label="状态" width="150" align="center" prop="clickCount">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 1">可用</el-tag>
          <el-tag type="danger" v-if="scope.row.status === 0">不可用</el-tag>
        </template>
      </el-table-column>


      <el-table-column label="创建时间" width="150" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="更新时间" width="150" align="center" prop="createTime">
        <template slot-scope="scope">
          <span>{{ scope.row.updateTime }}</span>
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
    <el-dialog :title="title" :visible.sync="dialogFormVisible" :before-close="closeDialog">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item label="分类名称" :label-width="formLabelWidth" prop="name">
          <el-input v-model="form.name" @input="isChange = true"></el-input>
        </el-form-item>

        <el-form-item label="分类介绍" :label-width="formLabelWidth" prop="description">
          <el-input v-model="form.description" @input="isChange = true"
                    :autosize="{ minRows: 3, maxRows: 10}"
                    type="textarea"></el-input>
        </el-form-item>

        <el-form-item label="状态" :label-width="formLabelWidth">
          <el-switch
              v-model="form.status"
              active-color="#13ce66"
              inactive-color="#ff4949">
          </el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import {
  deleteCategory,
  saveOrUpdateCategory,
  batchDeleteCategory,
  getCategoryListWithArticleCount
} from "@/api/blog";

export default {
  data() {
    return {
      // 多选, 用于批量删除
      multipleSelection: [],
      tableData: [],
      keyword: "",
      sort: false,
      currentPage: 1,
      pageSize: 10,
      records: 0,
      totalPage: 0,
      title: "增加分类",
      // 控制弹出框
      dialogFormVisible: false,
      isChange: false,
      formLabelWidth: "120px",
      isEditForm: false,
      form: {
        id: null,
        name: null,
        oldName: null,
        description: null,
        status: false
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
  watch: {},
  methods: {
    categoryList() {
      let params = new URLSearchParams();
      params.append("keyword", this.keyword.trim());
      params.append("page", this.currentPage);
      params.append("sort", this.sort);
      params.append("pageSize", this.pageSize);
      getCategoryListWithArticleCount(params).then(res => {
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
        description: null,
        status: false,
      }
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
    clearStatus() {
      this.dialogFormVisible = false;
      this.clearData();
      this.isChange = false;
    },
    handleFind() {
      this.categoryList();
    },
    handleAdd() {
      this.title = "增加分类"
      this.dialogFormVisible = true;
      this.clearData();
      this.isEditForm = false;
    },
    handleCancel() {
      this.dialogFormVisible = false;
      // 清除本次的校验信息
      this.$refs.form.clearValidate();
    },
    // 通过博客数量排序
    handleArticleSortByCount() {
      this.$confirm(
          "此操作将根据博博客数量进行排序, 是否继续?",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          }
      ).then(() => {
        this.sort = true;
        this.categoryList();
      })
    },
    handleDeleteBatch() {
      const that = this;
      if (that.multipleSelection.length <= 0) {
        this.$message.error("请先选中需要删除的内容!")
        return;
      }
      this.$confirm("此操作将把选中的类别删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        let ids = [];
        for (let item of that.multipleSelection) {
          ids.push(item.id);
        }
        batchDeleteCategory(ids).then(response => {
          this.$message.success(response.msg);
          that.multipleSelection = [];
          this.categoryList();
        })
      })
    },
    handleEdit(row) {
      this.title = "编辑分类"
      this.dialogFormVisible = true;
      this.isEditForm = true;
      this.form.id = row.id;
      this.form.oldName = row.name;
      this.form.name = row.name;
      this.form.description = row.description;
      this.form.status = !!row.status;
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
      })
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.categoryList();
    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    getBlogCategoryNameById(categoryId) {
      let categoryList = this.tableData;
      alert(JSON.stringify(categoryList))
      for (let i = 0; i < categoryList.length; i++) {
        if (categoryId === categoryList[i].id) {
          return categoryList[i].name;
        }
      }
    },
    resetQuery() {
      this.keyword = "";
      this.sort = false;
      this.categoryList();
    },
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.form.status = Number(this.form.status);
          if (this.isEditForm) {
            saveOrUpdateCategory(this.form).then(response => {
              this.$message.success(response.msg);
              this.dialogFormVisible = false;
              this.categoryList();
            });
          } else {
            saveOrUpdateCategory(this.form).then(response => {
              this.$message.success(response.msg);
              this.dialogFormVisible = false;
              this.categoryList();
            });
          }

        }
      });
    }
  }
};
</script>
