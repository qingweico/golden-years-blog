package cn.qingweico.user.service.impl;

import cn.qingweico.entity.Fans;
import cn.qingweico.enums.Sex;
import cn.qingweico.exception.GraceException;
import cn.qingweico.global.RedisConst;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.user.entity.RegionRatioVO;
import cn.qingweico.user.mapper.FansMapper;
import cn.qingweico.user.service.FanService;
import cn.qingweico.util.PagedResult;
import cn.qingweico.util.SnowflakeIdWorker;
import cn.qingweico.util.redis.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/12
 */
@Service
public class FanServiceImpl extends ServiceImpl<FansMapper, Fans> implements FanService {

    @Resource
    private FansMapper fansMapper;
    @Resource
    private RedisCache redisCache;

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
    public Integer queryFansCounts(String authorId, Sex sex) {
        LambdaQueryWrapper<Fans> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Fans::getAuthorId, authorId);
        // TODO
        lqw.eq(Fans::getFanId, sex.getVal());
        return fansMapper.selectCount(lqw);
    }

    public static final String[] REGIONS = {"北京", "天津", "上海", "重庆", "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾", "内蒙古", "广西", "西藏", "宁夏", "新疆", "香港", "澳门"};

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
}
