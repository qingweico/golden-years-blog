package cn.qingweico.enums;

/**
 * @author zqw
 * @date 2022/4/12
 */
public enum UploadLocalType {
    /**
     * 使用IO流上传文件至本地文件
     */
    YES(1, "使用IO流上传文件至本地文件"),
    /**
     * 使用其他方式上传文件
     */
    NO(0, "使用其他方式上传文件");
    public final Integer type;
    public final String value;

    UploadLocalType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
