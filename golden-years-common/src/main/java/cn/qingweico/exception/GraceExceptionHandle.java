package cn.qingweico.exception;

import cn.qingweico.result.GraceJsonResult;
import cn.qingweico.result.ResponseStatusEnum;
import cn.qingweico.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一异常拦截处理
 * 根据异常类型进行捕获, 然后返回异常信息到前端
 *
 * @author zqw
 * @date 2021/9/6
 */
@Slf4j
@ControllerAdvice
public class GraceExceptionHandle {

    @ExceptionHandler({CustomException.class})
    @ResponseBody
    public GraceJsonResult returnException(CustomException ex) {
        log.error(ExceptionUtil.getExceptionMessage(ex));
        return GraceJsonResult.exception(ex.getResponseStatus());
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    @ResponseBody
    public GraceJsonResult returnMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error(ExceptionUtil.getExceptionMessage(ex));
        return GraceJsonResult.errorCustom(ResponseStatusEnum.FILE_MAX_SIZE_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public GraceJsonResult returnMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // 判断 BindingResult 中是否保存了错误的验证信息, 如果有则返回
        BindingResult result = ex.getBindingResult();
        Map<String, String> map = getErrors(result);
        return GraceJsonResult.errorMap(map);
    }

    /**
     * 获取BO中的错误信息
     *
     * @param result BindingResult
     */
    public Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>(10);
        List<FieldError> errors = result.getFieldErrors();
        for (FieldError error : errors) {
            // 发生验证错误时所对应的某个属性
            String field = error.getField();
            // 验证的错误消息
            String msg = error.getDefaultMessage();
            map.put(field, msg);
        }
        return map;
    }
}
