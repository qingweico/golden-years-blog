package cn.qingweico.user.handler;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zqw
 * @date 2021/10/23
 */
@Slf4j
public class DefaultHandler {
    public static GraceJsonResult reqFrequentError(BlockException e) {
        log.warn("{}", e.getMessage());
        return new GraceJsonResult(ResponseStatusEnum.SYSTEM_REQUEST_REFUSE);
    }
}
