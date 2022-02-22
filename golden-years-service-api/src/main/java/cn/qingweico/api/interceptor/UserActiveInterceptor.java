package cn.qingweico.api.interceptor;

import cn.qingweico.enums.UserStatus;
import cn.qingweico.pojo.AppUser;
import cn.qingweico.exception.GraceException;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * 用户激活状态拦截器
 *  *    用户发表文章、修改文章以及删除文章
 *  *    发表评论和查看评论需要在用户激活状态下进行
 *  *    否则强制用户前往[账号设置]出完善账号信息
 *
 * @author:qiming
 * @date: 2021/9/7
 */

public class UserActiveInterceptor extends BaseInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {

        String userId = request.getHeader("headUserId");
        AppUser user;
        String jsonUser = redisOperator.get(REDIS_USER_INFO + ":" + userId);
        if (StringUtils.isNotBlank(jsonUser)) {
            user = JsonUtils.jsonToPojo(jsonUser, AppUser.class);
        } else {
            GraceException.display(ResponseStatusEnum.UN_LOGIN);
            return false;
        }
        if (user != null) {
            if (user.getActiveStatus() == null || !UserStatus.ACTIVE.type.equals(user.getActiveStatus())) {
                GraceException.display(ResponseStatusEnum.USER_INACTIVE_ERROR);
                return false;
            }
        }
        return true;
    }
}