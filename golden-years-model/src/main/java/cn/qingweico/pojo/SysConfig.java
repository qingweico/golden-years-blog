package cn.qingweico.pojo;

import cn.qingweico.enums.EditorModelType;
import cn.qingweico.enums.SearchModelType;
import cn.qingweico.enums.FileUploadType;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zqw
 * @date 2022/4/12
 */
@Data
@Table(name = "t_sys_config")
public class SysConfig {
    @Id
    private String id;

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
     * 主站文章搜索模式 {@link SearchModelType}
     */
    private Integer searchModel;

    /**
     * 本地图片上传路径
     */
    private String localPicUrl;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建时间
     */
    private Date createTime;
}
