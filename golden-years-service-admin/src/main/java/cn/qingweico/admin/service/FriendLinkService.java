package cn.qingweico.admin.service;

import cn.qingweico.pojo.mo.FriendLinkMo;

import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
public interface FriendLinkService {

    /**
     * 保存或者更新友情链接
     *
     * @param friendLinkMo FriendLinkMO
     */
    void saveOrUpdateFriendLink(FriendLinkMo friendLinkMo);

    /**
     * 查询所有的友情链接
     *
     * @return 友情链接列表
     */
    List<FriendLinkMo> queryAllFriendLinkList();

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
    List<FriendLinkMo> queryAllFriendLinkList(Integer isDelete);

}
