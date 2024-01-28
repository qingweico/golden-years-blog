package cn.qingweico.core.service;

import cn.qingweico.util.PagedResult;

import java.util.List;

/**
 * @author zqw
 * @date 2021/9/10
 */
public class BaseService {
    public PagedResult setterPagedGrid(List<?> list,
                                       Integer page) {
        // 分页 TODO
        return new PagedResult();
    }
}
