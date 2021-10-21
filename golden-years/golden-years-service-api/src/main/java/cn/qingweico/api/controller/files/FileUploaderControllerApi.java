package cn.qingweico.api.controller.files;

import cn.qingweico.grace.result.GraceJsonResult;
import cn.qingweico.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author:qiming
 * @date: 2021/9/8
 */
@Api(value = "文件上传的接口定义", tags = {"文件上传的接口定义"})
@RequestMapping("fs")
public interface FileUploaderControllerApi {

    @ApiOperation(value = "上传用户头像",
            notes = "上传用户头像", httpMethod = "POST")
    @PostMapping("/uploadFace")
    GraceJsonResult uploadFace(String userId, MultipartFile file) throws IOException;



    @ApiOperation(value = "上传文件到mongodb的GridFS中",
            notes = "上传文件到mongodb的GridFS中", httpMethod = "POST")
    @PostMapping("/uploadToGridFS")
    GraceJsonResult uploadToGridFs(@RequestBody NewAdminBO newAdminBO) throws IOException;

    @ApiOperation(value = "从mongodb的GridFS中读取图片内容",
            notes = "从mongodb的GridFS中读取图片内容", httpMethod = "GET")
    @GetMapping("/readInGridFS")
    void readInGridFs(String faceId, HttpServletResponse resp) throws IOException;

    @ApiOperation(value = "从mongodb的GridFS中读取图片内容, 并且返回base64",
            notes = "从mongodb的GridFS中读取图片内容, 并且返回base64", httpMethod = "GET")
    @GetMapping("/readFace64InGridFS")
    GraceJsonResult readFace64InGridFs(String faceId, HttpServletResponse resp) throws IOException;



    @ApiOperation(value = "上传多个文件",
            notes = "上传多个文件", httpMethod = "POST")
    @PostMapping("/uploadSomeFiles")
    GraceJsonResult uploadSomeFiles(String userId, MultipartFile[] files) throws IOException;

}
