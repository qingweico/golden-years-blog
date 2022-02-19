package cn.qingweico.pojo.eo;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.persistence.Id;

/**
 * @author:qiming
 * @date: 2021/9/19
 */
@Document(indexName = "fans", type = "_doc")
public class FansEO {
    @Id
    private String id;
    @Field
    private String writerId;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getFanId() {
        return fanId;
    }

    public void setFanId(String fanId) {
        this.fanId = fanId;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getFanNickname() {
        return fanNickname;
    }

    public void setFanNickname(String fanNickname) {
        this.fanNickname = fanNickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}