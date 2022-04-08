package cn.qingweico.files.restapi;

import cn.qingweico.exception.GraceException;
import cn.qingweico.files.resource.FileResource;
import cn.qingweico.files.service.UploaderService;
import cn.qingweico.global.Constants;
import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.pojo.bo.NewAdminBO;
import cn.qingweico.util.FileUtils;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.gridfs.GridFSBucket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@Slf4j
@Api(value = "文件上传的接口定义", tags = {"文件上传的接口定义"})
@RequestMapping("fs")
@RestController
public class FileUploadRestApi {

    @Resource
    private UploaderService uploaderService;

    @Resource
    private FileResource fileResource;

    @Resource
    private GridFSBucket gridFsBucket;

    @Value("${pic.path}")
    private String facePicPath;

    @ApiOperation(value = "上传用户头像", notes = "上传用户头像", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public GraceJsonResult uploadFace(MultipartFile file) throws IOException {
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
                        !fileExtension.equalsIgnoreCase(Constants.FILE_SUFFIX_PNG) &&
                !fileExtension.equalsIgnoreCase(Constants.FILE_SUFFIX_BLOB)) {
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


    @ApiOperation(value = "上传文件到mongodb的GridFS中", notes = "上传文件到mongodb的GridFS中", httpMethod = "POST")
    @PostMapping("/uploadToGridFS")
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

    @ApiOperation(value = "从mongodb的GridFS中读取图片内容", notes = "从mongodb的GridFS中读取图片内容", httpMethod = "GET")
    @GetMapping("/readInGridFS")
    public void readInGridFs(String faceId, HttpServletResponse resp) throws IOException {

        if (StringUtils.isBlank(faceId)) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        // 从gridFS中读取文件
        File adminFace = readGridFsByFaceId(faceId);

        // 把人脸图片输出到浏览器
        FileUtils.downloadFileByStream(resp, adminFace);
    }

    @ApiOperation(value = "从mongodb的GridFS中读取图片内容, 并且返回base64", notes = "从mongodb的GridFS中读取图片内容, 并且返回base64", httpMethod = "GET")
    @GetMapping("/readFace64InGridFS")
    public GraceJsonResult readFace64InGridFs(String faceId) throws IOException {

        // 获得GridFS中人脸数据文件
        File face = readGridFsByFaceId(faceId);

        // 转换人脸为base64
        String base64Face = FileUtils.fileToBase64(face);
        return GraceJsonResult.ok(base64Face);
    }

    @ApiOperation(value = "上传多个文件", notes = "上传多个文件", httpMethod = "POST")
    @PostMapping("/uploadSomeFiles")
    public GraceJsonResult uploadSomeFiles(MultipartFile[] files) throws IOException {

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
        return new GraceJsonResult(ResponseStatusEnum.UPLOAD_SUCCESS, imageUrlList);
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

