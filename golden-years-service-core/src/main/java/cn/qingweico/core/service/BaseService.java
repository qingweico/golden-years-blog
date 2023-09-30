package cn.qingweico.core.service;

import cn.qingweico.entity.Article;
import cn.qingweico.util.PagedResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
public class BaseService {
    public PagedResult setterPagedGrid(List<?> list,
                                       Integer page) {
        // 分页 TODO
        return new PagedResult();
    }

    public List<Article> filterArticleTag(List<Article> list, String tag) {
        List<Article> result = new ArrayList<>();
        for (Article article : list) {
            String tags = article.getTags();
            String[] tagIds = tags.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",");
            for (String tagId : tagIds) {
                if (tag.equals(tagId)) {
                    result.add(article);
                    break;
                }
            }
        }
        return result;
    }
}
