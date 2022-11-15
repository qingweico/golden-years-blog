package cn.qingweico.admin.service;

import cn.qingweico.pojo.mo.FriendLink;
import cn.qingweico.util.PagedResult;

import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
public interface FriendLinkService {

    /**
     * 保存或者更新友情链接
     *
     * @param friendLink FriendLinkMo
     */
    void saveOrUpdateFriendLink(FriendLink friendLink);

    /**
     * 查询所有的友情链接
     *
     * @param page     起始分页
     * @param pageSize 每页的数量
     * @return PagedGridResult 友情链接列表
     */
    PagedResult getFriendLinkList(String linkName, Integer isDelete,
                                  Integer page, Integer pageSize);

    /**
     * 根据友情链接id删除友情链接
     *
     * @param linkId 友情链接id
     */
    void delete(String linkId);

    /**
     * 批量删除友情链接
     *
     * @param ids 友情链接id集合
     */
    void deleteAll(List<String> ids);

    /**
     * 首页友情链接的展示
     *
     * @param isDelete 友情链接的可用状态
     * @return 友情链接列表
     */
    List<FriendLink> queryIndexFriendLinkList(Integer isDelete);

}
