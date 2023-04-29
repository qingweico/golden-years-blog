package cn.qingweico.entity.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zqw
 * @date 2023/4/29
 */
@Data
public class RedisData {

    Object data;
    LocalDateTime date;

    LocalDateTime expireTime;
}
