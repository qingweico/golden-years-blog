package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.SysUserMapper;
import cn.qingweico.admin.model.bo.OperatorSysUserBO;
import cn.qingweico.admin.service.SysUserService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.entity.SysUser;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.util.AddressUtil;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.SnowflakeIdWorker;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class SysUserServiceImpl extends BaseService implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser querySysUserByUsername(String username) {
        Example ex = new Example(SysUser.class);
        Example.Criteria criteria = ex.createCriteria();
        criteria.andEqualTo(SysConst.USERNAME, username);
        return sysUserMapper.selectOneByExample(ex);
    }

    @Override
    public SysUser querySysUserById(String id) {
        return sysUserMapper.selectByPrimaryKey(id);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void createSysUser(OperatorSysUserBO operatorSysUserBO) {
        String id = SnowflakeIdWorker.nextId();
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setUsername(operatorSysUserBO.getUsername());

        // 如果密码不为空, 则将密码加密入库
        if (StringUtils.isNotBlank(operatorSysUserBO.getPassword())) {
            String encryptedPwd = BCrypt.hashpw(operatorSysUserBO.getPassword(), BCrypt.gensalt());
            sysUser.setPassword(encryptedPwd);
        }

        // 如果人脸上传以后则获有FaceId, 则需要将FaceId与SysUser信息相关联
        if (StringUtils.isNotBlank(operatorSysUserBO.getFaceId())) {
            sysUser.setFaceId(operatorSysUserBO.getFaceId());
        }
        sysUser.setCreateTime(new Date());
        sysUser.setUpdateTime(new Date());
        int res = sysUserMapper.insert(sysUser);
        if (res != 1) {
            GraceException.error(Response.SYS_USER_CREATE_ERROR);
        }
    }

    @Override
    public PagedResult querySysUserList(Integer page, Integer pageSize) {
        Example ex = new Example(SysUser.class);
        ex.orderBy(SysConst.CREATE_TIME).desc();
        PageHelper.startPage(page, pageSize);
        List<SysUser> sysUserUserList = sysUserMapper.selectByExample(ex);
        return setterPagedGrid(sysUserUserList, page);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void alterPwd(String id, String newPassword) {
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setPassword(newPassword);
        sysUserMapper.updateByPrimaryKeySelective(sysUser);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void updateSysUserProfile(OperatorSysUserBO user) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(user, sysUser);
        sysUser.setUpdateTime(new Date());
        if (sysUserMapper.updateByPrimaryKeySelective(sysUser) > 0) {
            // 更新缓存
            sysUser = querySysUserByUsername(user.getUsername());
            refreshCache(sysUser);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void checkUsernamePresent(String username) {
        SysUser sysUser = querySysUserByUsername(username);
        if (sysUser != null) {
            GraceException.error(Response.SYS_USERNAME_EXIST_ERROR);
        }
    }

    @Override
    @Async("asyncTask")
    public void doSaveToken(SysUser sysUser, String token) {
        redisCache.set(RedisConst.REDIS_ADMIN_TOKEN + SysConst.SYMBOL_COLON + sysUser.getId(), token);
        redisCache.set(RedisConst.REDIS_ADMIN_INFO + SysConst.SYMBOL_COLON + sysUser.getId(), JsonUtils.objectToJson(sysUser));
    }

    @Override
    public void doSaveLoginLog(String id) {
        SysUser sysUser = new SysUser();
        sysUser.setId(id);
        sysUser.setLastLoginTime(new Date());
        String[] str = AddressUtil.getRealAddress().split(SysConst.SYMBOL_COMMA);
        // 获取登陆ip
        String ip = str[0];
        // 获取登陆位置
        String address = str[1];
        sysUser.setLastLoginIp(ip);
        sysUser.setLastLoginLocation(address);
        sysUserMapper.updateByPrimaryKeySelective(sysUser);
    }

    @Async("asyncTask")
    public void refreshCache(SysUser sysUser) {
        String key = RedisConst.REDIS_ADMIN_INFO + SysConst.SYMBOL_COLON + sysUser.getId();
        redisCache.set(key, JsonUtils.objectToJson(sysUser));
    }
}
