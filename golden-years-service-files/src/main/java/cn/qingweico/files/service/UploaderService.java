package cn.qingweico.files.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传服务
 *
 * @author zqw
 * @date 2021/9/8
 */
public interface UploaderService {
    /**
     * 使用fastDFS上传文件
     *
     * @param file        MultipartFile
     * @param fileExtName 文件扩展名
     * @return String
     * @throws IOException IOException
     */

    String uploadFastDfs(MultipartFile file, String fileExtName) throws IOException;

    /**
     * 使用阿里OSS上传文件
     *
     * @param file        MultipartFile
     * @param fileExtName 文件扩展名
     * @param userId      用户id
     * @return String
     * @throws IOException IOException
     */
    String uploadOss(MultipartFile file, String userId, String fileExtName) throws IOException;
}
