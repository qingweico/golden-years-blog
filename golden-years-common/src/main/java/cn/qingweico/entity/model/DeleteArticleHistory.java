package cn.qingweico.entity.model;

import cn.qingweico.enums.ArticleHistoryDeleteType;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2022/5/6
 */
@Data
public class DeleteArticleHistory {
    @NotBlank
    private String userId;
    /**
     * {@link ArticleHistoryDeleteType}
     */
    private Integer deleteModel;
}
