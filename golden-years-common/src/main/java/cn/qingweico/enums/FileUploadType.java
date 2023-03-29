package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 文件上传方式
 * @author zqw
 * @date 2022/4/12
 */
@AllArgsConstructor
public enum FileUploadType {
    /**
     * 使用IO流上传文件至本地文件
     */
    LOCAL(0, "使用IO流上传文件至本地文件"),
    /**
     * 使用七牛云上传文件
     */
    QINIUYUN(1, "使用七牛云上传文件"),
    /**
     * 使用阿里云上传文件
     */
    ALIYUN(2, "使用阿里云上传文件"),
    /**
     * 使用FastDFS上传文件
     */
    FASTDFS(3, "使用FastDFS上传文件");
    public final Integer type;
    public final String value;
}
