package cn.qingweico.util;

import cn.qingweico.util.aliyun.AliResource;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author:qiming
 * @date: 2021/9/5
 */
@Component
public class SmsUtil {

   @Autowired
   public AliResource aliResource;

   public void sendSMS(String mobile, String code) {
      DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
              aliResource.getAccessKeyId(),
              aliResource.getAccessKeySecret());
      IAcsClient client = new DefaultAcsClient(profile);

      CommonRequest request = new CommonRequest();
      request.setSysMethod(MethodType.POST);
      request.setSysDomain("dysmsapi.aliyuncs.com");
      request.setSysVersion("2017-05-25");
      request.setSysAction("SendSms");
      request.putQueryParameter("RegionId", "cn-hangzhou");

      request.putQueryParameter("PhoneNumbers", mobile);
      request.putQueryParameter("SignName", "流金岁月");
      request.putQueryParameter("TemplateCode", "SMS_183761535");
      request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");

      try {
         CommonResponse response = client.getCommonResponse(request);
         System.out.println(response.getData());
      } catch (ClientException e) {
         e.printStackTrace();
      }
   }
}
