package cn.qingweico.user.controller;

import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.user.FansControllerApi;
import cn.qingweico.enums.Sex;
import cn.qingweico.global.Constants;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.vo.FansCountsVO;
import cn.qingweico.user.service.FanService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author zqw
 * @date 2021/9/12
 */
@RestController
public class FansController extends BaseController implements FansControllerApi {

    @Resource
    private FanService fanService;

    @Override
    public GraceJsonResult isMeFollowThisAuthor(String userId, String fanId) {
        boolean res = fanService.isMeFollowThisAuthor(userId, fanId);
        return GraceJsonResult.ok(res);
    }

    @Override
    public GraceJsonResult follow(String userId, String fanId) {
        if (userId == null || fanId == null) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }
        if (userId.equals(fanId)) {
            return new GraceJsonResult(ResponseStatusEnum.CANNOT_FOLLOW_ONESELF);
        }
        fanService.follow(userId, fanId);
        return new GraceJsonResult(ResponseStatusEnum.FOLLOWED);
    }

    @Override
    public GraceJsonResult unfollow(String userId, String fanId) {
        fanService.unfollow(userId, fanId);
        return new GraceJsonResult(ResponseStatusEnum.UNFOLLOWED);
    }

    @Override
    public GraceJsonResult query(String userId,
                                 Integer page,
                                 Integer pageSize) {
        if (page == null) {
            page = Constants.COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }

        return GraceJsonResult.ok(fanService.getMyFansList(userId, page, pageSize));
    }

    @Override
    public GraceJsonResult queryViaEs(String userId, Integer page, Integer pageSize) {
        if (page == null) {
            page = Constants.COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = Constants.COMMON_PAGE_SIZE;
        }

        return GraceJsonResult.ok(fanService.getMyFansListViaEs(userId, page, pageSize));
    }

    @Override
    public GraceJsonResult queryRatio(String userId) {
        int manCounts = fanService.queryFansCounts(userId, Sex.MAN);
        int womanCounts = fanService.queryFansCounts(userId, Sex.WOMAN);
        FansCountsVO fanCountsVO = new FansCountsVO();
        fanCountsVO.setManCounts(manCounts);
        fanCountsVO.setWomanCounts(womanCounts);
        return GraceJsonResult.ok(fanCountsVO);
    }

    @Override
    public GraceJsonResult queryRatioViaEs(String userId) {
        return GraceJsonResult.ok(fanService.queryFansCountsViaEs(userId));
    }

    @Override
    public GraceJsonResult queryRatioByRegion(String userId) {
        return GraceJsonResult.ok(fanService.queryRatioByRegion(userId));
    }

    @Override
    public GraceJsonResult queryRatioByRegionViaEs(String userId) {
        return GraceJsonResult.ok(fanService.queryRatioByRegionViaEs(userId));
    }

    @Override
    public GraceJsonResult passive(String relationId, String fanId) {
        fanService.passive(relationId, fanId);
        return GraceJsonResult.ok();
    }
}
