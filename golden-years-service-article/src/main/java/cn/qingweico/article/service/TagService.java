package cn.qingweico.article.service;

import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Category;
import cn.qingweico.pojo.Tag;
import cn.qingweico.pojo.vo.ArticleDetailVO;
import cn.qingweico.util.PagedGridResult;

import java.util.List;


/**
 *
 * @author zqw
 * @date 2022/03/05
 */
public interface TagService {
    /**
     *
     * @return
     */
    List<Tag> getTagList();
    /**
     *
     * @return
     */
    Integer deletePersonalTag();
    /**
     *
     * @return
     */
    Integer addPersonalTag();
}
