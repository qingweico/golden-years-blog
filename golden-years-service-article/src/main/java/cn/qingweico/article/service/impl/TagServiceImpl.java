package cn.qingweico.article.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.TagMapper;
import cn.qingweico.article.service.TagService;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Tag;
import cn.qingweico.pojo.bo.TagBO;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedGridResult;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zqw
 * @date 2022/3/6
 */
@Slf4j
@Service
public class TagServiceImpl extends BaseService implements TagService {

    @Resource
    private TagMapper tagMapper;
    @Resource
    private Sid sid;

    @Override
    public PagedGridResult getTagList(Integer page, Integer pageSize) {
        Example example = new Example(Article.class);
        example.orderBy("createTime").desc();
        PageHelper.startPage(page, pageSize);
        List<Tag> tags = tagMapper.selectByExample(example);
        return setterPagedGrid(tags, page);
    }

    @Override
    public List<Tag> getTagList() {
        String tagJson = redisOperator.get(RedisConf.REDIS_ARTICLE_TAG);
        List<Tag> tags;
        if (StringUtils.isBlank(tagJson)) {
            Example example = new Example(Tag.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", YesOrNo.YES.type);
            criteria.andEqualTo("sys", YesOrNo.YES.type);
            tags = tagMapper.selectByExample(example);
            redisOperator.set(RedisConf.REDIS_ARTICLE_TAG, JsonUtils.objectToJson(tags));
            log.info("标签信息已存入缓存");
        } else {
            tags = JsonUtils.jsonToList(tagJson, Tag.class);
        }
        return tags;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveOrUpdate(TagBO tagBO) {
        Tag tag = new Tag();
        if (tagBO.getId() != null) {
            // 修改操作
            BeanUtils.copyProperties(tagBO, tag);
            tag.setId(tagBO.getId());
            tag.setUpdateTime(new Date());
            if (tagMapper.updateByPrimaryKeySelective(tag) > 0) {
                String keys = RedisConf.REDIS_ARTICLE_TAG;
                refreshCache(keys);
                log.info("saveOrUpdate(refreshCache): {}", keys);
            } else {
                log.error("更新标签失败");
            }
        } else {
            // 添加操作
            String id = sid.nextShort();
            BeanUtils.copyProperties(tagBO, tag);
            tag.setId(id);
            tag.setSys(YesOrNo.YES.type);
            setDefault(tag);
            if (tagMapper.insert(tag) > 0) {
                String keys = RedisConf.REDIS_ARTICLE_TAG;
                refreshCache(keys);
                log.info("insert(refreshCache): {}", keys);
            } else {
                log.error("新增标签失败");
            }
        }
    }

    @Override
    public void delete(String tagId) {
        tagMapper.deleteByPrimaryKey(tagId);
    }

    @Override
    public void deletePersonalTag() {

    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public String addPersonalTag(Tag tag, String userId) {
        Tag personalTag = new Tag();
        String tagId = sid.nextShort();
        personalTag.setId(tagId);
        personalTag.setUserId(userId);
        personalTag.setName(tag.getName());
        personalTag.setStatus(YesOrNo.YES.type);
        personalTag.setSys(YesOrNo.NO.type);
        personalTag.setColor(tag.getColor());
        setDefault(personalTag);
        if (tagMapper.insert(personalTag) > 0) {
            String keys = RedisConf.REDIS_ARTICLE_TAG;
            refreshCache(keys);
            log.info("insert(refreshCache): {}", keys);
        } else {
            log.error("新增标签失败");
        }
        return tagId;
    }

    private void setDefault(Tag tag) {
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
    }
}
