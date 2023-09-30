package cn.qingweico.entity.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zqw
 * @date 2022/4/15
 */
@Data
public class ArticleRelated {
    @NotBlank
    private String articleId;
    @NotBlank
    private String userId;
}
