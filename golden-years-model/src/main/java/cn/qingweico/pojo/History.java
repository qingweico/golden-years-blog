package cn.qingweico.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户浏览历史
 *
 * @author zqw
 * @date 2022/4/15
 */
@Data
@Table(name = "t_history")
public class History {

    @Id
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 文章id
     */
    private String articleId;

    /**
     * 浏览时间
     */
    private Date createTime;
}
