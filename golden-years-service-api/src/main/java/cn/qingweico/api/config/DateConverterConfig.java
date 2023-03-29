package cn.qingweico.api.config;

import cn.qingweico.exception.GraceException;
import cn.qingweico.global.SysConst;
import cn.qingweico.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 请求路径url中的参数进行时间日期类型的转换, 字符串 -> 日期Date
 *
 * @author zqw
 * @date 2021/9/11
 */

@Configuration
@Slf4j
public class DateConverterConfig implements Converter<String, Date> {

    private static final List<String> FORMATTER_LIST = new ArrayList<>(4);

    static {
        FORMATTER_LIST.add("yyyy-MM");
        FORMATTER_LIST.add("yyyy-MM-dd");
        FORMATTER_LIST.add("yyyy-MM-dd hh:mm");
        FORMATTER_LIST.add("yyyy-MM-dd hh:mm:ss");
    }

    @Override
    public Date convert(String source) {
        String value = source.trim();
        if (SysConst.EMPTY_STRING.equals(value)) {
            return null;
        }
        if (source.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(source, FORMATTER_LIST.get(0));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(source, FORMATTER_LIST.get(1));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, FORMATTER_LIST.get(2));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, FORMATTER_LIST.get(3));
        } else {
            GraceException.error(Response.SYSTEM_DATE_PARSER_ERROR);
        }
        return null;
    }

    /**
     * 日期转换方法
     *
     * @param dateStr   String
     * @param formatter formatter
     * @return Date
     */
    public Date parseDate(String dateStr, String formatter) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(formatter);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return date;
    }
}
