package cn.qingweico.enums;

import lombok.AllArgsConstructor;

/**
 * 人脸比对类型
 *
 * @author zqw
 * @date 2021/9/10
 */
@AllArgsConstructor
public enum FaceVerifyType {
    /**
     * 图片Base64对比
     */
    BASE64(1, "图片Base64对比"),
    /**
     * URL图片地址对比
     */
    IMAGE_URL(0, "URL图片地址对比");

    public final Integer type;
    public final String value;
}
