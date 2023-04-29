package cn.qingweico.article.mapper;

import cn.qingweico.pojo.Comments;
import cn.qingweico.core.mapper.CommonMapper;
import cn.qingweico.pojo.vo.CommentsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Repository
public interface CommentsMapper extends CommonMapper<Comments> {

    /**
     * 查询文章评论列表
     *
     * @param map Map
     * @return List<CommentsVO>
     */
    List<CommentsVO> queryArticleCommentList(@Param("paramMap") Map<String, Object> map);
}
