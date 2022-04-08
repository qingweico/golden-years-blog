package cn.qingweico.user.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.pojo.UserLoginLog;
import cn.qingweico.user.mapper.UserLoginLogMapper;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.util.AddressUtil;
import cn.qingweico.util.PagedGridResult;
import cn.qingweico.util.ServletReqUtils;
import com.github.pagehelper.PageHelper;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Sid;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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

    @Resource
    private Sid sid;

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
        String id = sid.nextShort();
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
    public PagedGridResult getLoginLogList(String userId,
                                           Integer page,
                                           Integer pageSize) {

        Example loginLogExample = new Example(UserLoginLog.class);
        loginLogExample.orderBy("loginTime").desc();
        Example.Criteria criteria = loginLogExample.createCriteria();
        criteria.andEqualTo("userId");
        Calendar c = Calendar.getInstance();

        // 只展示用户最近一周的登陆日志
        c.setTime(new Date());
        c.add(Calendar.DATE, - 7);
        criteria.andGreaterThanOrEqualTo("loginTime", c.getTime());
        PageHelper.startPage(page, pageSize);
        List<UserLoginLog> userLoginLogList = userLoginLogMapper.selectByExample(loginLogExample);
        return setterPagedGrid(userLoginLogList, page);
    }

    @Override
    public void cleanLoginLog() {
        Example loginLogExample = new Example(UserLoginLog.class);
        Example.Criteria criteria = loginLogExample.createCriteria();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        criteria.andLessThanOrEqualTo("loginTime", c.getTime());
        userLoginLogMapper.deleteByExample(loginLogExample);
    }

}
