package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.AdminUserMapper;
import cn.qingweico.admin.service.AdminUserService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.exception.GraceException;
import cn.qingweico.grace.result.ResponseStatusEnum;
import cn.qingweico.pojo.AdminUser;
import cn.qingweico.pojo.bo.NewAdminBO;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/9
 */
@Service
public class AdminUserServiceImpl extends BaseService implements AdminUserService{

    @Resource
    private AdminUserMapper adminUserMapper;

    @Resource
    private Sid sid;


    @Override
    public AdminUser queryAdminByUsername(String username) {

        Example adminExample = new Example(AdminUser.class);
        Example.Criteria criteria = adminExample.createCriteria();
        criteria.andEqualTo("username", username);
        return adminUserMapper.selectOneByExample(adminExample);
    }

    @Transactional
    @Override
    public void createAdminUser(NewAdminBO newAdminBO) {
        String adminId = sid.nextShort();

        AdminUser admin = new AdminUser();
        admin.setId(adminId);
        admin.setUsername(newAdminBO.getUsername());
        admin.setAdminName(newAdminBO.getAdminName());

        // 如果密码不为空 则将密码加密入库
        if(StringUtils.isNotBlank(newAdminBO.getPassword())) {
            String encryptedPwd = BCrypt.hashpw(newAdminBO.getPassword(),BCrypt.gensalt());
            admin.setPassword(encryptedPwd);
        }

        // 如果人脸上传以后则获有FaceId 则需要将FaceId与admin信息相关联
        if(StringUtils.isNotBlank(newAdminBO.getFaceId())) {
            admin.setFaceId(newAdminBO.getFaceId());
        }
        admin.setCreatedTime(new Date());
        admin.setUpdatedTime(new Date());
        int res = adminUserMapper.insert(admin);
        if(res != 1) {
            GraceException.display(ResponseStatusEnum.ADMIN_CREATE_ERROR);
        }
    }

    @Override
    public PagedGridResult queryAdminList(Integer page, Integer pageSize) {
        Example adminExample = new Example(AdminUser.class);
        adminExample.orderBy("createdTime").desc();

        PageHelper.startPage(page, pageSize);
        List<AdminUser> adminUserList = adminUserMapper.selectByExample(adminExample);
        return setterPagedGrid(adminUserList, page);
    }
}
