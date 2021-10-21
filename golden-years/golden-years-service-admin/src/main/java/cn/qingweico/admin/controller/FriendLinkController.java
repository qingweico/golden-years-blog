package cn.qingweico.admin.controller;

import cn.qingweico.admin.service.FriendLinkService;
import cn.qingweico.api.controller.admin.FriendLinkControllerApi;
import cn.qingweico.api.controller.BaseController;
import cn.qingweico.enums.YesOrNo;
import cn.qingweico.grace.result.GraceJsonResult;
import cn.qingweico.grace.result.ResponseStatusEnum;
import cn.qingweico.pojo.bo.SaveFriendLinkBO;
import cn.qingweico.pojo.mo.FriendLinkMO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
@RestController
public class FriendLinkController extends BaseController implements FriendLinkControllerApi {

   @Resource
   private FriendLinkService friendLinkService;
   @Override
   public GraceJsonResult saveOrUpdateFriendLink(SaveFriendLinkBO saveFriendLinkBO) {

      FriendLinkMO friendLinkMO = new FriendLinkMO();

      BeanUtils.copyProperties(saveFriendLinkBO, friendLinkMO);

      friendLinkMO.setCreateTime(new Date());
      friendLinkMO.setUpdateTime(new Date());
      friendLinkService.saveOrUpdateFriendLink(friendLinkMO);
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