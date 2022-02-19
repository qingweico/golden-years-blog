package cn.qingweico.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * 获取位置的工具类
 *
 * @author zqw
 * @date 2021/9/22
 */
public class AddressUtil {

    private static final Logger log = LoggerFactory.getLogger(AddressUtil.class);

    /**
     * IP地址查询
     */
    public static final String IP_URL = "https://whois.pconline.com.cn/ipJson.jsp";

    /**
     * 未知地址
     */
    public static final String UNKNOWN = "未知";

    public static String getRealAddressByIp(String ip) {
        // 内网不查询
        if (IpUtils.internalIp(ip)) {
            return "内网IP";
        }
        try {
            String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", "GBK");
            if (StringUtils.isEmpty(rspStr)) {
                log.error("获取地理位置异常 {}", ip);
                return UNKNOWN;
            }
            JSONObject obj = JSONObject.parseObject(rspStr);
            String region = obj.getString("pro");
            String city = obj.getString("city");
            return String.format("%s %s", region, city);
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", e.getMessage());
        }
        return UNKNOWN;
    }


    public static String getRealAddress() {
        try {
            HashMap<String, String> params = new HashMap<>(1);
            params.put("json", "true");
            String rspStr = HttpUtils.doGet(IP_URL, params);
            if (StringUtils.isEmpty(rspStr)) {
                return UNKNOWN;
            }
            JSONObject obj = JSONObject.parseObject(rspStr);
            String ip = obj.getString("ip");
            String addr = obj.getString("addr");
            return ip + "," + addr;
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", e.getMessage());
        }
        return UNKNOWN;
    }
}
