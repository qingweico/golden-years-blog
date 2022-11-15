package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.repository.FriendLinkRepository;
import cn.qingweico.admin.service.FriendLinkService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.mo.FriendLink;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Service
public class FriendLinkServiceImpl extends BaseService implements FriendLinkService {

    @Resource
    private FriendLinkRepository repo;

    @Override
    public void saveOrUpdateFriendLink(FriendLink friendLink) {
        // 数据都会替换
        repo.save(friendLink);
    }

    @Override
    public PagedResult getFriendLinkList(String linkName, Integer iDelete, Integer page, Integer pageSize) {
        // start 0 not 1
        Pageable pageable = PageRequest.of(--page, pageSize);
        FriendLink fl = new FriendLink();
        fl.setLinkName(linkName);
        fl.setIsDelete(iDelete);
        // 条件查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("linkName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("isDelete", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<FriendLink> example = Example.of(fl, exampleMatcher);
        Page<FriendLink> friendLinkPage = repo.findAll(example, pageable);
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
        refreshCache(RedisConf.REDIS_FRIEND_LINK);
    }

    @Override
    public void deleteAll(List<String> ids) {
        for (String id : ids) {
            repo.deleteById(id);
        }
        refreshCache(RedisConf.REDIS_FRIEND_LINK);
    }

    @Override
    public List<FriendLink> queryIndexFriendLinkList(Integer isDelete) {
        String cacheFriendLinkKey = RedisConf.REDIS_FRIEND_LINK;
        String cache = redisTemplate.get(cacheFriendLinkKey);
        List<FriendLink> friendLinkList;
        if (StringUtils.isBlank(cache)) {
            friendLinkList = repo.getAllByIsDelete(isDelete);
            redisTemplate.set(cacheFriendLinkKey, JsonUtils.objectToJson(friendLinkList));
        } else {
            friendLinkList = JsonUtils.jsonToList(cache, FriendLink.class);
        }
        return friendLinkList;
    }
}
