package cn.qingweico.user.handler;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * @author:qiming
 * @date: 2021/10/23
 */
public class DefaultHandler {
    public static GraceJsonResult reqFrequentError(BlockException e) {
        e.printStackTrace(System.out);
        return new GraceJsonResult(ResponseStatusEnum.SYSTEM_REQUEST_REFUSE);
    }
}
