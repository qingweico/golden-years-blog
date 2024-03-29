package cn.qingweico.article.controller;

import cn.qingweico.api.base.BaseController;
import cn.qingweico.article.service.TagService;
import cn.qingweico.pojo.bo.TagBO;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author zqw
 * @date 2022/3/5
 */
@Api(value = "标签相关的接口定义", tags = {"标签相关的接口定义"})
@RequestMapping("tag")
@RestController
public class TagController extends BaseController {

    @Resource
    private TagService tagService;

    @ApiOperation(value = "管理员查询所有的标签", notes = "管理员查询所有的标签", httpMethod = "GET")
    @GetMapping("/query")
    public Result query(@RequestParam String tagName,
                        @RequestParam Integer status,
                        @RequestParam Integer sys,
                        @RequestParam Integer page,
                        @RequestParam Integer pageSize) {
        checkPagingParams(page, pageSize);
        return Result.ok(tagService.getTagList(tagName, status, sys, page, pageSize));
    }

    @ApiOperation(value = "查询所有的标签", notes = "查询所有的标签", httpMethod = "GET")
    @GetMapping("/list")
    public Result list() {
        return Result.ok(tagService.getTagList());
    }


    @ApiOperation(value = "管理员添加或者更新系统标签", notes = "管理员添加或者更新系统标签", httpMethod = "POST")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody @Valid TagBO tagBO) {
        tagService.saveOrUpdate(tagBO);
        if (tagBO.getId() == null) {
            return Result.r(Response.APPEND_SUCCESS);
        } else {
            return Result.r(Response.ALERT_SUCCESS);
        }
    }

    @ApiOperation(value = "管理员删除标签", notes = "管理员删除标签", httpMethod = "POST")
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") String tagId) {
        tagService.delete(tagId);
        return Result.r(Response.DELETE_SUCCESS);
    }

    @ApiOperation(value = "管理员批量删除标签", notes = "管理员批量删除标签", httpMethod = "POST")
    @PostMapping("/delete")
    public Result batchDelete(@RequestBody List<String> ids) {
        tagService.batchDelete(ids);
        return Result.r(Response.DELETE_SUCCESS);
    }

    @ApiOperation(value = "用户添加自定义标签", notes = "用户添加自定义标签", httpMethod = "POST")
    @PostMapping("/user/add")
    public Result addPersonalTag() {
        return null;
    }


    @ApiOperation(value = "用户删除我的自定义标签", notes = "用户删除我的自定义标签", httpMethod = "POST")
    @PostMapping("/user/delete")
    public Result deletePersonalTag() {
        return null;
    }
}
