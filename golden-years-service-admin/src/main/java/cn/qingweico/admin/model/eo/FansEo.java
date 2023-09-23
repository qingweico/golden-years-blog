package cn.qingweico.admin.model.eo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;


/**
 * @author zqw
 * @date 2021/9/19
 */
@Data
@Document(indexName = "fans", type = "_doc")
public class FansEo {
    private String id;
    @Field
    private String authorId;
    @Field
    private String fanId;
    @Field
    private String face;
    @Field
    private String fanNickname;
    @Field
    private Integer sex;
    @Field
    private String province;
}
