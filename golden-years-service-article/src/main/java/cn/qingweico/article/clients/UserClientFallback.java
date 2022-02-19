package cn.qingweico.article.clients;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author:qiming
 * @date: 2021/10/19
 */
@Component
public class UserClientFallback implements UserBaseInfoClient {
    private static final Logger log = LoggerFactory.getLogger(UserClientFallback.class);

    @Override
    public GraceJsonResult getUserBasicInfoList(String userIds) {
        log.warn("{}, 用户服务不可用", DateUtils.dateToStringWithTime());
        return new GraceJsonResult(ResponseStatusEnum.SYSTEM_ERROR, new ArrayList<>(0));
    }
}
