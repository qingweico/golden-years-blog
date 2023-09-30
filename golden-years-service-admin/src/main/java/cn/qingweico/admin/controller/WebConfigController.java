package cn.qingweico.admin.controller;

import cn.qingweico.admin.service.WebConfigService;
import cn.qingweico.entity.WebConfig;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2022/4/11
 */
@Api(value = "网站配置相关接口", tags = {"网站配置相关接口"})
@RestController
@RequestMapping("/webConfig")
@Slf4j
public class WebConfigController {

    @Resource
    WebConfigService webConfigService;

    @ApiOperation(value = "获取网站配置", notes = "获取网站配置")
    @GetMapping("/getWebConfig")
    public Result getWebConfig() {
        return Result.ok(webConfigService.getWebConfig());
    }

    @ApiOperation(value = "修改网站配置", notes = "修改网站配置")
    @PostMapping("/alterWebConfig")
    public Result alterWebConfig(@RequestBody WebConfig webConfig) {
        webConfigService.alterWebConfig(webConfig);
        return Result.r(Response.ALERT_SUCCESS);
    }
}

