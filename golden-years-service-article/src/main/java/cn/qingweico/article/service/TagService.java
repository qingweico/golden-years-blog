package cn.qingweico.article.service;

import cn.qingweico.pojo.Tag;
import cn.qingweico.pojo.bo.TagBO;
import cn.qingweico.util.PagedGridResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * @author zqw
 * @date 2022/03/05
 */
public interface TagService {
    /**
     * 条件查询标签列表
     *
     * @param tagName  标签名称
     * @param status   标签状态(1: 可用 0: 不可用)
     * @param sys      标签归属(1: 系统标签 0: 用户自定义标签)
     * @param page     起始分页
     * @param pageSize 每页的数量
     * @return PagedGridResult
     */
    PagedGridResult getTagList(String tagName,
                               Integer status,
                               Integer sys,
                               Integer page,
                               Integer pageSize);

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
     * 管理员删除标签
     *
     * @param tagId 标签id
     */
    void delete(String tagId);

    /**
     * 管理员批量删除标签
     *
     * @param ids 标签id集合
     */
    void batchDelete(List<String> ids);


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
