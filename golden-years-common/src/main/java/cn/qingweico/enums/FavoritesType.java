package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收藏夹可见状态
 *
 * @author zqw
 * @date 2022/4/15
 */
@AllArgsConstructor
@Getter
public enum FavoritesType {
    /**
     * 公开
     */
    PUBLIC(1, "公开"),
    /**
     * 私密
     */
    SECRET(0, "私密");
    private final Integer val;
    private final String desc;
}
