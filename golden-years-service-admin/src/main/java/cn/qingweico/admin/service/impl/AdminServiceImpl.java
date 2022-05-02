package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.AdminMapper;
import cn.qingweico.admin.service.AdminService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Admin;
import cn.qingweico.pojo.bo.OperatorAdminBO;
import cn.qingweico.util.*;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/9
 */
@Slf4j
@Service
public class AdminServiceImpl extends BaseService implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private Sid sid;


    @Override
    public Admin queryAdminByUsername(String username) {
        Example adminExample = new Example(Admin.class);
        Example.Criteria criteria = adminExample.createCriteria();
        criteria.andEqualTo("username", username);
        return adminMapper.selectOneByExample(adminExample);
    }

    @Override
    public Admin queryAdminById(String id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createAdminUser(OperatorAdminBO operatorAdminBO) {
        String adminId = sid.nextShort();
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setUsername(operatorAdminBO.getUsername());

        // 如果密码不为空, 则将密码加密入库
        if (StringUtils.isNotBlank(operatorAdminBO.getPassword())) {
            String encryptedPwd = BCrypt.hashpw(operatorAdminBO.getPassword(), BCrypt.gensalt());
            admin.setPassword(encryptedPwd);
        }

        // 如果人脸上传以后则获有FaceId, 则需要将FaceId与admin信息相关联
        if (StringUtils.isNotBlank(operatorAdminBO.getFaceId())) {
            admin.setFaceId(operatorAdminBO.getFaceId());
        }
        admin.setCreateTime(new Date());
        admin.setUpdateTime(new Date());
        int res = adminMapper.insert(admin);
        if (res != 1) {
            GraceException.error(ResponseStatusEnum.ADMIN_CREATE_ERROR);
        }
    }

    @Override
    public PagedGridResult queryAdminList(Integer page, Integer pageSize) {
        Example adminExample = new Example(Admin.class);
        adminExample.orderBy("createTime").desc();
        PageHelper.startPage(page, pageSize);
        List<Admin> adminUserList = adminMapper.selectByExample(adminExample);
        return setterPagedGrid(adminUserList, page);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void alterPwd(String id, String newPassword) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setPassword(newPassword);
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void updateUserProfile(OperatorAdminBO user) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(user, admin);
        admin.setUpdateTime(new Date());
        if (adminMapper.updateByPrimaryKeySelective(admin) > 0) {
            // 更新缓存
            admin = queryAdminByUsername(user.getUsername());
            refreshCache(admin);
        } else {
            log.error("admin update profile error");
            GraceException.error(ResponseStatusEnum.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void checkUsernameIsPresent(String username) {
        Admin admin = queryAdminByUsername(username);
        if (admin != null) {
            GraceException.error(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }

    @Override
    @Async("asyncTask")
    public void doSaveToken(Admin admin, String token) {
        redisOperator.set(RedisConf.REDIS_ADMIN_TOKEN + SysConf.SYMBOL_COLON + admin.getId(), token);
        redisOperator.set(RedisConf.REDIS_ADMIN_INFO + SysConf.SYMBOL_COLON + admin.getId(), JsonUtils.objectToJson(admin));
    }

    @Override
    public void doSaveLoginLog(String id) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setLastLoginTime(new Date());
        String[] str = AddressUtil.getRealAddress().split(",");
        // 获取登陆ip
        String ip = str[0];
        // 获取登陆位置
        String address = str[1];
        admin.setLastLoginIp(ip);
        admin.setLastLoginLocation(address);
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    @Async("asyncTask")
    public void refreshCache(Admin admin) {
        String key = RedisConf.REDIS_ADMIN_INFO + SysConf.SYMBOL_COLON + admin.getId();
        redisOperator.set(key, JsonUtils.objectToJson(admin));
    }
}
