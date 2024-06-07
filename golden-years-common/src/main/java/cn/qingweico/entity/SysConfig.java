package cn.qingweico.entity;

import cn.qingweico.enums.EditorModelType;
import cn.qingweico.enums.FileUploadType;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * 系统配置
 *
 * @author zqw
 * @date 2022/4/12
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SysConfig extends BaseEntity {
    private static final long serialVersionUID = 7978868592680200717L;
    /**
     * 文件上传方式 {@link FileUploadType}
     */
    private Integer uploadMethod;

    /**
     * 编辑器模式 {@link EditorModelType}
     */
    private Integer editorModel;
    /**
     * 主题颜色
     */
    private String themeColor;

    /**
     * 本地图片上传路径
     */
    private String localPicUrl;
}
