package cn.qingweico.entity.model;


import lombok.Data;

/**
 * @author zqw
 * @date 2021/9/7
 */
@Data
public class UserBasicInfo {
    private String id;
    private String nickname;
    private String face;;
    private Integer myFollowCounts;
    private Integer myFansCounts;
}
