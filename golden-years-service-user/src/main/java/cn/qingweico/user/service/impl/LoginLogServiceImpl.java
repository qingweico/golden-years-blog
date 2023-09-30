package cn.qingweico.user.service.impl;

import cn.qingweico.core.service.BaseService;
import cn.qingweico.entity.UserLoginLog;
import cn.qingweico.global.SysConst;
import cn.qingweico.user.mapper.UserLoginLogMapper;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.util.AddressUtil;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.ServletReqUtils;
import cn.qingweico.util.SnowflakeIdWorker;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/22
 */
@Slf4j
@Service
public class LoginLogServiceImpl extends BaseService implements LoginLogService {

    @Resource
    private UserLoginLogMapper userLoginLogMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void saveUserLoginLog(String userId) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletReqUtils.getRequest().getHeader("User-Agent"));
        String[] str = AddressUtil.getRealAddress().split(",");
        // 获取登陆ip
        String ip = str[0];
        // 获取登陆位置
        String address = str[1];
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        Date timestamp = new Date();
        String id = SnowflakeIdWorker.nextId();
        UserLoginLog userLoginLog = new UserLoginLog();
        userLoginLog.setId(id);
        userLoginLog.setUserId(userId);
        userLoginLog.setOs(os);
        userLoginLog.setIpaddr(ip);
        userLoginLog.setLoginLocation(address);
        userLoginLog.setBrowser(browser);
        userLoginLog.setLoginTime(timestamp);
        userLoginLogMapper.insert(userLoginLog);
    }
    @Override
    public PagedResult getLoginLogList(String userId,
                                       Integer page,
                                       Integer pageSize) {

        LambdaQueryWrapper<UserLoginLog> lwq = new LambdaQueryWrapper<>();
        lwq.orderByDesc(UserLoginLog::getLoginTime);
        lwq.eq(UserLoginLog::getUserId, userId);
        Calendar c = Calendar.getInstance();
        // 只展示用户最近一周的登陆日志
        c.setTime(new Date());
        c.add(Calendar.DATE, -7);
        lwq.ge(UserLoginLog::getLoginTime, c.getTime());
        // 分页 TODO
        List<UserLoginLog> userLoginLogList = userLoginLogMapper.selectList(lwq);
        return setterPagedGrid(userLoginLogList, page);
    }

    @Override
    public void cleanLoginLog() {
        LambdaQueryWrapper<UserLoginLog> lwq = new LambdaQueryWrapper<>();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        lwq.le(UserLoginLog::getLoginTime, c.getTime());
        userLoginLogMapper.delete(lwq);
    }
}
