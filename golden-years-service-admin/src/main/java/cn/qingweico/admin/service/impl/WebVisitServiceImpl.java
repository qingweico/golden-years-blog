package cn.qingweico.admin.service.impl;

import cn.qingweico.admin.mapper.WebVisitMapper;
import cn.qingweico.admin.service.WebVisitService;
import cn.qingweico.core.service.BaseService;
import cn.qingweico.global.SysConst;
import cn.qingweico.global.RedisConst;
import cn.qingweico.util.DateUtils;
import cn.qingweico.util.JsonUtils;
import cn.qingweico.util.redis.RedisCache;
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
public class WebVisitServiceImpl extends BaseService implements WebVisitService {
    @Resource
    private WebVisitMapper webVisitMapper;

    @Resource
    private RedisCache redisCache;

    @Override
    public int getWebVisitCount() {
        // 获取今日开始和结束时间
        String startTime = DateUtils.getToDayStartTime();
        String endTime = DateUtils.getToDayEndTime();
        return webVisitMapper.getIpCount(startTime, endTime);
    }

    @Override
    public Map<String, Object>  getVisitByWeek() {
        // 从Redis中获取一周访问量
        String weekVisitJson = redisCache.get(RedisConst.DASHBOARD + SysConst.SYMBOL_COLON + RedisConst.WEEK_VISIT);
        if (StringUtils.isNotEmpty(weekVisitJson)) {
            return JsonUtils.jsonToMap(weekVisitJson, String.class, Object.class);
        }

        // 获取到今天结束的时间
        String todayEndTime = DateUtils.getToDayEndTime();
        //获取最近七天的日期
        Date sevenDaysDate = DateUtils.getDate(todayEndTime, -6);
        String sevenDays = DateUtils.getOneDayStartTime(sevenDaysDate);
        // 获取最近七天的数组列表
        List<String> sevenDaysList = DateUtils.getDaysByN(7, SysConst.DATE_FORMAT_YYYY_MM_DD);
        // 获得最近七天的访问量
        List<Map<String, Object>> pvMap = webVisitMapper.getPvByWeek(sevenDays, todayEndTime);
        // 获得最近七天的独立用户
        List<Map<String, Object>> uvMap = webVisitMapper.getUvByWeek(sevenDays, todayEndTime);

        Map<String, Object> countPvMap = new HashMap<>(pvMap.size());
        Map<String, Object> countUvMap = new HashMap<>(pvMap.size());

        for (Map<String, Object> item : pvMap) {
            countPvMap.put(item.get("DATE").toString(), item.get("COUNT"));
        }
        for (Map<String, Object> item : uvMap) {
            countUvMap.put(item.get("DATE").toString(), item.get("COUNT"));
        }
        // 访问量数组
        List<Integer> pvList = new ArrayList<>();
        // 独立用户数组
        List<Integer> uvList = new ArrayList<>();

        for (String day : sevenDaysList) {
            if (countPvMap.get(day) != null) {
                Number pvNumber = (Number) countPvMap.get(day);
                Number uvNumber = (Number) countUvMap.get(day);
                pvList.add(pvNumber.intValue());
                uvList.add(uvNumber.intValue());
            } else {
                pvList.add(0);
                uvList.add(0);
            }
        }
        Map<String, Object> resultMap = new HashMap<>(SysConst.NUM_THREE);
        // 不含年份的数组格式
        List<String> resultSevenDaysList = DateUtils.getDaysByN(7, SysConst.DATE_FORMAT_MM_DD);
        resultMap.put("date", resultSevenDaysList);
        resultMap.put("pv", pvList);
        resultMap.put("uv", uvList);

        // 将一周访问量存入Redis中(过期时间10分钟)
        redisCache.setNx(RedisConst.DASHBOARD + SysConst.SYMBOL_COLON + RedisConst.WEEK_VISIT, JsonUtils.objectToJson(resultMap), 10, TimeUnit.MINUTES);
        return resultMap;
    }
}

