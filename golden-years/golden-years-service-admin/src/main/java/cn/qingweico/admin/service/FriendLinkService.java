package cn.qingweico.admin.service;

import cn.qingweico.pojo.mo.FriendLinkMO;

import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
public interface FriendLinkService {

    /**
     * 保存或者更新友情链接
     *
     * @param friendLinkMO FriendLinkMO
     */
    void saveOrUpdateFriendLink(FriendLinkMO friendLinkMO);

    /**
     * 查询所有的友情链接
     *
     * @return 友情链接列表
     */
    List<FriendLinkMO> queryAllFriendLinkList();

    /**
     * 根据友情链接id删除友情链接
     *
     * @param linkId 友情链接id
     */
    void delete(String linkId);

    /**
     * 首页友情链接的展示
     *
     * @param isDelete 友情链接的可用状态
     * @return 友情链接列表
     */
    List<FriendLinkMO> queryAllFriendLinkList(Integer isDelete);

}
