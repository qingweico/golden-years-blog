package cn.qingweico.user.handler;

import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zqw
 * @date 2021/10/23
 */
@Slf4j
public class DefaultHandler {
    public static Result reqFrequentError(BlockException e) {
        log.warn("{}", e.getMessage());
        return Result.r(Response.SYSTEM_REQUEST_REFUSE);
    }
}
