package cn.qingweico.global;

/**
 * 系统全局常量
 *
 * @author zqw
 * @date 2022/3/23
 */
public final class SysConf {

    /**
     * db column
     */
    public final static String ALL = "all";
    public final static String ID = "id";
    public final static String USER_ID = "user_id";
    public final static String TITLE = "title";
    public final static String NAME = "name";
    public final static String VALUE = "value";
    public static final String TAG_ID = "tagId";
    public final static String CONTENT = "content";
    public final static String AVATAR = "avatar";
    public final static String STATUS = "status";


    /**
     * Java property
     */
    public final static String CREATE_TIME = "createTime";
    public final static String IS_DELETE = "isDelete";
    public final static String IS_APPOINT = "isAppoint";
    public final static String ARTICLE_STATUS = "articleStatus";
    public final static String CATEGORY_ID = "categoryId";
    public final static String INFLUENCE = "influence";
    public final static String AUTHOR_ID = "authorId";


    public final static String BLOG_COUNT = "blogCount";
    public final static String USER_COUNT = "userCount";
    public final static String COMMENT_COUNT = "commentCount";
    public final static String VISIT_COUNT = "visitCount";
    public final static String BLOG_COUNT_BY_TAG = "blogCountByTag";
    public final static String VISIT_BY_WEEK = "visitByWeek";

    public final static String ADMIN = "admin";
    public final static String ADMIN_ID = "adminId";
    public final static String CLAIMS = "claims";


    public final static String PARENT_LIST = "parentList";
    public final static String SON_LIST = "sonList";

    public final static String URL = "url";

    public final static String LOG = "log";
    public final static String EXCEPTION = "exception";
    public static final String SEPARATOR = System.getProperty("file.separator");

    /**
     * 英文符号
     */
    public final static String SYMBOL_COMMA = ",";
    public final static String SYMBOL_POINT = ".";
    public final static String SYMBOL_QUESTION = "?";
    public final static String SYMBOL_COLON = ":";
    public final static String SYMBOL_STAR = "*";
    public final static String SYMBOL_WELL = "#";
    public final static String SYMBOL_HYPHEN = "-";
    public final static String SYMBOL_UNDERLINE = "_";
    public final static String SYMBOL_LEFT_BRACKET = "{";
    public final static String SYMBOL_RIGHT_BRACKET = "}";
    public final static String SYMBOL_RIGHT_EQUAL = "=";
    public final static String SYMBOL_LEFT_OBLIQUE_LINE = "/";
    public static final String PATH_SEPARATOR = "/";
    public static final String DELIMITER_TO = "@";
    public static final String DELIMITER_COLON = ":";
    public static final String DELIMITER_PERCENT = "%";

    /**
     * 文件名后缀-img
     */
    public final static String FILE_SUFFIX_WEBP = "webp";
    public final static String FILE_SUFFIX_TIF = "tif";
    public final static String FILE_SUFFIX_BMP = "bmp";
    public final static String FILE_SUFFIX_GIF = "gif";
    public final static String FILE_SUFFIX_JPG = "jpg";
    public final static String FILE_SUFFIX_JPEG = "jpeg";
    public final static String FILE_SUFFIX_PNG = "png";
    public final static String FILE_SUFFIX_BLOB = "blob";
    /**
     * 文件名后缀-doc
     */
    public final static String FILE_SUFFIX_PPT = "ppt";
    public final static String FILE_SUFFIX_XLS = "xls";
    public final static String FILE_SUFFIX_PDF = "pdf";
    public final static String FILE_SUFFIX_DOC = "doc";
    public final static String FILE_SUFFIX_HLP = "hlp";
    public final static String FILE_SUFFIX_WPS = "wps";
    public final static String FILE_SUFFIX_RTF = "rtf";
    public final static String FILE_SUFFIX_HTML = "html";
    public final static String FILE_SUFFIX_MD = "md";
    public final static String FILE_SUFFIX_DOCX = "docx";

    public final static String FILE_SUFFIX_SQL = "sql";
    public final static String FILE_SUFFIX_CSS = "css";
    public final static String FILE_SUFFIX_JS = "js";
    public final static String FILE_SUFFIX_VUE = "vue";
    public final static String FILE_SUFFIX_JAVA = "java";

