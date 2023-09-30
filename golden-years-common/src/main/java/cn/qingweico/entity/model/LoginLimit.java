package cn.qingweico.entity.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zqw
 * @date 2023/9/30
 */
@Data
@AllArgsConstructor
public class
LoginLimit {
    private int limitPeriod;private int limitCount;
}
