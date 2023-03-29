package cn.qingweico.files.controller;

import cn.qingweico.enums.FileUploadType;
import cn.qingweico.exception.GraceException;
import cn.qingweico.files.resource.FileResource;
import cn.qingweico.files.service.UploaderService;
import cn.qingweico.global.SysConst;
import cn.qingweico.pojo.bo.GridFsBO;
import cn.qingweico.result.Result;
import cn.qingweico.result.Response;
import cn.qingweico.util.FileUtils;
import cn.qingweico.util.QiniuUtil;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.gridfs.GridFSBucket;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
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
public class FileUploadController {

    @Resource
    private UploaderService uploaderService;

    @Resource
    private FileResource fileResource;

    @Resource
    private QiniuUtil qiniuUtil;

//    @Resource
//    private SystemConfigService systemConfigService;
    @Resource
    private GridFSBucket gridFsBucket;

    private static final String FACE_PIC_PATH;

    static {
        String os = System.getProperty("os.name");
        if (SysConst.OS.equals(os)) {
            FACE_PIC_PATH = SysConst.WINDOWS_FACE_PIC_PATH;
        } else {
            FACE_PIC_PATH = SysConst.LINUX_FACE_PIC_PATH;
        }
    }

    @ApiOperation(value = "上传用户头像", notes = "上传用户头像", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public Result uploadFace(MultipartFile file) throws IOException {
        String path = null;
//        SystemConfig systemConfig = systemConfigService.getSystemConfig();

//        Integer uploadMethod = systemConfig.getUploadMethod();

        Integer uploadMethod = 1;
        if (file != null) {
            // 获得上传文件名称
            String fileName = file.getOriginalFilename();

            // 判断文件名不能为空
            if (StringUtils.isNotBlank(fileName)) {
                // 获得文件扩展名
                String fileExtension = fileName.substring(fileName.lastIndexOf(SysConst.SYMBOL_POINT) + 1);
                // 判断文件扩展名类型
                if (!fileExtension.equalsIgnoreCase(SysConst.FILE_SUFFIX_JPG) && !fileExtension.equalsIgnoreCase(SysConst.FILE_SUFFIX_JPEG) && !fileExtension.equalsIgnoreCase(SysConst.FILE_SUFFIX_PNG) && !fileExtension.equalsIgnoreCase(SysConst.FILE_SUFFIX_BLOB)) {
                    return Result.r(Response.FILE_FORMATTER_FAILED);
                }

                if (FileUploadType.FASTDFS.type.equals(uploadMethod)) {
                    path = uploaderService.uploadFastDfs(file, fileExtension);
                } else if (FileUploadType.ALIYUN.type.equals(uploadMethod)) {
                    // TODO
                    String userId = "001";
                    path = uploaderService.uploadOss(file, userId, fileExtension);
                } else if (FileUploadType.QINIUYUN.type.equals(uploadMethod)) {
                    path = qiniuUtil.upload(file.getInputStream(), fileName);
                }

            } else {
                return Result.r(Response.FILE_UPLOAD_NULL_ERROR);
            }
        } else {
            return Result.r(Response.FILE_UPLOAD_FAILED);
        }
        log.info("path = {}", path);

        String finalPath;
        if (StringUtils.isNotBlank(path)) {
            if (FileUploadType.FASTDFS.type.equals(uploadMethod)) {
                finalPath = fileResource.getFsHost() + path;
            } else if (FileUploadType.ALIYUN.type.equals(uploadMethod)) {
                finalPath = fileResource.getOssHost() + path;
            }else {
                finalPath = path;
            }
        } else {
            return Result.r(Response.FILE_UPLOAD_FAILED);
        } return Result.ok(Response.UPLOAD_SUCCESS, finalPath);
    }


    @ApiOperation(value = "上传文件到mongodb的GridFs中", notes = "上传文件到mongodb的GridFs中", httpMethod = "POST")
    @PostMapping("/uploadToGridFs")
    public Result uploadToGridFs(@RequestBody GridFsBO gridFsBO) throws IOException {

        // 获得图片的base64字符串
        String file64 = gridFsBO.getImg64();
        // 将base64字符串转换为字符数组
        byte[] bytes = new BASE64Decoder().decodeBuffer(file64);
        // 转换为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        // 上传到gridFS中
        ObjectId id = gridFsBucket.uploadFromStream(gridFsBO.getUsername() + SysConst.SYMBOL_POINT + SysConst.FILE_SUFFIX_PNG, inputStream);
        // 获得文件在gridFS中的主键id
        String fileId = id.toString();
        return Result.ok(Response.UPLOAD_SUCCESS, fileId);
    }

    @ApiOperation(value = "从mongodb的GridFS中读取图片内容", notes = "从mongodb的GridFS中读取图片内容", httpMethod = "GET")
    @GetMapping("/readInGridFS")
    public void readInGridFs(String faceId, HttpServletResponse resp) throws IOException {

        if (StringUtils.isBlank(faceId)) {
            GraceException.error(Response.FILE_NOT_EXIST_ERROR);
        }

        // 从gridFS中读取文件
        File adminFace = readGridFsByFaceId(faceId);

        // 把人脸图片输出到浏览器
        FileUtils.downloadFileByStream(resp, adminFace);
    }

    @ApiOperation(value = "从mongodb的GridFS中读取图片内容, 并且返回base64", notes = "从mongodb的GridFS中读取图片内容, 并且返回base64", httpMethod = "GET")
    @GetMapping("/readFace64InGridFS")
    public Result readFace64InGridFs(String faceId) throws IOException {

        // 获得GridFs中人脸数据文件
        File face = readGridFsByFaceId(faceId);

        // 转换人脸为base64
        String base64Face = FileUtils.fileToBase64(face);
        return Result.ok(base64Face);
    }

    @ApiOperation(value = "上传多个文件", notes = "上传多个文件", httpMethod = "POST")
    @PostMapping("/uploadSomeFiles")
    public Result uploadSomeFiles(MultipartFile[] files) throws IOException {

        List<String> imageUrlList = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                String path;

                if (file != null) {
                    // 获得上传文件名称
                    String fileName = file.getOriginalFilename();

                    // 判断文件名不能为空
                    if (StringUtils.isNotBlank(fileName)) {
                        // 获得文件扩展名
                        String fileExtension = fileName.substring(fileName.lastIndexOf(SysConst.SYMBOL_POINT) + 1);
                        // 判断文件扩展名类型
                        if (!fileExtension.equalsIgnoreCase(SysConst.FILE_SUFFIX_JPG) && !fileExtension.equalsIgnoreCase(SysConst.FILE_SUFFIX_JPEG) && !fileExtension.equalsIgnoreCase(SysConst.FILE_SUFFIX_PNG)) {
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
        return Result.ok(Response.UPLOAD_SUCCESS, imageUrlList);
    }

    private File readGridFsByFaceId(String faceId) throws IOException {

        GridFSFindIterable gridFsFiles = gridFsBucket.find(Filters.eq(SysConst.MONGO_ID, new ObjectId((faceId))));
        GridFSFile gridFs = gridFsFiles.first();
        if (gridFs == null) {
            GraceException.error(Response.FILE_NOT_EXIST_ERROR);
        }
        String fileName = gridFs.getFilename();
        // 获取文件流; 保存到本地或者服务器的临时目录
        File picFile = new File(FACE_PIC_PATH + SysConst.SYMBOL_LEFT_OBLIQUE_LINE + fileName);
        // 创建文件输出流
        OutputStream os = Files.newOutputStream(picFile.toPath());
        // 下载到服务器或者本地
        gridFsBucket.downloadToStream(new ObjectId(faceId), os);
        return picFile;
    }

    @ApiOperation(value = "根据faceId删除GridFs中的人脸信息", notes = "根据faceId删除GridFs中的人脸信息", httpMethod = "GET")
    @GetMapping("/removeGridFsFile")
    public Result removeGridFsFile(String faceId) {
        gridFsBucket.delete(new ObjectId(faceId));
        return Result.ok();
    }
}

