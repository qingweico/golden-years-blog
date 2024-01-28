package cn.qingweico.files.controller;

import cn.qingweico.enums.FileUploadType;
import cn.qingweico.files.resource.FileResource;
import cn.qingweico.files.service.UploaderService;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import cn.qingweico.result.Result;
import cn.qingweico.util.upload.QiniuUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
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


    @ApiOperation(value = "上传用户头像", notes = "上传用户头像", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public Result uploadFace(MultipartFile file) throws IOException {
        String path = null;

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

                if (FileUploadType.FAST_DFS.getVal().equals(uploadMethod)) {
                    path = uploaderService.uploadFastDfs(file, fileExtension);
                } else if (FileUploadType.ALI_YUN.getVal().equals(uploadMethod)) {
                    // TODO
                    String userId = "001";
                    path = uploaderService.uploadOss(file, userId, fileExtension);
                } else if (FileUploadType.QI_NIU_YUN.getVal().equals(uploadMethod)) {
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
            if (FileUploadType.FAST_DFS.getVal().equals(uploadMethod)) {
                finalPath = fileResource.getFsHost() + path;
            } else if (FileUploadType.ALI_YUN.getVal().equals(uploadMethod)) {
                finalPath = fileResource.getOssHost() + path;
            } else {
                finalPath = path;
            }
        } else {
            return Result.r(Response.FILE_UPLOAD_FAILED);
        }
        return Result.ok(Response.UPLOAD_SUCCESS, finalPath);
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
}

