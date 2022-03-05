package cn.qingweico.article.clients;

import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
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
public class ArticleDetailClientFallback implements ArticleDetailClient {

    @Override
    public GraceJsonResult getArticleDetail(String articleId) {
        log.warn("{}, 文章服务不可用", DateUtils.dateToStringWithTime());
        return new GraceJsonResult(ResponseStatusEnum.SYSTEM_ERROR);
    }
}
