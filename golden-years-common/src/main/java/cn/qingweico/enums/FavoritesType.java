package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 收藏夹可见状态
 *
 * @author zqw
 * @date 2022/4/15
 */
@AllArgsConstructor
public enum FavoritesType {
    /**
     * 公开
     */
    PUBLIC(1, "公开"),
    /**
     * 私密
     */
    SECRET(0, "私密");
    public final Integer type;
    public final String value;
}
