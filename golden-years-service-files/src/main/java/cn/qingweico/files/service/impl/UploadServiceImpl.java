package cn.qingweico.files.service.impl;

import cn.qingweico.files.resource.FileResource;
import cn.qingweico.files.service.UploaderService;
import cn.qingweico.global.SysConst;
import cn.qingweico.util.aliyun.AliResource;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zqw
 * @date 2021/9/8
 */
@Service
public class UploadServiceImpl implements UploaderService {
    @Resource
    private FastFileStorageClient fastFileStorageClient;
    @Resource
    private FileResource fileResource;
    @Resource
    private AliResource aliResource;

    @Override
    public String uploadFastDfs(MultipartFile file, String fileExtName) throws IOException {
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
        return storePath.getFullPath();
    }

    @Override
    public String uploadOss(MultipartFile file, String userId, String fileExtName) throws IOException {

        String endpoint = fileResource.getEndpoint();
        String accessKeyId = aliResource.getAccessKeyId();
        String accessKeySecret = aliResource.getAccessKeySecret();
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        String fileName = "";
        String myObjectName = fileResource.getObjectName() + SysConst.PATH_SEPARATOR + userId + SysConst.PATH_SEPARATOR + fileName + SysConst.SYMBOL_POINT + fileExtName;

        InputStream inputStream = file.getInputStream();
        ossClient.putObject(fileResource.getBucketName(), myObjectName, inputStream);
        ossClient.shutdown();
        return myObjectName;
    }
}
