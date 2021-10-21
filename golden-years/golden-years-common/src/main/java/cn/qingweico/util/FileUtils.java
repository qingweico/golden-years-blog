package cn.qingweico.util;

import sun.misc.BASE64Encoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;

/**
 * @author:qiming
 * @date: 2021/9/10
 */
public class FileUtils {

    /**
     * 文件流下载 在浏览器展示
     *
     * @param response HttpServletResponse
     * @param file     文件从盘符开始的完整路径
     */
    public static void downloadFileByStream(HttpServletResponse response, File file) {
        String filePath = file.getPath();
        System.out.println("filePath = " + filePath);
        // 对encode过的filePath处理
        if (filePath.contains("%")) {
            try {
                filePath = URLDecoder.decode(filePath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
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
            if ("jpg,jepg,gif,png".contains(fileType)) {    // 判断图片类型
                response.setContentType("image/" + fileType);
            } else if ("pdf".contains(fileType)) {          // 判断pdf类型
                response.setContentType("application/pdf");
            } else {                                        // 设置multipart
                response.setContentType("multipart/form-data");
            }
            out = response.getOutputStream();
            // 读取文件流
            int len = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        // 读取文件字节数组
        try {
            in = new FileInputStream(file);
            fileData = new byte[in.available()];
            in.read(fileData);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 对字节数组Base64编码并且返回
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(fileData);
    }

}