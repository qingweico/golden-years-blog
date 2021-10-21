package cn.qingweico.admin.clients;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author:qiming
 * @date: 2021/10/21
 */
@FeignClient(value = "service-article", path = "/portal/article")
public interface CategoryArticleCountClient {
    @GetMapping("queryEachCategoryCount")
    List<Integer> queryEachCategoryCount();
}
