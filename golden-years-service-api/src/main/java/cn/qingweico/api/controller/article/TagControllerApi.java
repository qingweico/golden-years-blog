package cn.qingweico.api.controller.article;

import cn.qingweico.result.GraceJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zqw
 * @date 2022/3/5
 */

@Api(value = "标签相关的接口定义", tags = {"标签相关的接口定义"})
@RequestMapping("tag")
public interface TagControllerApi {

    /**
     * 查询所有的标签
     *
     * @return GraceJsonResult
     */
    @ApiOperation(value = "查询所有的标签", notes = "查询所有的标签", httpMethod = "GET")
    @GetMapping("/query")
    GraceJsonResult query();

    /**
     * 用户添加自定义标签
     *
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户添加自定义标签", notes = "用户添加自定义标签", httpMethod = "POST")
    @PostMapping("/user/add")
    GraceJsonResult addPersonalTag();

    /**
     * 用户删除我的自定义标签
     *
     * @return GraceJsonResult
     */
    @ApiOperation(value = "用户删除我的自定义标签", notes = "用户删除我的自定义标签", httpMethod = "POST")
    @PostMapping("/user/delete")
    GraceJsonResult deletePersonalTag();
}
