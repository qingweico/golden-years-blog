package cn.qingweico.files.controller;

import cn.qingweico.exception.GraceException;
import cn.qingweico.files.resource.FileResource;
import cn.qingweico.files.service.UploaderService;
import cn.qingweico.api.controller.files.FileUploaderControllerApi;
import cn.qingweico.global.Constants;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.bo.NewAdminBO;
import cn.qingweico.util.FileUtils;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.gridfs.GridFSBucket;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/8
 */
@RestController
public class FileUploadController implements FileUploaderControllerApi {

    @Resource
    private UploaderService uploaderService;

    @Resource
    private FileResource fileResource;

    @Resource
    private GridFSBucket gridFsBucket;

    @Value("${pic.path}")
    private String facePicPath;

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);


    @Override
    public GraceJsonResult uploadFace(String userId, MultipartFile file) throws IOException {
        String path;

        if (file != null) {
            // 获得上传文件名称
            String fileName = file.getOriginalFilename();

            // 判断文件名不能为空
            if (StringUtils.isNotBlank(fileName)) {
                // 获得文件扩展名
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                // 判断文件扩展名类型
                if (!fileExtension.equalsIgnoreCase(Constants.FILE_SUFFIX_JPG) &&
                        !fileExtension.equalsIgnoreCase(Constants.FILE_SUFFIX_JPEG) &&
                        !fileExtension.equalsIgnoreCase(Constants.FILE_SUFFIX_PNG)) {
                    return GraceJsonResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILED);
                }

                path = uploaderService.uploadFastDfs(file, fileExtension);
            } else {
                return GraceJsonResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        } else {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }
        log.info("path = {}", path);

        String finalPath;
        if (StringUtils.isNotBlank(path)) {
            finalPath = fileResource.getFsHost() + path;
        } else {
            return GraceJsonResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }
        return new GraceJsonResult(ResponseStatusEnum.UPLOAD_SUCCESS, finalPath);
    }


    @Override
    public GraceJsonResult uploadToGridFs(NewAdminBO newAdminBO) throws IOException {

        // 获得图片的base64字符串
        String file64 = newAdminBO.getImg64();
        // 将base64字符串转换为字符数组
        byte[] bytes = new BASE64Decoder().decodeBuffer(file64);
        // 转换为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        // 上传到gridFS中
        ObjectId id = gridFsBucket.uploadFromStream(newAdminBO.getUsername() + Constants.FILE_SUFFIX_PNG, inputStream);
        // 获得文件在gridFS中的主键id
        String fileId = id.toString();
        return new GraceJsonResult(ResponseStatusEnum.UPLOAD_SUCCESS, fileId);
    }

    @Override
    public void readInGridFs(String faceId, HttpServletResponse resp) throws IOException {

        if (StringUtils.isBlank(faceId)) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        // 从gridFS中读取文件
        File adminFace = readGridFsByFaceId(faceId);

        // 把人脸图片输出到浏览器
        FileUtils.downloadFileByStream(resp, adminFace);
    }

    @Override
    public GraceJsonResult readFace64InGridFs(String faceId,
                                              HttpServletResponse resp) throws IOException {

        // 获得GridFS中人脸数据文件
        File face = readGridFsByFaceId(faceId);

        // 转换人脸为base64
        String base64Face = FileUtils.fileToBase64(face);
        return GraceJsonResult.ok(base64Face);
    }

    @Override
    public GraceJsonResult uploadSomeFiles(String userId, MultipartFile[] files) throws IOException {

        List<String> imageUrlList = new ArrayList<>();

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String path;

                if (file != null) {
                    // 获得上传文件名称
                    String fileName = file.getOriginalFilename();

                    // 判断文件名不能为空
                    if (StringUtils.isNotBlank(fileName)) {
                        // 获得文件扩展名
                        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                        // 判断文件扩展名类型
                        if (!fileExtension.equalsIgnoreCase(Constants.FILE_SUFFIX_JPG) &&
                                !fileExtension.equalsIgnoreCase(Constants.FILE_SUFFIX_JPEG) &&
                                !fileExtension.equalsIgnoreCase(Constants.FILE_SUFFIX_PNG)) {
                            continue;
                        }
                        // 执行文件上传
                        path = uploaderService.uploadFastDfs(file, fileExtension);
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
                log.info("path = {}", path);

                String finalPath = "";
                if (StringUtils.isNotBlank(path)) {
                    finalPath = fileResource.getFsHost() + path;
                }
                imageUrlList.add(finalPath);
            }
        }
        return GraceJsonResult.ok(imageUrlList);
    }

    private File readGridFsByFaceId(String faceId) throws FileNotFoundException {

        GridFSFindIterable gridFsFiles = gridFsBucket.
                find(Filters.eq("_id", new ObjectId((faceId))));

        GridFSFile gridFs = gridFsFiles.first();

        if (gridFs == null) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }
        String fileName = gridFs.getFilename();
        // 获取文件流; 保存到本地或者服务器的临时目录
        File picFile = new File(facePicPath + fileName);
        // 创建文件输出流
        OutputStream os = new FileOutputStream(picFile);
        // 下载到服务器或者本地
        gridFsBucket.downloadToStream(new ObjectId(faceId), os);
        return picFile;
    }
}

