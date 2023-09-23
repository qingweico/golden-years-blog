package cn.qingweico.user.service;


import cn.qingweico.entity.Fans;
import cn.qingweico.enums.Sex;
import cn.qingweico.user.entity.FansCountsVO;
import cn.qingweico.user.entity.RegionRatioVO;
import cn.qingweico.util.PagedResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * @author zqw
 * @date 2021/9/12
 */
public interface FanService extends IService<Fans> {


    /**
     * 查询当前用户是否关注作者
     *
     * @param authorId 作者id
     * @param fanId    粉丝id
     * @return 未关注 or 已关注
     */
    boolean isMeFollowThisAuthor(String authorId, String fanId);

    /**
     * 用户关注作者, 成为粉丝
     *
     * @param authorId 作家id
     * @param fanId    粉丝id
     */
    void follow(String authorId, String fanId);

    /**
     * 用户取关作者
     *
     * @param authorId 作者id
     * @param fanId    粉丝id
     */
    void unfollow(String authorId, String fanId);

    /**
     * 作者查询我的所有粉丝
     *
     * @param authorId 作者id
     * @param page     起始分页
     * @param pageSize 每页显示的条数
     * @return PagedGridResult
     */
    PagedResult getMyFansList(String authorId,
                              Integer page,
                              Integer pageSize);

    /**
     * 作者查询我的所有粉丝(elasticSearch)
     *
     * @param authorId 作者id
     * @param page     起始分页
     * @param pageSize 每页显示的条数
     * @return PagedGridResult
     */
    PagedResult getMyFansListViaEs(String authorId,
                                   Integer page,
                                   Integer pageSize);


    /**
     * 查询男女粉丝的数量
     *
     * @param authorId 作者id
     * @param sex      查询条件: 性别
     * @return 粉丝数量
     */
    Integer queryFansCounts(String authorId, Sex sex);


    /**
     * 查询男女粉丝的数量(es)
     *
     * @param authorId 作者id
     * @return FansCountsVO
     */
    FansCountsVO queryFansCountsViaEs(String authorId);

    /**
     * 根据地域分布查询粉丝数量
     *
     * @param authorId 作者id
     * @return List<RegionRatioVO>
     */
    List<RegionRatioVO> queryRatioByRegion(String authorId);


    /**
     * 根据地域分布查询粉丝数量(es)
     *
     * @param authorId 作者id
     * @return List<RegionRatioVO>
     */
    List<RegionRatioVO> queryRatioByRegionViaEs(String authorId);



    /**
     * 被动更新粉丝信息 ---> 点击粉丝头像时
     *
     * @param relationId 粉丝信息相关主键id
     * @param fanId      粉丝id
     */
    void passive(String relationId, String fanId);
}
