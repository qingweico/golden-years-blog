package cn.qingweico.api.controller.user.fallback;

import cn.qingweico.api.controller.user.UserControllerApi;
import cn.qingweico.grace.result.GraceJsonResult;
import cn.qingweico.grace.result.ResponseStatusEnum;
import cn.qingweico.pojo.bo.UpdateUserInfoBO;
import cn.qingweico.pojo.vo.UserBasicInfoVO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:qiming
 * @date: 2021/9/16
 */
//@Component
//public class UserControllerFactoryFallback implements FallbackFactory<UserControllerApi> {
//    @Override
//    public UserControllerApi create(Throwable throwable) {
//        return new UserControllerApi() {
//
//            @Override
//            public GraceJsonResult getAccountInfo(String userId) {
//                return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
//            }
//
//            @Override
//            public GraceJsonResult getUserBasicInfo(String userId) {
//                return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
//            }
//
//            @Override
//            public GraceJsonResult updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {
//                return GraceJsonResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR);
//            }
//
//            @Override
//            public GraceJsonResult queryByIds(String userIds) {
//                List<UserBasicInfoVO> userBasicInfoVOList = new ArrayList<>(0);
//                return GraceJsonResult.ok(userBasicInfoVOList);
//            }
//        };
//    }
//}
