package cn.qingweico.article.clients;

import cn.qingweico.grace.result.GraceJsonResult;
import cn.qingweico.grace.result.ResponseStatusEnum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author:qiming
 * @date: 2021/10/19
 */
@Component
public class UserClientFallback implements UserBaseInfoClient {
    @Override
    public GraceJsonResult getUserBasicInfoList(String userIds) {
        return new GraceJsonResult(ResponseStatusEnum.SYSTEM_ERROR, new ArrayList<>(0));
    }
}
