package cn.qingweico.admin.repository;

import cn.qingweico.pojo.mo.FriendLinkMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
@Repository
public interface FriendLinkRepository extends MongoRepository<FriendLinkMO, String> {

    List<FriendLinkMO> getAllByIsDelete(Integer isDelete);
}
