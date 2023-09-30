package cn.qingweico.article.mapper;

import cn.qingweico.entity.Comments;
import cn.qingweico.entity.model.CommentsTree;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Repository
public interface CommentsMapper extends BaseMapper<Comments> {

    /**
     * 查询文章评论列表
     *
     * @param map Map
     * @return List<CommentsVO>
     */
    List<CommentsTree> queryArticleCommentList(@Param("paramMap") Map<String, Object> map);
}