    public final static String FILE_SUFFIX_RAR = "rar";
    public final static String FILE_SUFFIX_ZIP = "zip";

    public final static String FILE_SUFFIX_SVG = "svg";
    public final static String FILE_SUFFIX_MSI = "msi";

    public final static String FILE_SUFFIX_TXT = "txt";
    public final static String FILE_SUFFIX_PPTX = "pptx";
    public final static String FILE_SUFFIX_XLSX = "xlsx";
    public final static String FILE_SUFFIX_RM = "rm";
    public final static String FILE_SUFFIX_RMVB = "rmvb";
    public final static String FILE_SUFFIX_WMV = "wmv";
    public final static String FILE_SUFFIX_MP4 = "mp4";

    public final static String FILE_SUFFIX_3GP = "3gp";
    public final static String FILE_SUFFIX_MKV = "mkv";
    public final static String FILE_SUFFIX_AVI = "avi";
    public final static String FILE_SUFFIX_MPG = "mpg";
    public final static String FILE_SUFFIX_MOV = "mov";
    public final static String FILE_SUFFIX_SWF = "swf";

    public final static String FILE_SUFFIX_WAV = "wav";

    public final static String FILE_SUFFIX_AIF = "aif";
    public final static String FILE_SUFFIX_AU = "au";
    public final static String FILE_SUFFIX_MP3 = "mp3";
    public final static String FILE_SUFFIX_RAM = "ram";
    public final static String FILE_SUFFIX_WMA = "wma";
    public final static String FILE_SUFFIX_MMF = "mmf";
    public final static String FILE_SUFFIX_AMR = "amr";
    public final static String FILE_SUFFIX_AAC = "aac";
    public final static String FILE_SUFFIX_FLAC = "flac";


    /**
     * 系统全局是否标识
     */
    public static final int YES = 1;
    public static final int NO = 0;

    /**
     * 数字 0~10
     */
    public final static int NUM_ZERO = 0;
    public final static int NUM_ONE = 1;
    public final static int NUM_TWO = 2;
    public final static int NUM_THREE = 3;
    public final static int NUM_FOUR = 4;
    public final static int NUM_FIVE = 5;
    public final static int NUM_SIX = 6;
    public final static int NUM_SEVEN = 7;
    public final static int NUM_EIGHT = 8;
    public final static int NUM_NINE = 9;
    public final static int NUM_TEN = 10;
    public final static int NUM_TWENTY = 20;
    public final static int NUM_32 = 32;
    public final static int NUM_500 = 500;
    public final static int NUM_1000 = 1000;
    public final static int NUM_1024 = 1024;
    public final static int NUM_5000 = 5000;

    /**
     * 字符串数字 0~10
     */
    public final static String STR_ZERO = "0";
    public final static String STR_ONE = "1";
    public final static String STR_TWO = "2";
    public final static String STR_THREE = "3";
    public final static String STR_FOUR = "4";
    public final static String STR_FIVE = "5";
    public final static String STR_SIX = "6";
    public final static String STR_SEVEN = "7";
    public final static String STR_EIGHT = "8";
    public final static String STR_NINE = "9";
    public final static String STR_TEN = "10";

    /**
     * 日期 + 时间
     */
    public final static String DATE_FORMAT_YYYY = "yyyy";

    public final static String DATE_FORMAT_YYYY_MM = "yyyy-MM";

    public final static String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    public final static String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final Integer COMMON_START_PAGE = 1;
    public static final Integer COMMON_PAGE_SIZE = 5;
    public static final String IP = "ip";
    public static final String EMPTY_STRING = "";
    public static final String OS = "Windows 10";
    public final static String CONTRIBUTE_DATE = "contributeDate";
    public final static String BLOG_CONTRIBUTE_COUNT = "blogContributeCount";
    public static final int RESPONSE_SUCCESS = 200;
    /**
     * rabbit mq (article.*.do) routingKey
     */
    public static final String ARTICLE_CREATE_FAVORITES_DO = "article.create_favorites.do";

    /**
     * rabbit mq (delay queue) (article.delay.publish.*.do) routingKey
     */
    public static final String ARTICLE_DELAY_PUBLISH_SUCCESS_DO = "article.delay.publish.success.do";

}
