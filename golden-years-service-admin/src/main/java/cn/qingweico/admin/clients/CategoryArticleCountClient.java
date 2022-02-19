package cn.qingweico.admin.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author zqw
 * @date 2021/10/21
 */
@FeignClient(value = "service-article", path = "/portal/article")
public interface CategoryArticleCountClient {
    /**
     *
     * @return
     */
    @GetMapping("queryEachCategoryCount")
    List<Integer> queryEachCategoryCount();
}
