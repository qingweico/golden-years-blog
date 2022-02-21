package cn.qingweico.admin.controller;

import cn.qingweico.admin.service.FriendLinkService;
import cn.qingweico.api.controller.admin.FriendLinkControllerApi;
import cn.qingweico.api.controller.BaseController;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.bo.SaveFriendLinkBO;
import cn.qingweico.pojo.mo.FriendLinkMo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zqw
 * @date 2021/9/10
 */
@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerApi {

   @Resource
   private FriendLinkService friendLinkService;
   @Override
   public GraceJsonResult saveOrUpdateFriendLink(SaveFriendLinkBO saveFriendLinkBO) {

      FriendLinkMo friendLinkMo = new FriendLinkMo();

      BeanUtils.copyProperties(saveFriendLinkBO, friendLinkMo);

      friendLinkMo.setCreateTime(new Date());
      friendLinkMo.setUpdateTime(new Date());
      friendLinkService.saveOrUpdateFriendLink(friendLinkMo);
      String pendingFriendLinkId = saveFriendLinkBO.getId();
      if (StringUtils.isBlank(pendingFriendLinkId)) {
         return new GraceJsonResult(ResponseStatusEnum.APPEND_SUCCESS);
      } else {
         return new GraceJsonResult(ResponseStatusEnum.ALERT_SUCCESS);
      }
   }

   @Override
   public GraceJsonResult getFriendLinkList() {
      return GraceJsonResult.ok(friendLinkService.queryAllFriendLinkList());
   }

   @Override
   public GraceJsonResult delete(String linkId) {
      friendLinkService.delete(linkId);
      return new GraceJsonResult(ResponseStatusEnum.DELETE_SUCCESS);
   }

   @Override
   public GraceJsonResult getIndexFriendLinkList() {
      return GraceJsonResult.ok(friendLinkService.queryAllFriendLinkList(YesOrNo.NO.type));
   }
}