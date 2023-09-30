package cn.qingweico.admin.model.mo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author zqw
 * @date 2022/4/2
 */
@Data
@Document("FriendLink")
public class FriendLink {
    @Id
    private String id;
    @Field("link_name")
    private String linkName;
    @Field("link_url")
    private String linkUrl;
    @Field("delete_flag")
    private Integer deleteFlag;
    @Field("create_time")
    private Date createTime;
    @Field("update_time")
    private Date updateTime;
}
