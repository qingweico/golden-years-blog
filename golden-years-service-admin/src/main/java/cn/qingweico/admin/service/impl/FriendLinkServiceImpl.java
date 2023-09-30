package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.model.mo.FriendLink;
import cn.qingweico.admin.repository.FriendLinkRepository;
import cn.qingweico.admin.service.FriendLinkService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.redis.RedisCache;
import cn.qingweico.util.redis.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 友情连接Service
 *
 * @author zqw
 * @date 2021/9/10
 * {@link MongoRepository}
 * {@link MongoTemplate}
 */
@Service
public class FriendLinkServiceImpl extends BaseService implements FriendLinkService {
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private FriendLinkRepository repo;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private RedisCache redisCache;

    @Override
    public void saveOrUpdateFriendLink(FriendLink friendLink) {
        // 数据都会替换
        mongoTemplate.save(friendLink);
    }

    @Override
    public PagedResult getFriendLinkList(String linkName, Integer iDelete, Integer page, Integer pageSize) {
        // start 0 not 1
        Pageable pageable = PageRequest.of(--page, pageSize);
        FriendLink fl = new FriendLink();
        fl.setLinkName(linkName);
        fl.setDeleteFlag(iDelete);
        // 条件查询

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher(SysConst.LINK_NAME, ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher(SysConst.IS_DELETE, ExampleMatcher.GenericPropertyMatchers.exact());

        Example<FriendLink> ex = Example.of(fl, exampleMatcher);
        Page<FriendLink> friendLinkPage = repo.findAll(ex, pageable);
        PagedResult pgr = new PagedResult();
        pgr.setRows(friendLinkPage.getContent());
        pgr.setRecords(friendLinkPage.getTotalElements());
        pgr.setCurrentPage(friendLinkPage.getNumber());
        pgr.setTotalPage(friendLinkPage.getTotalPages());
        return pgr;
    }

    @Override
    public void delete(String linkId) {
        repo.deleteById(linkId);
        redisUtil.clearCache(RedisConst.REDIS_FRIEND_LINK);
    }

    @Override
    public void deleteAll(List<String> ids) {
        for (String id : ids) {
            repo.deleteById(id);
        }
        redisUtil.clearCache(RedisConst.REDIS_FRIEND_LINK);
    }

    @Override
    public List<FriendLink> queryIndexFriendLinkList(Integer isDelete) {
        String cacheFriendLinkKey = RedisConst.REDIS_FRIEND_LINK;
        String cache = redisCache.get(cacheFriendLinkKey);
        List<FriendLink> friendLinkList;
        if (StringUtils.isBlank(cache)) {
            friendLinkList = repo.getAllByDeleteFlag(isDelete);
            redisCache.set(cacheFriendLinkKey, JsonUtils.objectToJson(friendLinkList));
        } else {
            friendLinkList = JsonUtils.jsonToList(cache, FriendLink.class);
        }
        return friendLinkList;
    }
}
