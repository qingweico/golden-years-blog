package cn.qingweico.admin.controller;

import cn.qingweico.admin.service.CategoryService;
import cn.qingweico.core.base.BaseController;
import cn.qingweico.entity.Category;
import cn.qingweico.entity.model.SaveOrUpdateCategory;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.util.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/11
 */
@Api(value = "文章分类", tags = {"文章分类"})
@RequestMapping("category")
@RestController
public class CategoryController extends BaseController {
    @Resource
    private CategoryService categoryService;

    @ApiOperation(value = "查询分类列表", notes = "查询分类列表", httpMethod = "GET")
    @GetMapping("/getCategoryList")
    public Result getCategoryList(@RequestParam String keyword,
                                  @RequestParam Boolean sort,
                                  @RequestParam Integer page,
                                  @RequestParam Integer pageSize) {
        PagedResult result = categoryService.queryCategoryList(keyword,sort, page, pageSize);
        return Result.ok(result);
    }

    @ApiOperation(value = "新增或者修改文章分类", notes = "新增或者修改文章分类", httpMethod = "POST")
    @PostMapping("/saveOrUpdateCategory")
    public Result saveOrUpdateCategory(@RequestBody SaveOrUpdateCategory saveOrUpdateCategory) {

        boolean isExist;
        Category pendingCategory = new Category();
        BeanUtils.copyProperties(saveOrUpdateCategory, pendingCategory);
        String pendingCategoryId = pendingCategory.getId();

        // id为空; 新增类别
        if (pendingCategoryId == null) {
            isExist = categoryService.queryCategoryHasPresent(pendingCategory.getName(),
                    null);
            if (isExist) {
                return Result.r(Response.CATEGORY_EXIST_ERROR);
            } else {
                categoryService.createCategory(pendingCategory);
            }
        }
        // id不为空; 修改类别
        else {
            isExist = categoryService.queryCategoryHasPresent(pendingCategory.getName(),
                    saveOrUpdateCategory.getOldName());
            if (isExist) {
                return Result.r(Response.CATEGORY_EXIST_ERROR);
            } else {
                categoryService.modifyCategory(pendingCategory);
            }
        }

        if (pendingCategoryId == null) {
            return Result.r(Response.APPEND_SUCCESS);
        } else {
            return Result.r(Response.ALERT_SUCCESS);
        }
    }

    @ApiOperation(value = "删除类别", notes = "删除类别", httpMethod = "POST")
    @PostMapping("/deleteCategory/{id}")
    public Result deleteCategory(@PathVariable("id") String categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.r(Response.DELETE_SUCCESS);
    }
    @ApiOperation(value = "批量删除类别", notes = "批量删除类别", httpMethod = "POST")
    @PostMapping("/delete")
    public Result batchDeleteCategory(@RequestBody List<String> ids) {
        categoryService.deleteAll(ids);
        return Result.r(Response.DELETE_SUCCESS);
    }
}
