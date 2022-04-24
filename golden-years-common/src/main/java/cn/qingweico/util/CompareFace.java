package cn.qingweico.util;

import cn.qingweico.enums.FaceVerifyType;
import cn.qingweico.exception.GraceException;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.aliyun.AliResource;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.facebody.model.v20191230.CompareFaceRequest;
import com.aliyuncs.facebody.model.v20191230.CompareFaceResponse;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zqw
 * @date 2022/4/9
 */
@Slf4j
@Component
public class CompareFace {
    @Resource
    private AliResource aliResource;

    private static final String GATEWAY = "http://viapi-test.oss-cn-shanghai.aliyuncs.com/viapi-3.0domepic/facebody/CompareFace/";

    public boolean faceVerify(int type, String face1, String face2, float targetConfidence) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", aliResource.getAccessKeyId(),
                aliResource.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CompareFaceRequest request = new CompareFaceRequest();
        if (type == FaceVerifyType.BASE64.type) {
            request.setImageDataA(face1);
            request.setImageDataB(face2);
        } else {
            GraceException.error(ResponseStatusEnum.FACE_VERIFY_TYPE_ERROR);
        }

        try {
            CompareFaceResponse response = client.getAcsResponse(request);
            float responseConfidence = response.getData().getConfidence();
            log.info("responseConfidence: {}, 人脸对比结果: {}", responseConfidence,
                    responseConfidence > targetConfidence ? "通过" : "未通过");

            return responseConfidence > targetConfidence;

        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
        return false;
    }
}
