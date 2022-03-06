package cn.qingweico.article.mapper;

import cn.qingweico.mapper.MyMapper;
import cn.qingweico.pojo.Comments;
import cn.qingweico.pojo.Tag;
import cn.qingweico.pojo.vo.CommentsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zqw
 * @date 2021/9/13
 */
@Repository
public interface TagMapper extends MyMapper<Tag> {

}