package cn.qingweico.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件上传方式
 * @author zqw
 * @date 2022/4/12
 */
@AllArgsConstructor
@Getter
public enum FileUploadType {
    /**
     * 使用IO流上传文件至本地文件
     */
    LOCAL(0, "使用IO流上传文件至本地文件"),
    /**
     * 使用七牛云上传文件
     */
    QI_NIU_YUN(1, "使用七牛云上传文件"),
    /**
     * 使用阿里云上传文件
     */
    ALI_YUN(2, "使用阿里云上传文件"),
    /**
     * 使用FastDFS上传文件
     */
    FAST_DFS(3, "使用FastDFS上传文件");
    private final Integer val;
    private final String desc;
}
