package cn.qingweico.admin.restapi;

import cn.qingweico.admin.service.SystemConfigService;
import cn.qingweico.pojo.SystemConfig;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zqw
 * @date 2022/4/12
 */
@Api(value = "系统配置相关接口", tags = {"系统配置相关接口"})
@RestController
@RequestMapping("/systemConfig")
@Slf4j
public class SystemConfigRestApi {

    @Resource
    private SystemConfigService systemConfigService;

    @ApiOperation(value = "获取系统配置", notes = "获取系统配置")
    @GetMapping("/getSystemConfig")
    public Result getSystemConfig() {
        return Result.ok(systemConfigService.getSystemConfig());
    }

    @ApiOperation(value = "通过Key前缀清空Redis缓存", notes = "通过Key前缀清空Redis缓存")
    @PostMapping("/cleanRedisByKey")
    public Result cleanRedisByKey(@RequestBody List<String> key) {
        systemConfigService.cleanRedisByKey(key);
        return Result.r(Response.CLEAN_CACHE_SUCCESS);
    }


    @ApiOperation(value = "修改系统配置", notes = "修改系统配置")
    @PostMapping("/alterSystemConfig")
    public Result editSystemConfig(@RequestBody SystemConfig systemConfig) {
        systemConfigService.alterSystemConfig(systemConfig);
        return Result.r(Response.ALERT_SUCCESS);
    }
}
