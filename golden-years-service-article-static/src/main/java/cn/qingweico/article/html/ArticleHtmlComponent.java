package cn.qingweico.article.html;

import com.mongodb.client.gridfs.GridFSBucket;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author zqw
 * @date 2021/9/15
 */
@Slf4j
@Component
public class ArticleHtmlComponent {

    @Resource
    private GridFSBucket gridFsBucket;

    @Value("${freemarker.html.article}")
    private String articlePath;

    public Integer download(String articleId, String articleMongoId)
            throws Exception {

        // 拼接最终文件的保存的地址
        String path = articlePath + File.separator + articleId + ".html";

        // 获取文件流, 定义存放的位置和名称
        File file = new File(path);
        // 创建输出流
        OutputStream outputStream = new FileOutputStream(file);
        // 执行下载
        gridFsBucket.downloadToStream(new ObjectId(articleMongoId), outputStream);
        outputStream.close();

        return HttpStatus.OK.value();
    }

    public Integer delete(String articleId) {

        // 拼接最终文件的保存的地址
        String path = articlePath + File.separator + articleId + ".html";

        // 获取文件流, 定义存放的位置和名称
        File file = new File(path);

        // 删除文件
        boolean isDelete = file.delete();
        if (!isDelete) {
            log.warn("articleId: {} file delete fail!", articleId);
        }
        return HttpStatus.OK.value();
    }
}
