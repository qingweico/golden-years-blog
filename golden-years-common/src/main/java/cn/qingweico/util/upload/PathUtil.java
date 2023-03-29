package cn.qingweico.util.upload;

import cn.qingweico.global.SysConst;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author zqw
 * @date 2021/11/6
 */
public class PathUtil {
   /**
    * 获取项目所在的根目录路径
    *
    * @return resources路径
    */
   public static String getProjectRootPath() {
      String absolutePath = null;
      try {
         String url = ResourceUtils.getURL("classpath:").getPath();
         absolutePath = urlDecode(new File(url).getAbsolutePath()) + File.separator;
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      return absolutePath;
   }

   /**
    * 路径解码
    *
    * @param url 目标url
    * @return 已解码url
    */
   public static String urlDecode(String url) {
      String decodeUrl = null;
      try {
         decodeUrl = URLDecoder.decode(url, "utf-8");
      } catch (UnsupportedEncodingException e) {
         e.printStackTrace();
      }
      return decodeUrl;
   }

   /**
    * 得到static路径
    *
    * @return @{code} static路径
    */
   public static String getStaticPath() {
      String projectRootAbsolutePath = getProjectRootPath();

      int index = projectRootAbsolutePath.indexOf("file:");
      if (index != -1) {
         projectRootAbsolutePath = projectRootAbsolutePath.substring(0, index);
      }

      return projectRootAbsolutePath + "static" + File.separator;


   }

   /**
    * 依据原始文件名生成新文件名
    *
    * @return 新文件名
    */
   public static String getFileName(String fileName) {
      try {
         SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
         return "" + number.nextInt(10000)
                 + System.currentTimeMillis() + getFileExt(fileName);
      } catch (NoSuchAlgorithmException e) {
        // swallow
      }
      return "" + System.currentTimeMillis() + getFileExt(fileName);

   }

   /**
    * 获取文件扩展名
    *
    * @return string
    */
   private static String getFileExt(String fileName) {
      if (fileName.lastIndexOf(SysConst.SYMBOL_POINT) == -1) {
         return ".png";
      }
      return fileName.substring(fileName.lastIndexOf(SysConst.SYMBOL_POINT));
   }
}
