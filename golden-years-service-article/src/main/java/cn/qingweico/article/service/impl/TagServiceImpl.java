package cn.qingweico.article.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.TagMapper;
import cn.qingweico.article.service.TagService;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConf;
import cn.qingweico.global.SysConf;
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
    public PagedGridResult getTagList(String tagName,
                                      Integer status,
                                      Integer sys,
                                      Integer page, Integer pageSize) {
        Example example = new Example(Tag.class);
        example.orderBy(SysConf.CREATE_TIME).desc();
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(tagName)) {
            criteria.andLike(SysConf.NAME, SysConf.DELIMITER_PERCENT + tagName + SysConf.DELIMITER_PERCENT);
        }
        if (status != null) {
            criteria.andEqualTo(SysConf.STATUS, status);
        }
        if (sys != null) {
            criteria.andEqualTo(SysConf.SYS, sys);
        }
        PageHelper.startPage(page, pageSize);
        List<Tag> tags = tagMapper.selectByExample(example);
        return setterPagedGrid(tags, page);
    }

    @Override
    public List<Tag> getTagList() {
        String tagJson = redisTemplate.get(RedisConf.REDIS_ARTICLE_TAG);
        List<Tag> tags;
        if (StringUtils.isBlank(tagJson)) {
            Example example = new Example(Tag.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(SysConf.STATUS, YesOrNo.YES.type);
            tags = tagMapper.selectByExample(example);
            redisTemplate.set(RedisConf.REDIS_ARTICLE_TAG, JsonUtils.objectToJson(tags));
            log.info("tag list has been cached");
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
                log.info("save or update(refreshCache): {}", keys);
            } else {
                log.error("update tag error");
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
                log.error("add tag error");
            }
        }
    }

    @Override
    public void delete(String tagId) {
        if (tagMapper.deleteByPrimaryKey(tagId) > 0) {
            String keys = RedisConf.REDIS_ARTICLE_TAG;
            refreshCache(keys);
        } else {
            log.error("delete tag error");
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void batchDelete(List<String> ids) {
        for (String id : ids) {
            tagMapper.deleteByPrimaryKey(id);
        }
        String keys = RedisConf.REDIS_ARTICLE_TAG;
        refreshCache(keys);
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
            log.info("insert personal tag(refreshCache): {}", keys);
        } else {
            log.error("addPersonalTag error");
        }
        return tagId;
    }

    private void setDefault(Tag tag) {
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
    }
}
