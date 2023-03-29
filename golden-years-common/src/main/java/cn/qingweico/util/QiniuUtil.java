package cn.qingweico.util;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛云上传图片
 *
 * @author zqw
 * @date 2022/8/27
 */
@Slf4j
@Component
public class QiniuUtil {

//    @Value("${qiniuyun.access_key}")
    private String accessKey = "6Swju3-Ms1CukLIPVkrtD2hLcVYeLEQpZWMX3N_1";
//    @Value("${qiniuyun.secret_key}")
    private String secretKey = "B2baXxy0Z9q-XjI52rF5qAZokYHspyEWukQ65NrP";
//    @Value(("${qiniuyun.bucket_name}"))
    private String bucketName = "static002";
//    @Value(("${qiniuyun.domain}"))
    private String domain = "http://static002.qingweico.cn/";

    /*密钥配置*/

    Auth auth = Auth.create(accessKey, secretKey);

    // 华东	z0
    // 华北	z1
    // 华南	z2
    // 北美	na0
    // 东南 as0

    Configuration cfg = new Configuration(Zone.zone2());


    /*创建上传对象*/

    UploadManager uploadManager = new UploadManager(cfg);


    /**
     * 简单上传 使用默认策略, 只需要设置上传的空间名就可以了
     *
     * @param fileName 文件上传至七牛云空间的名称
     * @return token
     */
    public String getUpToken(String fileName) {
        return auth.uploadToken(bucketName, fileName, 3600, new StringMap().put("insertOnly", 0));
    }


    /**
     * 普通上传
     *
     * @param is       文件流
     * @param fileName 文件上传至七牛云空间的名称
     */
    public String upload(InputStream is, String fileName) {
        try {
            byte[] array = readInputStream(is);
            // 调用put方法上传
            Response res = uploadManager.put(array, fileName, getUpToken(fileName));
            String resultFileName = (String) res.jsonToMap().get("key");
            // 返回文件全路径
            return domain + resultFileName;
        } catch (QiniuException e) {
            Response r = e.response;
            log.error(r.toString());
            try {
                // 响应的文本信息
                log.info(r.bodyString());
            } catch (QiniuException e1) {
                // ignore
            }
        }
        return null;
    }

    /**
     * 读取字节输入流内容
     *
     * @param is InputStream
     * @return byte[]
     */
    private static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 2];
        int len;
        try {
            while ((len = is.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
            is.close();
        } catch (IOException e) {
            // swallow
        }
        return writer.toByteArray();
    }
}
