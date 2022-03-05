package cn.qingweico.admin.repository;

import cn.qingweico.pojo.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Repository
public interface FriendLinkRepository extends MongoRepository<FriendLinkMO, String> {

    /**
     * 根据友情链接的逻辑状态获取所有的友情链接
     * @param isDelete 逻辑状态 1: 未删除; 0: 已删除
     * @return 友情链接列表
     */
    List<FriendLinkMO> getAllByIsDelete(Integer isDelete);
}
