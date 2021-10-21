package cn.qingweico.admin.clients;

import cn.qingweico.grace.result.GraceJsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author:qiming
 * @date: 2021/10/19
 */
@FeignClient(value = "service-files", path = "fs")
public interface FaceBase64Client {

   @GetMapping("readFace64InGridFS")
   GraceJsonResult getFaceBase64(@RequestParam("faceId")String faceId);
}
