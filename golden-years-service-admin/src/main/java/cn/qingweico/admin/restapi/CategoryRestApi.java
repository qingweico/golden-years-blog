package cn.qingweico.admin.restapi;

import cn.qingweico.admin.service.CategoryService;
import cn.qingweico.api.base.BaseRestApi;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.bo.SaveCategoryBO;
import cn.qingweico.util.PagedGridResult;
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
public class CategoryRestApi extends BaseRestApi {
    @Resource
    private CategoryService categoryService;

    @ApiOperation(value = "查询分类列表", notes = "查询分类列表", httpMethod = "GET")
    @GetMapping("/getCategoryList")
    public GraceJsonResult getCategoryList(@RequestParam String keyword,
                                           @RequestParam Boolean sort,
                                           @RequestParam Integer page,
                                           @RequestParam Integer pageSize) {
        PagedGridResult result = categoryService.queryCategoryList(keyword,sort, page, pageSize);
        return GraceJsonResult.ok(result);
    }

    @ApiOperation(value = "新增或者修改文章分类", notes = "新增或者修改文章分类", httpMethod = "POST")
    @PostMapping("/saveOrUpdateCategory")
    public GraceJsonResult saveOrUpdateCategory(@RequestBody SaveCategoryBO saveCategoryBO) {

        boolean isExist;
        Category pendingCategory = new Category();
        BeanUtils.copyProperties(saveCategoryBO, pendingCategory);
        String pendingCategoryId = pendingCategory.getId();

        // id为空; 新增类别
        if (pendingCategoryId == null) {
            isExist = categoryService.queryCategoryIsPresent(pendingCategory.getName(),
                    null);
            if (isExist) {
                return GraceJsonResult.error(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            } else {
                categoryService.createCategory(pendingCategory);
            }
        }
        // id不为空; 修改类别
        else {
            isExist = categoryService.queryCategoryIsPresent(pendingCategory.getName(),
                    saveCategoryBO.getOldName());
            if (isExist) {
                return GraceJsonResult.error(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            } else {
                categoryService.modifyCategory(pendingCategory);
            }
        }

        if (pendingCategoryId == null) {
            return new GraceJsonResult(ResponseStatusEnum.APPEND_SUCCESS);
        } else {
            return new GraceJsonResult(ResponseStatusEnum.ALERT_SUCCESS);
        }
    }

    @ApiOperation(value = "删除类别", notes = "删除类别", httpMethod = "POST")
    @PostMapping("/deleteCategory/{id}")
    public GraceJsonResult deleteCategory(@PathVariable("id") String categoryId) {
        categoryService.deleteCategory(categoryId);
        return new GraceJsonResult(ResponseStatusEnum.DELETE_SUCCESS);
    }
    @ApiOperation(value = "批量删除类别", notes = "批量删除类别", httpMethod = "POST")
    @PostMapping("/delete")
    public GraceJsonResult batchDeleteCategory(@RequestBody List<String> ids) {
        categoryService.deleteAll(ids);
        return new GraceJsonResult(ResponseStatusEnum.DELETE_SUCCESS);
    }
}
