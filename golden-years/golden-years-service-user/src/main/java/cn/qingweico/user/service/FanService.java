package cn.qingweico.user.service;


import cn.qingweico.enums.Sex;
import cn.qingweico.pojo.vo.FansCountsVO;
import cn.qingweico.pojo.vo.RegionRatioVO;
import cn.qingweico.util.PagedGridResult;

import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/12
 */
public interface FanService {


    /**
     * 查询当前用户是否关注作家
     *
     * @param writerId 作家id
     * @param fanId    粉丝id
     * @return 未关注 or 已关注
     */
    boolean isMeFollowThisWriter(String writerId, String fanId);

    /**
     * 用户关注作家, 成为粉丝
     *
     * @param writerId 作家id
     * @param fanId    粉丝id
     */
    void follow(String writerId, String fanId);

    /**
     * 用户取关作家
     *
     * @param writerId 作家id
     * @param fanId    粉丝id
     */
    void unfollow(String writerId, String fanId);

    /**
     * 作家查询我的所有粉丝
     *
     * @param writerId 作家id
     * @param page     起始分页
     * @param pageSize 每页显示的条数
     * @return PagedGridResult
     */
    PagedGridResult getMyFansList(String writerId,
                                  Integer page,
                                  Integer pageSize);

    /**
     * 作家查询我的所有粉丝(elasticSearch)
     *
     * @param writerId 作家id
     * @param page     起始分页
     * @param pageSize 每页显示的条数
     * @return PagedGridResult
     */
    PagedGridResult getMyFansListViaEs(String writerId,
                                       Integer page,
                                       Integer pageSize);


    /**
     * 查询男女粉丝的数量
     *
     * @param writerId 作家id
     * @param sex      查询条件: 性别
     * @return 粉丝数量
     */
    Integer queryFansCounts(String writerId, Sex sex);


    /**
     * 查询男女粉丝的数量(es)
     *
     * @param writerId 作家id
     * @return FansCountsVO
     */
    FansCountsVO queryFansCountsViaEs(String writerId);

    /**
     * 根据地域分布查询粉丝数量
     *
     * @param writerId 作家id
     * @return List<RegionRatioVO>
     */
    List<RegionRatioVO> queryRatioByRegion(String writerId);


    /**
     * 根据地域分布查询粉丝数量(es)
     *
     * @param writerId 作家id
     * @return List<RegionRatioVO>
     */
    List<RegionRatioVO> queryRatioByRegionViaEs(String writerId);



    /**
     * 被动更新粉丝信息 ---> 点击粉丝头像时
     *
     * @param relationId 记录主键
     * @param fanId      粉丝id
     */
    void passive(String relationId, String fanId);
}
