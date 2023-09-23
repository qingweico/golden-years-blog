package cn.qingweico.user.clients;

import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.user.entity.CollectBO;
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
    public Result createFavorites(@RequestBody CollectBO collectBO) {
        log.warn("{}, create favorites, 文章服务不可用", DateUtils.dateToStringWithTime());
        return Result.r(Response.SYSTEM_ERROR);
    }
}
