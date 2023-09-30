package cn.qingweico.user.service.impl;

import cn.qingweico.entity.Fans;
import cn.qingweico.entity.User;
import cn.qingweico.enums.Sex;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.result.Response;
import cn.qingweico.user.entity.FansCountsVO;
import cn.qingweico.user.entity.RegionRatioVO;
import cn.qingweico.user.mapper.FansMapper;
import cn.qingweico.user.service.FanService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.SnowflakeIdWorker;
import cn.qingweico.util.redis.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zqw
 * @date 2021/9/12
 */
@Service
public class FanServiceImpl extends ServiceImpl<FansMapper, Fans> implements FanService {

    @Resource
    private FansMapper fansMapper;
    @Resource
    private UserService userService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ElasticsearchTemplate esTemplate;

    @Override
    public boolean isMeFollowThisAuthor(String authorId, String fanId) {
        LambdaQueryWrapper<Fans> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Fans::getAuthorId, authorId).eq(Fans::getFanId, fanId);
        int count = fansMapper.selectCount(lqw);
        return count > 0;
    }

    @Override
    public void follow(String authorId, String fanId) {
        Fans fan = new Fans();
        fan.setId(SnowflakeIdWorker.nextId());
        fan.setAuthorId(authorId);
        fan.setFanId(fanId);
        if (fansMapper.insert(fan) > 0) {
            // redis 作者粉丝累增
            redisCache.increment(RedisConst.REDIS_AUTHOR_FANS_COUNTS + SysConst.SYMBOL_COLON + authorId, 1);
            // redis 当前用户的(我的)关注数累增
            redisCache.increment(RedisConst.REDIS_MY_FOLLOW_COUNTS + SysConst.SYMBOL_COLON + fanId, 1);
            // 保存粉丝关系到es中
            Fans fanElastic = new Fans();
            BeanUtils.copyProperties(fan, fanElastic);
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withObject(fanElastic)
                    .build();
            esTemplate.index(indexQuery);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }

    @Override
    public void unfollow(String authorId, String fanId) {
        LambdaQueryWrapper<Fans> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Fans::getFanId, fanId);
        lqw.eq(Fans::getFanId, fanId);
        if (fansMapper.delete(lqw) > 0) {
            // redis 作者粉丝累减
            redisCache.decrement(RedisConst.REDIS_AUTHOR_FANS_COUNTS + SysConst.SYMBOL_COLON + authorId, 1);
            // redis 当前用户的(我的)关注数累减
            redisCache.decrement(RedisConst.REDIS_MY_FOLLOW_COUNTS + SysConst.SYMBOL_COLON + fanId, 1);
            // 删除es中的粉丝关系
            DeleteQuery deleteQuery = new DeleteQuery();
            deleteQuery.setQuery(QueryBuilders.termQuery(SysConst.AUTHOR_ID, authorId));
            deleteQuery.setQuery(QueryBuilders.termQuery(SysConst.FAN_ID, fanId));
            esTemplate.delete(deleteQuery, Fans.class);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }

    }

    @Override
    public PagedResult getMyFansList(String authorId, Integer page, Integer pageSize) {

        // TODO 我的粉丝数据分页
        return null;
    }

    @Override
    public PagedResult getMyFansListFromElastic(String writerId,
                                          Integer page,
                                          Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termsQuery(SysConst.AUTHOR_ID, writerId))
                .withPageable(pageable)
                .build();

        AggregatedPage<Fans> pagedFans = esTemplate.queryForPage(searchQuery, Fans.class);
        PagedResult gridResult = new PagedResult();
        gridResult.setRows(pagedFans.getContent());
        gridResult.setTotalPage(pagedFans.getTotalPages());
        gridResult.setRecords(pagedFans.getTotalElements());
        gridResult.setCurrentPage(page + 1);
        return gridResult;
    }

    @Override
    public Integer queryFansCounts(String authorId, Sex sex) {
        LambdaQueryWrapper<Fans> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Fans::getAuthorId, authorId);
        // TODO
        lqw.eq(Fans::getFanId, sex.getVal());
        return fansMapper.selectCount(lqw);
    }

    @Override
    public FansCountsVO queryFansCountsFromElastic(String authorId) {

        TermsAggregationBuilder termBuilder = AggregationBuilders.terms("sex_counts")
                .field("sex");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery(SysConst.AUTHOR_ID, authorId))
                .addAggregation(termBuilder)
                .build();

        Aggregations agg = esTemplate.query(searchQuery, SearchResponse::getAggregations);

        Map<String, Aggregation> aggregationMap = agg.asMap();
        LongTerms longTerms = (LongTerms) aggregationMap.get("sex_counts");
        final List<LongTerms.Bucket> buckets = longTerms.getBuckets();
        FansCountsVO fansCountsVO = new FansCountsVO();
        if (buckets.size() == 0) {
            fansCountsVO.setManCounts(0);
            fansCountsVO.setWomanCounts(0);
            return fansCountsVO;
        }
        for (LongTerms.Bucket bucket : buckets) {
            // key
            Long key = (Long) bucket.getKey();
            // value
            long docCount = bucket.getDocCount();
            if (key.intValue() == Sex.WOMAN.getVal()) {
                fansCountsVO.setWomanCounts((int) docCount);
            } else if (key.intValue() == Sex.MAN.getVal()) {
                fansCountsVO.setManCounts((int) docCount);
            }
        }
        return fansCountsVO;
    }

    public static final String[] REGIONS = {
            "北京", "天津", "上海", "重庆", "河北",
            "山西", "辽宁", "吉林", "黑龙江", "江苏",
            "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "海南",
            "四川", "贵州", "云南", "陕西", "甘肃",
            "青海", "台湾", "内蒙古", "广西", "西藏",
            "宁夏", "新疆", "香港", "澳门"
    };

    @Override
    public List<RegionRatioVO> queryRatioByRegion(String authorId) {
        List<RegionRatioVO> regionRatioVOList = new ArrayList<>();
        LambdaQueryWrapper<Fans> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Fans::getAuthorId, authorId);
        for (String region : REGIONS) {
            // TODO 添加省份
            int count = fansMapper.selectCount(lqw);
            RegionRatioVO regionRatioVO = new RegionRatioVO();
            regionRatioVO.setName(region);
            regionRatioVO.setValue(count);
            regionRatioVOList.add(regionRatioVO);
        }
        return regionRatioVOList;

    }

    @Override
    public List<RegionRatioVO> queryRatioByRegionFromElastic(String authorId) {
        TermsAggregationBuilder termBuilder = AggregationBuilders.terms("region_counts")
                .field("province");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery(SysConst.AUTHOR_ID, authorId))
                .addAggregation(termBuilder)
                .build();

        Aggregations agg = esTemplate.query(searchQuery, SearchResponse::getAggregations);

        Map<String, Aggregation> aggregationMap = agg.asMap();
        StringTerms stringTerms = (StringTerms) aggregationMap.get("region_counts");
        final List<StringTerms.Bucket> buckets = stringTerms.getBuckets();

        List<RegionRatioVO> regionRatioVOList = new ArrayList<>();
        for (StringTerms.Bucket bucket : buckets) {
            // key
            String key = (String) bucket.getKey();
            // value
            long docCount = bucket.getDocCount();

            RegionRatioVO regionRatioVO = new RegionRatioVO();
            regionRatioVO.setName(key);
            regionRatioVO.setValue((int) docCount);
            regionRatioVOList.add(regionRatioVO);
        }
        return regionRatioVOList;
    }
}
