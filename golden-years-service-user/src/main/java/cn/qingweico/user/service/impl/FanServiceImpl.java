package cn.qingweico.user.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.enums.Sex;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.pojo.User;
import cn.qingweico.pojo.Fans;
import cn.qingweico.pojo.eo.FansEo;
import cn.qingweico.pojo.vo.FansCountsVO;
import cn.qingweico.pojo.vo.RegionRatioVO;
import cn.qingweico.result.Response;
import cn.qingweico.user.mapper.FansMapper;
import cn.qingweico.user.service.FanService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.PagedResult;
import com.github.pagehelper.PageHelper;
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
public class FanServiceImpl extends BaseService implements FanService {

    @Resource
    private FansMapper fansMapper;

    @Resource
    private UserService userService;

    @Override
    public boolean isMeFollowThisAuthor(String authorId, String fanId) {

        Fans fans = new Fans();
        fans.setAuthor(authorId);
        fans.setFanId(fanId);
        int count = fansMapper.selectCount(fans);
        return count > 0;
    }

    @Override
    public void follow(String authorId, String fanId) {
        // 获得粉丝信息
        User fansInfo = userService.queryUserById(fanId);
        Fans fan = new Fans();
        fan.setId("");
        fan.setAuthor(authorId);
        fan.setFanId(fanId);

        fan.setFanNickname(fansInfo.getNickname());
        fan.setFace(fansInfo.getFace());
        fan.setSex(fansInfo.getSex());
        fan.setProvince(fansInfo.getProvince());
        if (fansMapper.insert(fan) > 0) {
            // redis 作者粉丝累增
            redisTemplate.increment(RedisConst.REDIS_AUTHOR_FANS_COUNTS + SysConst.SYMBOL_COLON + authorId, 1);
            // redis 当前用户的(我的)关注数累增
            redisTemplate.increment(RedisConst.REDIS_MY_FOLLOW_COUNTS + SysConst.SYMBOL_COLON + fanId, 1);
            // 保存粉丝关系到es中
            FansEo fanEo = new FansEo();
            BeanUtils.copyProperties(fan, fanEo);
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withObject(fanEo)
                    .build();
            esTemplate.index(indexQuery);
        } else {
            GraceException.error(Response.SYSTEM_ERROR);
        }
    }

    @Override
    public void unfollow(String authorId, String fanId) {
        Fans fan = new Fans();
        fan.setAuthor(authorId);
        fan.setFanId(fanId);
        if (fansMapper.delete(fan) > 0) {
            // redis 作者粉丝累减
            redisTemplate.decrement(RedisConst.REDIS_AUTHOR_FANS_COUNTS + SysConst.SYMBOL_COLON + authorId, 1);
            // redis 当前用户的(我的)关注数累减
            redisTemplate.decrement(RedisConst.REDIS_MY_FOLLOW_COUNTS + SysConst.SYMBOL_COLON + fanId, 1);
            // 删除es中的粉丝关系
            DeleteQuery deleteQuery = new DeleteQuery();
            deleteQuery.setQuery(QueryBuilders.termQuery(SysConst.AUTHOR_ID, authorId));
            deleteQuery.setQuery(QueryBuilders.termQuery(SysConst.FAN_ID, fanId));
            esTemplate.delete(deleteQuery, FansEo.class);
        } else {
            GraceException.error(Response.SYSTEM_ERROR);
        }

    }

    @Override
    public PagedResult getMyFansList(String authorId, Integer page, Integer pageSize) {
        Fans fan = new Fans();
        fan.setAuthor(authorId);
        List<Fans> fansList = fansMapper.select(fan);
        PageHelper.startPage(page, pageSize);
        return setterPagedGrid(fansList, page);
    }

    @Override
    public PagedResult getMyFansListViaEs(String writerId,
                                          Integer page,
                                          Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termsQuery(SysConst.AUTHOR_ID, writerId))
                .withPageable(pageable)
                .build();

        AggregatedPage<FansEo> pagedFans = esTemplate.queryForPage(searchQuery, FansEo.class);
        PagedResult gridResult = new PagedResult();
        gridResult.setRows(pagedFans.getContent());
        gridResult.setTotalPage(pagedFans.getTotalPages());
        gridResult.setRecords(pagedFans.getTotalElements());
        gridResult.setCurrentPage(page + 1);
        return gridResult;
    }

    @Override
    public Integer queryFansCounts(String authorId, Sex sex) {
        Fans fans = new Fans();
        fans.setAuthor(authorId);
        fans.setSex(sex.type);
        fansMapper.select(fans);
        return fansMapper.selectCount(fans);
    }

    @Override
    public FansCountsVO queryFansCountsViaEs(String authorId) {

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
            if (key.intValue() == Sex.WOMAN.type) {
                fansCountsVO.setWomanCounts((int) docCount);
            } else if (key.intValue() == Sex.MAN.type) {
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
        Fans fans = new Fans();
        fans.setAuthor(authorId);
        for (String region : REGIONS) {
            fans.setProvince(region);
            int count = fansMapper.selectCount(fans);
            RegionRatioVO regionRatioVO = new RegionRatioVO();
            regionRatioVO.setName(region);
            regionRatioVO.setValue(count);
            regionRatioVOList.add(regionRatioVO);
        }
        return regionRatioVOList;

    }

    @Override
    public List<RegionRatioVO> queryRatioByRegionViaEs(String authorId) {
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

    @Override
    public void passive(String relationId, String fanId) {
        // 根据fanId查询粉丝信息
        User user = userService.queryUserById(fanId);

        // update db
        Fans fan = new Fans();
        fan.setId(relationId);
        fan.setFanId(fanId);

        fan.setFanNickname(user.getNickname());
        fan.setFace(user.getFace());
        fan.setSex(user.getSex());
        fan.setProvince(user.getProvince());
        if (fansMapper.updateByPrimaryKeySelective(fan) > 0) {
            // update es

            Map<String, Object> updateMap = new HashMap<>(4);
            updateMap.put("nickname", user.getNickname());
            updateMap.put("faceId", user.getFace());
            updateMap.put("sex", user.getSex());
            updateMap.put("province", user.getProvince());

            IndexRequest indexRequest = new IndexRequest();
            indexRequest.source(updateMap);


            UpdateQuery updateQuery = new UpdateQueryBuilder()
                    .withClass(Fans.class)
                    .withId(relationId)
                    .withIndexRequest(indexRequest)
                    .build();
            esTemplate.update(updateQuery);
        } else {
            GraceException.error(Response.SYSTEM_OPERATION_ERROR);
        }
    }
}
