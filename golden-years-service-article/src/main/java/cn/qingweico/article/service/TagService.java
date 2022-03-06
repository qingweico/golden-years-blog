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
     * 获取标签列表
     * @return  List<Tag>
     */
    List<Tag> getTagList();
    /**
     * 删除用户自定义标签
     */
    void deletePersonalTag();
    /**
     * 添加用户自定义标签
     */
    void addPersonalTag();
}
