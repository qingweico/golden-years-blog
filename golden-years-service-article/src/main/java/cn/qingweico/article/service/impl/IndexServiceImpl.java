package cn.qingweico.article.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.article.mapper.ArticleMapper;
import cn.qingweico.article.mapper.CommentsMapper;
import cn.qingweico.article.mapper.TagMapper;
import cn.qingweico.article.service.IndexService;
import cn.qingweico.enums.ArticleReviewStatus;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.global.SysConf;
import cn.qingweico.global.RedisConf;
import cn.qingweico.pojo.Article;
import cn.qingweico.pojo.Comments;
import cn.qingweico.pojo.Tag;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zqw
 * @date 2022/4/18
 */
@Service
public class IndexServiceImpl extends BaseService implements IndexService {
    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentsMapper commentsMapper;

    @Resource
    private TagMapper tagMapper;

    @Override
    public Integer getArticleCounts() {
        Article article = new Article();
        article.setArticleStatus(ArticleReviewStatus.SUCCESS.type);
        article.setIsDelete(YesOrNo.NO.type);
        return articleMapper.selectCount(article);
    }

    @Override
    public Integer getCommentCount() {
        Comments comment = new Comments();
        return commentsMapper.selectCount(comment);
    }

    @Override
    public List<Map<String, Object>> getBlogCountByTag() {
        // 从Redis中获取标签下包含的博客数量
        String jsonArrayList = redisOperator.get(RedisConf.DASHBOARD + SysConf.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);
        if (StringUtils.isNotEmpty(jsonArrayList)) {
            ArrayList jsonList = JsonUtils.jsonArrayToArrayList(jsonArrayList);
            return jsonList;
        }

        List<Map<String, Object>> blogCountByTagMap = articleMapper.getBlogCountByTag();
        Map<String, Integer> tagMap = new HashMap<>(blogCountByTagMap.size());
        for (Map<String, Object> item : blogCountByTagMap) {
            String tags = String.valueOf(item.get("tags"));
            tags = tags.replace("[", "")
                    .replace("]", "")
                    .replace("\"", "");
            Number num = (Number) item.get("count");
            Integer count = num.intValue();
            //如果只有一个标签的情况
            if (tags.length() == 16) {
                if (tagMap.get(tags) == null) {
                    tagMap.put(tags, count);
                } else {
                    Integer tempCount = tagMap.get(tags) + count;
                    tagMap.put(tags, tempCount);
                }
            } else {
                //如果长度大于16 说明该文章含有多个标签
                if (StringUtils.isNotEmpty(tags)) {
                    List<String> strList = changeStringToString(tags, ",");
                    for (String strItem : strList) {
                        if (tagMap.get(strItem) == null) {
                            tagMap.put(strItem, count);
                        } else {
                            Integer tempCount = tagMap.get(strItem) + count;
                            tagMap.put(strItem, tempCount);
                        }
                    }
                }
            }
        }

        //把查询到的Tag放到Map中
        Set<String> tagIds = tagMap.keySet();
        Collection<Tag> tagCollection = new ArrayList<>();
        if (tagIds.size() > 0) {
            for (String tagId : tagIds) {
                Tag tag = tagMapper.selectByPrimaryKey(tagId);
                tagCollection.add(tag);
            }
        }

        Map<String, String> tagEntityMap = new HashMap<>(tagCollection.size());
        for (Tag tag : tagCollection) {
            if (StringUtils.isNotEmpty(tag.getName())) {
                tagEntityMap.put(tag.getId(), tag.getName());
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            String tagUid = entry.getKey();
            if (tagEntityMap.get(tagUid) != null) {
                String tagName = tagEntityMap.get(tagUid);
                Integer count = entry.getValue();
                Map<String, Object> itemResultMap = new HashMap<>(3);
                itemResultMap.put(SysConf.TAG_ID, tagUid);
                itemResultMap.put(SysConf.NAME, tagName);
                itemResultMap.put(SysConf.VALUE, count);
                resultList.add(itemResultMap);
            }
        }
        if (resultList.size() > 0) {
            redisOperator.setnx(RedisConf.DASHBOARD + SysConf.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG, JsonUtils.objectToJson(resultList), 2, TimeUnit.HOURS);
        }
        return resultList;
    }

    @Override
    public Map<String, Object> getBlogContributeCount() {
        String jsonMap = redisOperator.get(RedisConf.DASHBOARD + SysConf.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT);
        if (StringUtils.isNotEmpty(jsonMap)) {
            return JsonUtils.jsonToMap(jsonMap, String.class, Object.class);
        }

        // 获取今天结束时间
        String endTime = DateUtils.getNowTime();
        // 获取365天前的日期
        Date temp = DateUtils.getDate(endTime, -365);
        String startTime = DateUtils.dateTimeToStr(temp);
        List<Map<String, Object>> blogContributeMap = articleMapper.getBlogContributeCount(startTime, endTime);
        List<String> dateList = DateUtils.getDayBetweenDates(startTime, endTime);
        Map<String, Object> dateMap = new HashMap<>(32);
        for (Map<String, Object> itemMap : blogContributeMap) {
            dateMap.put(itemMap.get("DATE").toString(), itemMap.get("COUNT"));
        }

        List<List<Object>> resultList = new ArrayList<>();
        for (String item : dateList) {
            int count = 0;
            if (dateMap.get(item) != null) {
                count = Integer.parseInt(dateMap.get(item).toString());
            }
            List<Object> objectList = new ArrayList<>();
            objectList.add(item);
            objectList.add(count);
            resultList.add(objectList);
        }

        Map<String, Object> resultMap = new HashMap<>(SysConf.NUM_TWO);
        List<String> contributeDateList = new ArrayList<>();
        contributeDateList.add(startTime);
        contributeDateList.add(endTime);
        resultMap.put(SysConf.CONTRIBUTE_DATE, contributeDateList);
        resultMap.put(SysConf.BLOG_CONTRIBUTE_COUNT, resultList);
        redisOperator.setnx(RedisConf.DASHBOARD + SysConf.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT, JsonUtils.objectToJson(resultMap), 2, TimeUnit.HOURS);
        return resultMap;
    }

    /**
     * 把字符串按code 转换为List<String>
     */
    public static List<String> changeStringToString(String str, String code) {
        String[] split = split(str, code);
        List<String> result = new ArrayList<>();
        Collections.addAll(result, split);
        return result;
    }

    /**
     * 按code截取字符串
     *
     * @return String[]
     */
    public static String[] split(String str, String code) {
        String[] split;
        if (StringUtils.isEmpty(str)) {
            split = null;
        } else {
            split = str.split(code);
        }
        return split;
    }
}
