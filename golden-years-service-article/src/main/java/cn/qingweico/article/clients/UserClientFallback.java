package cn.qingweico.article.clients;

import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author zqw
 * @date 2021/10/19
 */
@Slf4j
@Component
public class UserClientFallback implements UserBaseInfoClient {

    @Override
    public Result queryByIds(String userIds) {
        log.warn("{}, query user info, 用户服务不可用", DateUtils.dateToStringWithTime());
        return Result.ok(Response.SYSTEM_ERROR, new ArrayList<>(0));
    }

    @Override
    public Result getUserBasicInfo(String userId) {
        log.warn("{}, get user basic info, 用户服务不可用", DateUtils.dateToStringWithTime());
        return Result.r(Response.SYSTEM_ERROR);
    }
}
