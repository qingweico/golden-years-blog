package cn.qingweico.article.service;

import cn.qingweico.pojo.Tag;
import cn.qingweico.pojo.bo.TagBO;
import cn.qingweico.util.PagedGridResult;

import java.util.List;


/**
 * @author zqw
 * @date 2022/03/05
 */
public interface TagService {
    /**
     * 获取标签列表
     *
     * @param page     page
     * @param pageSize page
     * @return PagedGridResult
     */
    PagedGridResult getTagList(Integer page, Integer pageSize);

    /**
     * 获取标签列表
     *
     * @return List<Tag>
     */
    List<Tag> getTagList();

    /**
     * 管理员添加或者更新系统标签
     *
     * @param tagBO {@link TagBO}
     */
    void saveOrUpdate(TagBO tagBO);

    /**
     * 管理员删除系统标签
     *
     * @param tagId 标签id
     */
    void delete(String tagId);


    /**
     * 删除用户自定义标签
     */
    void deletePersonalTag();

    /**
     * 添加用户自定义标签
     *
     * @param tag    Tag
     * @param userId userId
     * @return String tagId
     */
    String addPersonalTag(Tag tag, String userId);
}
