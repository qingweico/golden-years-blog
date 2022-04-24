package cn.qingweico.user.clients;

import cn.qingweico.pojo.bo.CollectBO;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zqw
 * @date 2022/4/16
 */
@Slf4j
@Component
public class FavoritesClientFallback implements FavoritesClient {
    @Override
    public GraceJsonResult createFavorites(@RequestBody CollectBO collectBO) {
        log.warn("{}, create favorites, 文章服务不可用", DateUtils.dateToStringWithTime());
        return new GraceJsonResult(ResponseStatusEnum.SYSTEM_ERROR);
    }
}
