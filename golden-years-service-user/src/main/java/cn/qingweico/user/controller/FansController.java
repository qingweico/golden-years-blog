package cn.qingweico.user.controller;

import cn.qingweico.api.controller.BaseController;
import cn.qingweico.api.controller.user.FansControllerApi;
import cn.qingweico.enums.Sex;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.vo.FansCountsVO;
import cn.qingweico.user.service.FanService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author:qiming
 * @date: 2021/9/12
 */
@RestController
public class FansController extends BaseController implements FansControllerApi {

    @Resource
    private FanService fanService;

    @Override
    public GraceJsonResult isMeFollowThisWriter(String writerId, String fanId) {
        boolean res = fanService.isMeFollowThisAuthor(writerId, fanId);
        return GraceJsonResult.ok(res);
    }

    @Override
    public GraceJsonResult follow(String writerId, String fanId) {
        if (writerId == null || fanId == null) {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
        }
        if (writerId.equals(fanId)) {
            return new GraceJsonResult(ResponseStatusEnum.CANNOT_FOLLOW_ONESELF);
        }
        fanService.follow(writerId, fanId);
        return new GraceJsonResult(ResponseStatusEnum.FOLLOWED);
    }

    @Override
    public GraceJsonResult unfollow(String writerId, String fanId) {
        fanService.unfollow(writerId, fanId);
        return new GraceJsonResult(ResponseStatusEnum.UNFOLLOWED);
    }

    @Override
    public GraceJsonResult queryAll(String writerId,
                                    Integer page,
                                    Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        return GraceJsonResult.ok(fanService.getMyFansListViaEs(writerId, page, pageSize));
    }

    @Override
    public GraceJsonResult queryRatio(String writerId) {
        int manCounts = fanService.queryFansCounts(writerId, Sex.MAN);
        int womanCounts = fanService.queryFansCounts(writerId, Sex.WOMAN);
        FansCountsVO fanCountsVO = new FansCountsVO();
        fanCountsVO.setManCounts(manCounts);
        fanCountsVO.setWomanCounts(womanCounts);
        return GraceJsonResult.ok(fanCountsVO);
    }

    @Override
    public GraceJsonResult queryRatioViaEs(String writerId) {
        return GraceJsonResult.ok(fanService.queryFansCountsViaEs(writerId));
    }

    @Override
    public GraceJsonResult queryRatioByRegion(String writerId) {
        return GraceJsonResult.ok(fanService.queryRatioByRegion(writerId));
    }

    @Override
    public GraceJsonResult queryRatioByRegionViaEs(String writerId) {
        return GraceJsonResult.ok(fanService.queryRatioByRegionViaEs(writerId));
    }

    @Override
    public GraceJsonResult passive(String relationId, String fanId) {
        fanService.passive(relationId, fanId);
        return GraceJsonResult.ok();
    }
}
