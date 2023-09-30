package cn.qingweico.util;

import cn.qingweico.global.Symbol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.Binder;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * @author zqw
 * @date 2021/9/10
 */
@Slf4j
public class FileUtils {

    /**
     * 文件流下载; 在浏览器展示
     *
     * @param response HttpServletResponse
     * @param file     文件从盘符开始的完整路径
     */
    public static void downloadFileByStream(HttpServletResponse response, File file) {
        String filePath = file.getPath();
        System.out.println("filePath = " + filePath);
        // 对encode过的filePath处理
        if (filePath.contains(Symbol.PERCENT)) {
            try {
                filePath = URLDecoder.decode(filePath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("{}", e.getMessage());
            }
        }

        ServletOutputStream out = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            String[] dir = filePath.split("/");
            String fileName = dir[dir.length - 1];
            String[] array = fileName.split("[.]");
            String fileType = array[array.length - 1].toLowerCase();
            // 设置文件ContentType类型
            // 判断图片类型

            // TODO优化
            if ("jpg,jepg,gif,png".contains(fileType)) {
                response.setContentType("image/" + fileType);
                // 判断pdf类型
            } else if ("pdf".contains(fileType)) {
                response.setContentType("application/pdf");
            } else {
                // 设置multipart
                response.setContentType("multipart/form-data");
            }
            out = response.getOutputStream();
            // 读取文件流
            int len;
            byte[] buffer = new byte[1024 * 10];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                log.error("{}", e.getMessage());
            }
        }
    }

    /**
     * 文件转换为base64
     * 将图片文件转化为字节数组字符串, 并对其进行Base64编码处理
     *
     * @param file 图片文件
     * @return String
     */
    public static String fileToBase64(File file) {
        InputStream in;
        byte[] fileData = null;
        int read = 0;
        // 读取文件字节数组
        try {
            in = Files.newInputStream(file.toPath());
            fileData = new byte[in.available()];
            read = in.read(fileData);
            in.close();
        } catch (IOException e) {
            log.error("read byte: {}, {}", read, e.getMessage());
        }
        // 对字节数组Base64编码并且去掉换行符(由于JDK自带base64不会去掉换行符,导致base64格式验证失败)
        // 根据RFC822规定, BASE64Encoder编码每76个字符, 还需要加上一个回车换行
        // 部分Base64编码的java库还按照这个标准实行
        return Base64.encodeBase64String(fileData);
    }

}
