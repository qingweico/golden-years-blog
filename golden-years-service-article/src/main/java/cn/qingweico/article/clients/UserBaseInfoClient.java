package cn.qingweico.article.clients;

import cn.qingweico.result.GraceJsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author:qiming
 * @date: 2021/10/19
 */
@FeignClient(value = "service-user", path = "user", fallback = UserClientFallback.class)
public interface UserBaseInfoClient {
    @GetMapping("queryByIds")
    GraceJsonResult getUserBasicInfoList(@RequestParam("userIds") String userIds);
}
