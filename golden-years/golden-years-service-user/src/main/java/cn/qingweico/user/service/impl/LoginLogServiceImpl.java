package cn.qingweico.user.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.pojo.UserLoginLog;
import cn.qingweico.user.mapper.UserLoginLogMapper;
import cn.qingweico.user.service.LoginLogService;
import cn.qingweico.util.AddressUtil;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import eu.bitwalker.useragentutils.UserAgent;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/22
 */
@Service
public class LoginLogServiceImpl extends BaseService implements LoginLogService {

    @Resource
    private UserLoginLogMapper userLoginLogMapper;

    @Resource
    private Sid sid;

    @Override
    public void saveUserLoginLog(String userId) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(getRequest().getHeader("User-Agent"));
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

        PageHelper.startPage(page, pageSize);
        List<UserLoginLog> userLoginLogList = userLoginLogMapper.selectByExample(loginLogExample);
        return setterPagedGrid(userLoginLogList, page);
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

}
