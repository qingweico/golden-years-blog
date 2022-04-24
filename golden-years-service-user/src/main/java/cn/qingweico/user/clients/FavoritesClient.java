package cn.qingweico.user.clients;

import cn.qingweico.pojo.bo.CollectBO;
import cn.qingweico.result.GraceJsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author zqw
 * @date 2022/4/16
 */
@FeignClient(value = "service-article", path = "/portal/article", fallback = FavoritesClientFallback.class)
public interface FavoritesClient {
    /**
     * 创建收藏夹
     *
     * @param collectBO {@link CollectBO}
     * @return GraceJsonResult
     */
    @PostMapping("createFavorites")
    GraceJsonResult createFavorites(@RequestBody CollectBO collectBO);
}
