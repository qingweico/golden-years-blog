package cn.qingweico.admin.model.bo;

import cn.qingweico.enums.ArticleHistoryDeleteType;
import lombok.Data;

/**
 * @author zqw
 * @date 2022/5/6
 */
@Data
public class DeleteArticleHistory {
    private String userId;
    /**
     * {@link ArticleHistoryDeleteType}
     */
    private Integer deleteModel;
}
