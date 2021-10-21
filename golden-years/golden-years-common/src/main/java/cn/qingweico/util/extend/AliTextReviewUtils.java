package cn.qingweico.util.extend;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author:qiming
 * @date: 2021/9/11
 */
@Component
public class AliTextReviewUtils {

    @Resource
    private AliResource aliResource;

    public String reviewTextContent(String content) {
        IClientProfile profile = DefaultProfile.getProfile("cn-shanghai",
                aliResource.getAccessKeyId(),
                aliResource.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
        textScanRequest.setHttpContentType(FormatType.JSON);
        textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId("cn-shanghai");
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        /**
         * 待检测的文本，长度不超过10000个字符
         */
//        抵制毒品交易
//          尼玛
        task1.put("content", content);
        tasks.add(task1);
        JSONObject data = new JSONObject();

        /**
         * 检测场景，文本垃圾检测传递：antispam
         **/
        data.put("scenes", Collections.singletonList("antispam"));
        data.put("tasks", tasks);
        System.out.println(JSON.toJSONString(data, true));

        try {
            textScanRequest.setHttpContent(data.toJSONString().getBytes(StandardCharsets.UTF_8), "UTF-8", FormatType.JSON);
            // 请务必设置超时时间
            textScanRequest.setConnectTimeout(3000);
            textScanRequest.setReadTimeout(6000);

            HttpResponse httpResponse = client.doAction(textScanRequest);
            if (httpResponse.isSuccess()) {
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), StandardCharsets.UTF_8));
                System.out.println(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        if (200 == ((JSONObject) taskResult).getInteger("code")) {
                            JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
                            JSONObject sceneResult = (JSONObject) sceneResults.get(0);
                            //                            for (Object sceneResult : sceneResults) {
                            String scene = sceneResult.getString("scene");
                            String suggestion = sceneResult.getString("suggestion");
                            //根据scene和suggetion做相关处理
                            //suggestion == pass 未命中垃圾  suggestion == block 命中了垃圾，可以通过label字段查看命中的垃圾分类
                            System.out.println("args = [" + scene + "]");
                            System.out.println("args = [" + suggestion + "]");

                            //                            suggestion=pass：文本正常，文章状态改为发布通过
                            //                            review：需要人工审核，需要在后台管理系统中进行人工审核（很多自媒体平台都会采用机审+人工审的方式）
                            //                            block：文本违规，可以直接删除或者做限制处理，审核不通过
                            //                            }
                            return suggestion;
                        } else {
                            System.out.println("task process fail:" + ((JSONObject) taskResult).getInteger("code"));
                            return null;
                        }
                    }
                } else {
                    System.out.println("detect not success. code:" + scrResponse.getInteger("code"));
                    return null;
                }
            } else {
                System.out.println("response not success. status:" + httpResponse.getStatus());
                return null;
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

}