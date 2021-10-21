package cn.qingweico.util;

import java.util.List;

/**
 * 用来返回分页Grid的数据格式
 *
 * @author:qiming
 * @date: 2021/9/9
 */
public class PagedGridResult {

    private int page;            // 当前页数
    private long total;          // 总页数
    private long records;        // 总记录数
    private List<?> rows;        // 每行显示的内容

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
