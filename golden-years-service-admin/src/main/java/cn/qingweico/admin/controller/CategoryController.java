package cn.qingweico.admin.controller;

import cn.qingweico.admin.service.CategoryService;
import cn.qingweico.api.controller.admin.CategoryControllerApi;
import cn.qingweico.api.controller.BaseController;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.bo.SaveCategoryBO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/11
 */
@RestController
public class CategoryController extends BaseController implements CategoryControllerApi {
    @Resource
    private CategoryService categoryService;

    @Override
    public GraceJsonResult getCategoryList() {
        List<Category> categoryList = categoryService.queryCategoryList();
        return GraceJsonResult.ok(categoryList);
    }

    @Override
    public GraceJsonResult saveOrUpdateCategory(SaveCategoryBO saveCategoryBO) {

        boolean isExist;
        Category pendingCategory = new Category();
        BeanUtils.copyProperties(saveCategoryBO, pendingCategory);

        Integer pendingCategoryId = pendingCategory.getId();

        // id为空; 新增类别
        if (pendingCategoryId == null) {
            isExist = categoryService.queryCategoryIsPresent(pendingCategory.getName(),
                    null);
            if (isExist) {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
            } else {
                categoryService.createCategory(pendingCategory);
            }
        }
        // id不为空; 修改类别
        else {
            isExist = categoryService.queryCategoryIsPresent(pendingCategory.getName(),
                    saveCategoryBO.getOldName());
            if (isExist) {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
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
}
