package cn.qingweico.entity.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * 生成图片辅助工具类
 *
 * @author zqw
 * @date 2020/09/10
 */
@Data
@NoArgsConstructor
public class ImageHolder {
    private String imageForm;
    private InputStream image;
    private String imageName;

    public ImageHolder(InputStream image, String imageName) {
        this.image = image;
        this.imageName = imageName;
    }
}
