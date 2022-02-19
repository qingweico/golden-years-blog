package cn.qingweico.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zqw
 * @date 2021/9/7
 */
@Slf4j
public class JsonUtils {

   /**
    *  定义jackson对象
    */
   private static final ObjectMapper MAPPER = new ObjectMapper();

   /**
    * 将对象转换成json字符串
    * @param data Object -> json
    * @return json
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
    * @param jsonData json数据
    * @param beanType 对象中的object类型
    * @return pojo
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
    * @param jsonData json
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
}
