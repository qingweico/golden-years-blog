package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.repository.FriendLinkRepository;
import cn.qingweico.admin.service.FriendLinkService;
import cn.qingweico.pojo.mo.FriendLink;
import cn.qingweico.util.PagedGridResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    @Resource
    private FriendLinkRepository repo;

    @Resource
    private MongoTemplate mongoTemplate;


    @Override
    public void saveOrUpdateFriendLink(FriendLink friendLink) {
        // 数据都会替换
        repo.save(friendLink);
    }

    @Override
    public PagedGridResult getFriendLinkList(Integer page, Integer pageSize) {
        // start 0 not 1
        Pageable pageable = PageRequest.of(--page, pageSize);
        Page<FriendLink> friendLinkPage = repo.findAll(pageable);
        PagedGridResult pgr = new PagedGridResult();
        pgr.setRows(friendLinkPage.getContent());
        pgr.setRecords(friendLinkPage.getTotalElements());
        pgr.setCurrentPage(friendLinkPage.getNumber());
        pgr.setTotalPage(friendLinkPage.getTotalPages());
        return pgr;
    }

    @Override
    public void delete(String linkId) {
        repo.deleteById(linkId);
    }

    @Override
    public List<FriendLink> queryAllFriendLinkList(Integer isDelete) {
        return repo.getAllByIsDelete(isDelete);
    }
}
