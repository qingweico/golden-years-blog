package cn.qingweico.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zqw
 * @date 2021/9/7
 */
@Slf4j
public class JsonUtils {

    /**
     * 定义jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串
     *
     * @param data Object -> JSON
     * @return JSON
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData JSON数据
     * @param beanType 对象中的object类型
     * @return POJO
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return null;
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param jsonData JSON
     * @param beanType Class<T>
     * @return List<T>
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return new ArrayList<>(0);
    }

    /**
     * 将 JSON 数据转换成 POJO 对象 Map
     *
     * @param jsonData JSON
     * @return Map<K, V>
     */
    public static <K, V> Map<K, V> jsonToMap(String jsonData, Class<K> k, Class<V> v) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Map.class, k, v);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
        return new HashMap<>(0);
    }

    /**
     * JSON 转 ArrayList
     *
     * @param jsonArray jsonArray
     * @return ArrayList<?>
     */
    public static ArrayList<?> jsonArrayToArrayList(String jsonArray) {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();
        ArrayList<?> list = null;
        try {
            Type listType = new TypeToken<ArrayList<?>>() {
            }.getType();

            list = gson.fromJson(jsonArray, listType);
        } catch (JsonSyntaxException e) {
            log.error("{}", e.getMessage());
        }
        return list;
    }
    /**
     * JSON 转 ArrayList
     */
    public static ArrayList<?> jsonArrayToArrayList(String jsonArray, Class<?> clazz) {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .create();
        ArrayList<?> list = null;
        try {

            list = (ArrayList<?>) gson.fromJson(jsonArray, clazz);
        } catch (JsonSyntaxException e) {
            log.error("{}", e.getMessage());
        }
        return list;
    }

    public static void main(String[] args) {

    }
}
