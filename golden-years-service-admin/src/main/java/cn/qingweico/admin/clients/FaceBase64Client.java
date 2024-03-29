package cn.qingweico.admin.clients;

import cn.qingweico.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zqw
 * @date 2021/10/19
 */
@FeignClient(value = "service-files", path = "fs")
public interface FaceBase64Client {

   /**
    * 获取人脸图像的base64编码
    * @param faceId 人脸id
    * @return GraceJsonResult
    */
   @GetMapping("readFace64InGridFS")
   Result getFaceBase64(@RequestParam("faceId")String faceId);

   /**
    * 根据faceId删除GridFs中的人脸信息
    * @param faceId 人脸id
    * @return GraceJsonResult
    */
   @GetMapping("removeGridFsFile")
   Result removeGridFsFile(@RequestParam("faceId")String faceId);
}
