package cn.qingweico.util.upload;

import cn.qingweico.entity.model.ImageHolder;
import cn.qingweico.enums.ImageFormEnum;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * 图片处理工具
 *
 * @author zqw
 * @date 2020/09/12
 */
@Slf4j
public class ImageUtil {

    private static final String BASE_PATH = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String SEPARATOR = System.getProperty("file.separator");

    public static final String OS = "Windows 10";
    private static final Random R = new Random();

    /**
     * TODO 使用系统参数
     */
    private static final String WINDOW_BASE_PATH = "";

    /**
     * TODO 使用系统参数
     */
    private static final String LINUX_BASE_PATH = "";
    private static final ThreadLocal<DateFormat> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 生成缩略图
     *
     * @param imageHolder   imageHolder
     * @param targetAddress 目标路径地址
     * @return relativeAddress
     */
    public static String generateThumbnails(ImageHolder imageHolder, String targetAddress) {
        String realFileName = getRandomFileName();
        String extension = getFileExtension(imageHolder.getImageName());
        makeDirFile(targetAddress);
        String relativeAddress = targetAddress + realFileName + extension;
        File dest = new File(getImageBasePath() + relativeAddress);
        log.debug("上传图片的相对路径: " + "----------> " + relativeAddress);
        log.debug("上传图片的绝对路径: " + "----------> " + dest);
        log.debug("当前classpath的绝对路径 : " + "----------> " + BASE_PATH);
        try {
            //生成正常大小的图片
            if (ImageFormEnum.NORMAL.toString().equals(imageHolder.getImageForm())) {
                Thumbnails.of(imageHolder.getImage()).size(400, 400)
                        //加水印
                        //.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/watermark.png")), 0.25f)
                        .outputQuality(0.8f).toFile(dest);
            }
            //生成缩略图
            else if (ImageFormEnum.THUMBNAIL.toString().equals(imageHolder.getImageForm())) {
                Thumbnails.of(imageHolder.getImage()).size(200, 200)
                        //加水印
                        //.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/watermark.png")), 0.25f)
                        .outputQuality(0.9f).toFile(dest);
            } else {
                throw new RuntimeException("请设置上传图片的形式!");
            }

        } catch (IOException e) {
            log.error(e.toString());
        }
        return relativeAddress;
    }


    /**
     * 生成随机的文件名 当前时间 + 五位随机数
     *
     * @return String
     */
    public static String getRandomFileName() {
        int randomNumber = R.nextInt(8999) + 1000;
        String dateString = getDateFormat().format(new Date());
        clearDateFormat();
        return dateString + randomNumber;
    }

    /**
     * SimpleDateFormat安全的时间格式化方法
     *
     * @return DateFormat
     */
    public static DateFormat getDateFormat() {
        DateFormat df = THREAD_LOCAL.get();
        if (df == null) {
            df = SIMPLE_DATE_FORMAT;
            THREAD_LOCAL.set(df);
        }
        return df;
    }

    public static void clearDateFormat() {
        THREAD_LOCAL.remove();
    }

    /**
     * 获取输入文件流的扩展名
     *
     * @return String
     */
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static void makeDirFile(String targetAddress) {
        String filePath = getImageBasePath() + targetAddress;
        File file = new File(filePath);
        if (!file.exists()) {
            boolean hasMkDirs = file.mkdirs();
            if (!hasMkDirs) {
                log.error("创建图片文件夹失败!");
            }

        }
    }

    public static void deleteFileOrDirectory(String path) {
        File file = new File(getImageBasePath() + path);
        File[] files = file.listFiles();
        if (file.exists()) {
            if (files != null && file.isDirectory()) {
                for (int i = 0; i < file.length(); i++) {
                    if (!files[i].delete()) {
                        log.error("删除图片文件夹失败!");
                    }
                }
            }
            if (!file.delete()) {
                log.error("删除图片文件夹失败!");
            }
        }

    }

    public static String getImageBasePath() {
        String os = System.getProperty("os.name");
        String path;
        if (OS.equals(os)) {
            path = WINDOW_BASE_PATH;
        } else {
            path = LINUX_BASE_PATH;
        }
        path = path.replace("/", SEPARATOR);
        return path;
    }
}
