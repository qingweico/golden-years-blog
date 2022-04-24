package cn.qingweico.admin.clients;

import cn.qingweico.enums.UserStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zqw
 * @date 2022/4/18
 */
@FeignClient(value = "service-user", path = "/user")
public interface UserClient {

    /**
     * 获取全站用户数
     * <p>
     * {@link UserStatus} 只获取已激活和冻结的用户
     *
     * @return 全站用户数
     */
    @GetMapping("/getUserCounts")
    Integer getUserCounts();
}
