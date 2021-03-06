package cn.qingweico.util.upload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static cn.qingweico.global.SysConf.SYMBOL_POINT;

/**
 * @author zqw
 * @date 2021/11/6
 */
public class FileUtil {
    public static final String[] IMG_FILE = {"bmp", "jpg", "png", "tif", "gif", "jpeg", "webp"};
    public static final String[] DOC_FILE = {"doc", "docx", "txt", "hlp", "wps", "rtf", "html", "pdf", "md", "sql", "css", "js", "vue", "java"};
    public static final String[] VIDEO_FILE = {"avi", "mp4", "mpg", "mov", "swf"};
    public static final String[] MUSIC_FILE = {"wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"};
    public static final String[] ALL_FILE = {"bmp", "jpg", "png", "tif", "gif", "jpeg", "webp",
            "doc", "docx", "txt", "hlp", "wps", "rtf", "html", "pdf", "md", "sql", "css", "js", "vue", "java",
            "avi", "mp4", "mpg", "mov", "swf",
            "wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"
    };
    public static final int IMAGE_TYPE = 1;
    public static final int DOC_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int MUSIC_TYPE = 4;
    public static final int OTHER_TYPE = 5;

    public static List<String> getFileExtendsByType(int fileType) {

        List<String> fileExtends;
        switch (fileType) {
            case IMAGE_TYPE:
                fileExtends = Arrays.asList(IMG_FILE);
                break;
            case DOC_TYPE:
                fileExtends = Arrays.asList(DOC_FILE);
                break;
            case VIDEO_TYPE:
                fileExtends = Arrays.asList(VIDEO_FILE);
                break;
            case MUSIC_TYPE:
                fileExtends = Arrays.asList(MUSIC_FILE);
                break;
            case OTHER_TYPE: {
                fileExtends = Arrays.asList(ALL_FILE);
            }
            break;
            default:
                fileExtends = new ArrayList<>();
                break;
        }
        return fileExtends;
    }

    /**
     * ???????????????????????????
     *
     * @param extendName ???????????????
     * @return ?????????????????????
     */
    public static boolean isImageFile(String extendName) {
        for (String s : IMG_FILE) {
            if (extendName.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }


    public static String pathSplitFormat(String filePath) {
        return filePath.replace("///", "/")
                .replace("//", "/")
                .replace("\\\\\\", "\\")
                .replace("\\\\", "\\");
    }

    /**
     * ?????????????????????
     *
     * @param fileName ?????????
     * @return ???????????????
     */
    public static String getFileType(String fileName) {
        if (fileName.lastIndexOf(SYMBOL_POINT) == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * ????????????????????????????????????
     *
     * @param fileName ?????????
     * @return ??????????????????????????????
     */
    public static String getFileNameNotExtend(String fileName) {
        String fileType = getFileType(fileName);
        return fileName.replace("." + fileType, "");
    }
}
