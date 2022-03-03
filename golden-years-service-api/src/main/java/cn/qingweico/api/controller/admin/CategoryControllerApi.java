package cn.qingweico.api.controller.admin;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.SaveCategoryBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Api(value = "文章分类", tags = {"文章分类"})
@RequestMapping("category")
public interface CategoryControllerApi {

    /**
     * 查询分类列表
     * @return GraceJsonResult
     */
    @ApiOperation(value = "查询分类列表", notes = "查询分类列表", httpMethod = "GET")
    @GetMapping("/getCategoryList")
    GraceJsonResult getCategoryList();

    /**
     * 新增或者修改文章分类
     * @param saveCategoryBO SaveCategoryBO
     * @return GraceJsonResult
     */
    @ApiOperation(value = "新增或者修改文章分类", notes = "新增或者修改文章分类", httpMethod = "POST")
    @PostMapping("/saveOrUpdateCategory")
    GraceJsonResult saveOrUpdateCategory(@RequestBody @Valid SaveCategoryBO saveCategoryBO);
}
