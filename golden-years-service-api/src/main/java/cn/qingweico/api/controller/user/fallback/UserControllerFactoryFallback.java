package cn.qingweico.api.controller.user.fallback;

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
