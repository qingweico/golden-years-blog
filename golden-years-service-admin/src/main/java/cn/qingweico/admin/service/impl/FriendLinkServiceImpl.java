package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.repository.FriendLinkRepository;
import cn.qingweico.admin.service.FriendLinkService;
import cn.qingweico.pojo.mo.FriendLinkMo;
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


    @Override
    public void saveOrUpdateFriendLink(FriendLinkMo friendLinkMo) {
        repo.save(friendLinkMo);

    }

    @Override
    public List<FriendLinkMo> queryAllFriendLinkList() {
        return repo.findAll();
    }

    @Override
    public void delete(String linkId) {
        repo.deleteById(linkId);
    }

    @Override
    public List<FriendLinkMo> queryAllFriendLinkList(Integer isDelete) {
        return repo.getAllByIsDelete(isDelete);
    }
}
