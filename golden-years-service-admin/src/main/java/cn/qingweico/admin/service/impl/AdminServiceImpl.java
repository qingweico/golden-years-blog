package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.AdminMapper;
import cn.qingweico.admin.service.AdminService;
import cn.qingweico.api.service.BaseService;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.Constants;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.User;
import cn.qingweico.pojo.bo.UpdateAdminBO;
import cn.qingweico.pojo.bo.UpdatePwdBO;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.Admin;
import cn.qingweico.pojo.bo.NewAdminBO;
import cn.qingweico.util.*;
import com.github.pagehelper.PageHelper;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.repository.query.StringBasedMongoQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.support.RequestPartServletServerHttpRequest;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    public void createAdminUser(NewAdminBO newAdminBO) {
        String adminId = sid.nextShort();
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setUsername(newAdminBO.getUsername());

        // 如果密码不为空, 则将密码加密入库
        if (StringUtils.isNotBlank(newAdminBO.getPassword())) {
            String encryptedPwd = BCrypt.hashpw(newAdminBO.getPassword(), BCrypt.gensalt());
            admin.setPassword(encryptedPwd);
        }

        // 如果人脸上传以后则获有FaceId, 则需要将FaceId与admin信息相关联
        if (StringUtils.isNotBlank(newAdminBO.getFaceId())) {
            admin.setFaceId(newAdminBO.getFaceId());
        }
        admin.setCreateTime(new Date());
        admin.setUpdateTime(new Date());
        int res = adminMapper.insert(admin);
        if (res != 1) {
            GraceException.display(ResponseStatusEnum.ADMIN_CREATE_ERROR);
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
    public void updateUserProfile(UpdateAdminBO user) {
        Admin admin = new Admin();
        BeanUtils.copyProperties(user, admin);
        admin.setUpdateTime(new Date());
        if (adminMapper.updateByPrimaryKeySelective(admin) > 0) {
            // 更新缓存
            admin = queryAdminByUsername(user.getUsername());
            refreshCache(admin);
        }
    }

    @Override
    public void checkUsernameIsPresent(String username) {
        Admin admin = queryAdminByUsername(username);
        if (admin != null) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }

    @Override
    @Async("asyncTask")
    public void doSaveToken(Admin admin, String token) {
        redisOperator.set(RedisConf.REDIS_ADMIN_TOKEN + Constants.SYMBOL_COLON + admin.getId(), token);
        redisOperator.set(RedisConf.REDIS_ADMIN_INFO + Constants.SYMBOL_COLON + admin.getId(), JsonUtils.objectToJson(admin));
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
        String key = RedisConf.REDIS_ADMIN_INFO + Constants.SYMBOL_COLON + admin.getId();
        redisOperator.set(key, JsonUtils.objectToJson(admin));
    }
}
