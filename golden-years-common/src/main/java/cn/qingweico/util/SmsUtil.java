package cn.qingweico.util;

import cn.qingweico.util.aliyun.AliResource;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.aliyuncs.dysmsapi.model.v20170525.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 阿里云短信服务 API 文档地址:
 * <a href="https://help.aliyun.com/document_detail/419273.htm?spm=a2c4g.11186623.0.0.34375695Ns9kFy"></a>
 * Open API地址
 * <a href="https://next.api.aliyun.com/api/Dysmsapi/2017-05-25/SendSms?spm=5176.25163407.overview-index-9c3d4_4cfbe_0.24.51a7bb6eHjKUqG&sdkStyle=old&lang=JAVA&params={}"></a>
 *
 * @author zqw
 * @date 2021/9/5
 */
@Component
public class SmsUtil {

    @Resource
    public AliResource aliResource;

    /**
     * 原版 SDK
     *
     * @param mobile 手机号
     * @param code 验证码
     */
    public void sendSms(String mobile, String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                aliResource.getAccessKeyId(), aliResource.getAccessKeySecret());
        /* use STS Token
         DefaultProfile profile = DefaultProfile.getProfile(
         "<your-region-id>",           // The region ID
         "<your-access-key-id>",       // The AccessKey ID of the RAM account
         "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
         "<your-sts-token>");          // STS Token
         **/

        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();

        request.setPhoneNumbers(mobile);
        // 必须是已添加、并通过审核的短信签名
        request.setSignName("流金岁月博客");
        // 必须是已添加、并通过审核的短信模板
        request.setTemplateCode("SMS_246935128");
        request.setTemplateParam("{\"code\":\"" + code + "\"}");

        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }

    public static void main(String[] args) {
        SmsUtil smsUtil = new SmsUtil();
        smsUtil.
                sendSms("17796706221", "956745");
    }
}
