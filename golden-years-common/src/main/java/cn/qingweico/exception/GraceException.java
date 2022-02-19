package cn.qingweico.exception;

import cn.qingweico.result.ResponseStatusEnum;

/**
 * 优雅的异常处理, 统一封装
 *
 * @author zqw
 * @date 2021/9/6
 */
public class GraceException {
   public static void display(ResponseStatusEnum responseStatus) {
      throw new CustomException(responseStatus);
   }
}
