package cn.qingweico.article.service.impl;

import cn.qingweico.article.mapper.TagMapper;
import cn.qingweico.article.service.TagService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.entity.Tag;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.SnowflakeIdWorker;
import cn.qingweico.util.redis.RedisCache;
import cn.qingweico.util.redis.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private RedisUtil redisUtil;

    @Resource
    private RedisCache redisCache;

    @Override
    public PagedResult getTagList(String tagName,
                                  Integer status,
                                  Integer sys,
                                  Integer page, Integer pageSize) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Tag::getCreateTime);
        if (StringUtils.isNotEmpty(tagName)) {
            wrapper.like(Tag::getName, SysConst.DELIMITER_PERCENT + tagName + SysConst.DELIMITER_PERCENT);
        }
        if (status != null) {
            wrapper.eq(Tag::getStatus, status);
        }
        if (sys != null) {
            wrapper.eq(Tag::getSys, sys);
        }
        // TODO 分页
        List<Tag> tags = tagMapper.selectList(wrapper);
        return setterPagedGrid(tags, page);
    }

    @Override
    public List<Tag> getTagList() {
        String tagJson = redisCache.get(RedisConst.REDIS_ARTICLE_TAG);
        List<Tag> tags;
        if (StringUtils.isEmpty(tagJson)) {
            LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tag::getStatus, YesOrNo.YES.getVal());
            tags = tagMapper.selectList(wrapper);
            redisCache.set(RedisConst.REDIS_ARTICLE_TAG, JsonUtils.objectToJson(tags));
        } else {
            tags = JsonUtils.jsonToList(tagJson, Tag.class);
        }
        return tags;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveOrUpdate(Tag param) {
        Tag tag = new Tag();
        if (StringUtils.isNotEmpty(param.getId())) {
            // 修改操作
            BeanUtils.copyProperties(param, tag);
            tag.setId(param.getId());
            tag.setUpdateTime(DateUtils.nowDateTime());
            if (tagMapper.updateById(tag) > 0) {
                String keys = RedisConst.REDIS_ARTICLE_TAG;
                redisUtil.clearCache(keys);
            }
        } else {
            // 添加操作
            String id = SnowflakeIdWorker.nextId();
            BeanUtils.copyProperties(param, tag);
            tag.setId(id);
            tag.setSys(YesOrNo.YES.getVal());
            setDefault(tag);
            if (tagMapper.insert(tag) > 0) {
                String keys = RedisConst.REDIS_ARTICLE_TAG;
                redisUtil.clearCache(keys);
            }
        }
    }

    @Override
    public void delete(String tagId) {
        if (tagMapper.deleteById(tagId) > 0) {
            String keys = RedisConst.REDIS_ARTICLE_TAG;
            redisUtil.clearCache(keys);
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void batchDelete(List<String> ids) {
        for (String id : ids) {
            tagMapper.deleteById(id);
        }
        String keys = RedisConst.REDIS_ARTICLE_TAG;
        redisUtil.clearCache(keys);
    }

    @Override
    public void deletePersonalTag() {

    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public String addPersonalTag(Tag tag, String userId) {
        Tag personalTag = new Tag();
        String tagId = SnowflakeIdWorker.nextId();
        personalTag.setId(tagId);
        personalTag.setUserId(userId);
        personalTag.setName(tag.getName());
        personalTag.setStatus(YesOrNo.YES.getVal());
        personalTag.setSys(YesOrNo.NO.getVal());
        personalTag.setColor(tag.getColor());
        setDefault(personalTag);
        if (tagMapper.insert(personalTag) > 0) {
            String keys = RedisConst.REDIS_ARTICLE_TAG;
            redisUtil.clearCache(keys);
        }
        return tagId;
    }

    private void setDefault(Tag tag) {
        tag.setCreateTime(DateUtils.nowDateTime());
        tag.setUpdateTime(DateUtils.nowDateTime());
    }
}
