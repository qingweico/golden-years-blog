package cn.qingweico.article.service.impl;

import cn.qingweico.article.mapper.TagMapper;
import cn.qingweico.article.service.TagService;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Tag;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

;

/**
 * @author zqw
 * @date 2022/3/6
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Resource
    private RedisOperator redisOperator;
    @Override
    public List<Tag> getTagList() {
        String tagsJson = redisOperator.get(RedisConf.REDIS_ARTICLE_TAG);
        List<Tag> tags;
        if (StringUtils.isBlank(tagsJson)) {
            Example example = new Example(Tag.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", YesOrNo.YES.type);
            tags = tagMapper.selectByExample(example);
            redisOperator.set(RedisConf.REDIS_ARTICLE_TAG, JsonUtils.objectToJson(tags));
            log.info("标签信息已存入缓存");
        } else {
            tags = JsonUtils.jsonToList(tagsJson, Tag.class);
        }
        return tags;

    }

    @Override
    public void deletePersonalTag() {

    }

    @Override
    public void addPersonalTag() {

    }
}
