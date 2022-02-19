package cn.qingweico.user.service.impl;

import cn.qingweico.user.service.AppUserService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.enums.UserStatus;
import cn.qingweico.pojo.AppUser;
import cn.qingweico.user.mapper.AppUserMapper;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/11
 */
@Service
public class AppUserServiceImpl extends BaseService implements AppUserService {

    @Resource
    private AppUserMapper appUserMapper;
    @Override
    public PagedGridResult queryAllUserList(String nickname,
                                            Integer status,
                                            Date startDate,
                                            Date endDate,
                                            Integer page,
                                            Integer pageSize) {

        Example example = new Example(AppUser.class);
        example.orderBy("createdTime").desc();
        Example.Criteria criteria = example.createCriteria();

        if(StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", "%" + nickname + "%");
        }

        if(UserStatus.isUserStatusValid(status)) {
            criteria.andEqualTo("activeStatus", status);
        }

        if(startDate != null) {
            criteria.andGreaterThanOrEqualTo("createdTime", startDate);
        }
        if(endDate != null) {
            criteria.andGreaterThanOrEqualTo("createdTime", endDate);
        }

        PageHelper.startPage(page, pageSize);
        List<AppUser> appUserList = appUserMapper.selectByExample(example);
        return setterPagedGrid(appUserList, page);
    }

    @Transactional
    @Override
    public void freezeUserOrNot(String userId, Integer doStatus) {
        AppUser appUser = new AppUser();
        appUser.setId(userId);
        appUser.setActiveStatus(doStatus);
        appUserMapper.updateByPrimaryKeySelective(appUser);
    }
}
