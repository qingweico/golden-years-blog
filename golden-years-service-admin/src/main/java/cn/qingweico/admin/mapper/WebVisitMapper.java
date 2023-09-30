package cn.qingweico.admin.mapper;

import cn.qingweico.entity.WebVisitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zqw
 * @date 2022/4/18
 */
@Repository
public interface WebVisitMapper extends BaseMapper<WebVisitor> {
    /**
     * 获取IP数目
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return IP数目
     */
    @Select("SELECT COUNT(ip) FROM (SELECT ip FROM t_web_visit WHERE create_time >= #{startTime} " +
            "AND create_time <= #{endTime} GROUP BY ip) AS tmp")
    Integer getIpCount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 统计最近七天内的访问量(PV)
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return 七天内的访问量
     */
    @Select("SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') DATE, " +
            "COUNT(id) COUNT FROM t_web_visit where create_time >= #{startTime} AND create_time <= #{endTime} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
    List<Map<String, Object>> getPvByWeek(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 统计七天内独立用户数(UV) 目前通过IP来统计
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return 七天内独立用户数
     */
    @Select("SELECT DATE, COUNT(ip) COUNT FROM (SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') " +
            "DATE, ip FROM t_web_visit WHERE create_time >= #{startTime} AND create_time <= #{endTime} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d'),ip) AS tmp GROUP BY DATE ")
    List<Map<String, Object>> getUvByWeek(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
