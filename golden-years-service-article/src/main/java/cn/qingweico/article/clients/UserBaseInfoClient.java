package cn.qingweico.article.clients;

import cn.qingweico.result.GraceJsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zqw
 * @date 2021/10/19
 */
@FeignClient(value = "service-user", path = "user", fallback = UserClientFallback.class)
public interface UserBaseInfoClient {
    /**
     * 批量查询用户基本信息
     *
     * @param userIds 用户id集合
     * @return GraceJsonResult
     */
    @GetMapping("queryByIds")
    GraceJsonResult getUserBasicInfoList(@RequestParam("userIds") String userIds);
}
