package cn.qingweico.admin.model.vo;

import lombok.Data;

/**
 * @author zqw
 * @date 2021/9/7
 */
@Data
public class UserBasicInfoVO {
   private String id;
   private String nickname;
   private String face;
   private Integer activeStatus;
   private Integer myFollowCounts;
   private Integer myFansCounts;
}
