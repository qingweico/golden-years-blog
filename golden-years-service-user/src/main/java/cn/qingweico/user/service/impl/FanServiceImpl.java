package cn.qingweico.user.service.impl;

import cn.qingweico.api.service.BaseService;
import cn.qingweico.enums.Sex;
import cn.qingweico.pojo.AppUser;
import cn.qingweico.pojo.Fans;
import cn.qingweico.pojo.eo.FansEO;
import cn.qingweico.pojo.vo.FansCountsVO;
import cn.qingweico.pojo.vo.RegionRatioVO;
import cn.qingweico.user.mapper.FansMapper;
import cn.qingweico.user.service.FanService;
import cn.qingweico.user.service.UserService;
import cn.qingweico.util.PagedGridResult;
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
import org.n3r.idworker.Sid;
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
 * @author:qiming
 * @date: 2021/9/12
 */
@Service
public class FanServiceImpl extends BaseService implements FanService {

    @Resource
    private FansMapper fansMapper;

    @Resource
    private UserService userService;

    @Resource
    private Sid sid;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public boolean isMeFollowThisWriter(String writerId, String fanId) {

        Fans fans = new Fans();
        fans.setWriterId(writerId);
        fans.setFanId(fanId);
        int count = fansMapper.selectCount(fans);
        return count > 0;
    }

    @Override
    public void follow(String writerId, String fanId) {

        // 获得粉丝信息
        AppUser fansInfo = userService.queryUserById(fanId);
        Fans fan = new Fans();
        fan.setId(sid.nextShort());
        fan.setWriterId(writerId);
        fan.setFanId(fanId);

        fan.setFanNickname(fansInfo.getNickname());
        fan.setFace(fansInfo.getFace());
        fan.setSex(fansInfo.getSex());
        fan.setProvince(fansInfo.getProvince());
        fansMapper.insert(fan);

        // redis 作家粉丝累增

        redisOperator.increment(REDIS_WRITER_FANS_COUNTS + ":" + writerId, 1);

        // redis 当前用户的(我的)关注数累增

        redisOperator.increment(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);

        // 保存粉丝关系到es中
        FansEO fanEO = new FansEO();
        BeanUtils.copyProperties(fan, fanEO);
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withObject(fanEO)
                .build();
        elasticsearchTemplate.index(indexQuery);
    }

    @Override
    public void unfollow(String writerId, String fanId) {
        Fans fan = new Fans();
        fan.setWriterId(writerId);
        fan.setFanId(fanId);


        fansMapper.delete(fan);

        // redis 作家粉丝累减

        redisOperator.decrement(REDIS_WRITER_FANS_COUNTS + ":" + writerId, 1);

        // redis 当前用户的(我的)关注数累减

        redisOperator.decrement(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);

        // 删除es中的粉丝关系
        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(QueryBuilders.termQuery("writerId", writerId));
        deleteQuery.setQuery(QueryBuilders.termQuery("fanId", fanId));
        elasticsearchTemplate.delete(deleteQuery, FansEO.class);

    }

    @Override
    public PagedGridResult getMyFansList(String writerId, Integer page, Integer pageSize) {

        Fans fan = new Fans();
        fan.setWriterId(writerId);
        List<Fans> fansList = fansMapper.select(fan);
        PageHelper.startPage(page, pageSize);
        return setterPagedGrid(fansList, page);
    }

    @Override
    public PagedGridResult getMyFansListViaEs(String writerId,
                                              Integer page,
                                              Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termsQuery("writerId", writerId))
                .withPageable(pageable)
                .build();

        AggregatedPage<FansEO> pagedFans = elasticsearchTemplate.queryForPage(searchQuery, FansEO.class);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(pagedFans.getContent());
        gridResult.setTotal(pagedFans.getTotalPages());
        gridResult.setRecords(pagedFans.getTotalElements());
        gridResult.setPage(page + 1);
        return gridResult;
    }

    @Override
    public Integer queryFansCounts(String writerId, Sex sex) {
        Fans fans = new Fans();
        fans.setWriterId(writerId);
        fans.setSex(sex.type);
        fansMapper.select(fans);

        return fansMapper.selectCount(fans);
    }

    @Override
    public FansCountsVO queryFansCountsViaEs(String writerId) {

        TermsAggregationBuilder termBuilder = AggregationBuilders.terms("sex_counts")
                .field("sex");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("writerId", writerId))
                .addAggregation(termBuilder)
                .build();

        Aggregations agg = elasticsearchTemplate.query(searchQuery, SearchResponse::getAggregations);

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

    public static final String[] regions = {
            "北京", "天津", "上海", "重庆", "河北",
            "山西", "辽宁", "吉林", "黑龙江", "江苏",
            "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "海南",
            "四川", "贵州", "云南", "陕西", "甘肃",
            "青海", "台湾", "内蒙古", "广西", "西藏",
            "宁夏", "新疆", "香港", "澳门"
    };

    @Override
    public List<RegionRatioVO> queryRatioByRegion(String writerId) {
        List<RegionRatioVO> regionRatioVOList = new ArrayList<>();
        Fans fans = new Fans();
        fans.setWriterId(writerId);
        for (String region : regions) {
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
    public List<RegionRatioVO> queryRatioByRegionViaEs(String writerId) {
        TermsAggregationBuilder termBuilder = AggregationBuilders.terms("region_counts")
                .field("province");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("writerId", writerId))
                .addAggregation(termBuilder)
                .build();

        Aggregations agg = elasticsearchTemplate.query(searchQuery, SearchResponse::getAggregations);

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
        AppUser user = userService.queryUserById(fanId);

        // update db
        Fans fan = new Fans();
        fan.setId(relationId);
        fan.setFanId(fanId);

        fan.setFanNickname(user.getNickname());
        fan.setFace(user.getFace());
        fan.setSex(user.getSex());
        fan.setProvince(user.getProvince());
        fansMapper.updateByPrimaryKeySelective(fan);

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
        elasticsearchTemplate.update(updateQuery);

    }
}
