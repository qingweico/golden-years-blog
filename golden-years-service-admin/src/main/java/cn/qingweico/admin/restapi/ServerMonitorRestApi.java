package cn.qingweico.admin.restapi;

import cn.qingweico.global.SysConf;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.util.server.ServerInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zqw
 * @date 2022/3/25
 */
@RestController
@RequestMapping("/monitor")
@Api(value = "服务监控相关接口", tags = {"系统设置相关接口"})
@Slf4j
public class ServerMonitorRestApi {

    @ApiOperation(value = "获取服务信息", notes = "获取服务信息")
    @GetMapping("/getServerInfo")
    public GraceJsonResult getInfo() {
        ServerInfo server = new ServerInfo();
        server.copyTo();
        return GraceJsonResult.ok(server);
    }
}
