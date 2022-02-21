package cn.qingweico.util;

import lombok.Data;

import java.util.List;

/**
 * 用来返回分页Grid的数据格式
 *
 * @author zqw
 * @date 2021/9/9
 */
@Data
public class PagedGridResult {
    /**
     * 当前页数
     */
    private int currentPage;
    /**
     * 总页数
     */
    private long totalPage;
    /**
     * 总记录数
     */
    private long records;
    /**
     * 每行显示的内容
     */
    private List<?> rows;
}
