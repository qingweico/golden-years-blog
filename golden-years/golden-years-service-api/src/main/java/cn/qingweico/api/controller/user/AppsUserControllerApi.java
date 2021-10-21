package cn.qingweico.api.controller.user;

import cn.qingweico.grace.result.GraceJsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
@Api(value = "用户管理相关的接口定义", tags = {"用户管理相关的接口定义"})
@RequestMapping("appUser")
public interface AppsUserControllerApi {
    @ApiOperation(value = "查询网站的所有注册用户", notes = "查询网站的所有注册用户", httpMethod = "POST")
    @GetMapping("/queryAll")
    GraceJsonResult queryAll(@RequestParam String nickname,
                             @RequestParam Integer status,
                             @RequestParam Date startDate,
                             @RequestParam Date endDate,
                             @RequestParam Integer page,
                             @RequestParam Integer pagSize);

    @ApiOperation(value = "查看用户详情", notes = "查看用户详情", httpMethod = "POST")
    @PostMapping("/userDetail")
    GraceJsonResult userDetail(@RequestParam String userId);

    @ApiOperation(value = "冻结或者解冻用户", notes = "冻结或者解冻用户", httpMethod = "POST")
    @PostMapping("/freezeUserOrNot")
    GraceJsonResult freezeUserOrNot(@RequestParam String userId,
                                    @RequestParam Integer doStatus);
}
