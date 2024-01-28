package cn.qingweico.article.service.impl;

import cn.qingweico.article.clients.UserBaseInfoClient;
import cn.qingweico.entity.model.UserBasicInfo;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.JsonUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zqw
 * @date 2023/9/30
 */
@Component
@Slf4j
public class GenericUserChannel {
    @Resource
    private UserBaseInfoClient client;

    public UserBasicInfo getUserBasicInfoClient(String id) {
        UserBasicInfo userBasicInfo;
        Result result = client.getUserBasicInfo(id);
        if (result.getData() != null) {
            String userJson = JsonUtils.objectToJson(result.getData());
            userBasicInfo = JsonUtils.jsonToPojo(userJson, UserBasicInfo.class);
        } else {
            userBasicInfo = new UserBasicInfo();
        }
        return userBasicInfo;
    }
    public Map<String, Object> geAuthorInfo(String authorId) {
        Map<String, Object> map = new HashMap<>(4);
        UserBasicInfo userBasicInfo = getUserBasicInfoClient(authorId);
        if (ObjectUtils.isNotNull(userBasicInfo)) {
            map.put(SysConst.AUTHOR_NAME, userBasicInfo.getNickname());
            map.put(SysConst.AUTHOR_FACE, userBasicInfo.getFace());
            map.put(SysConst.FOLLOW_COUNTS, userBasicInfo.getMyFollowCounts());
            map.put(SysConst.FANS_COUNTS, userBasicInfo.getMyFansCounts());
        } else {
            GraceException.error(Response.SYSTEM_ERROR);
            log.error("用户服务不可用");
        }
        return map;
    }
}
