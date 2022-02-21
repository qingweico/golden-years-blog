package cn.qingweico.api.controller.admin;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.pojo.bo.AdminLoginBO;
import cn.qingweico.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author zqw
 * @date 2021/9/9
 */
@Api(value = "管理员信息相关的接口定义", tags = {"管理员信息相关的接口定义"})
@RequestMapping("admin")
public interface AdminControllerApi {
   @ApiOperation(value = "管理员登陆接口", notes = "管理员登陆接口", httpMethod = "POST")
   @PostMapping("/login")
   GraceJsonResult login(@RequestBody @Valid AdminLoginBO adminLoginBO,
                         HttpServletRequest req,
                         HttpServletResponse resp);

   @ApiOperation(value = "查询admin用户名是否存在, 保证admin的唯一性", notes = "查询admin用户名是否存在, 保证admin的唯一性", httpMethod = "POST")
   @PostMapping("/present")
   GraceJsonResult present(String username);


   @ApiOperation(value = "创建新的管理员", notes = "创建新的管理员", httpMethod = "POST")
   @PostMapping("/add")
   GraceJsonResult add(@RequestBody @Valid NewAdminBO newAdminBO,
                       HttpServletRequest req,
                       HttpServletResponse resp);


   @ApiOperation(value = "查询admin列表", notes = "查询admin列表", httpMethod = "POST")
   @PostMapping("/list")
   GraceJsonResult list(
           @ApiParam(name = "page", value = "分页查询下一页的第几页") Integer page,
           @ApiParam(name = "page", value = "分页查询每一页显示的条数") Integer pageSize);


   @ApiOperation(value = "admin退出登陆", notes = "admin退出登陆", httpMethod = "POST")
   @PostMapping("/logout")
   GraceJsonResult logout(String adminId,
                          HttpServletRequest req,
                          HttpServletResponse resp);


   @ApiOperation(value = "admin人脸登陆", notes = "admin人脸登陆", httpMethod = "POST")
   @PostMapping("/face")
   GraceJsonResult face(@RequestBody AdminLoginBO adminLoginBO,
                        HttpServletRequest req,
                        HttpServletResponse resp);
}
