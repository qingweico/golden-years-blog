package cn.qingweico.result;

import lombok.Data;

import java.util.Map;

/**
 * 返回统一接口
 *
 * @author zqw
 * @date 2021/9/5
 */
@Data
public class Result {
    /**
     * 响应业务状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应数据,可以是Object,也可以是List或Map等
     */
    private Object data;


    //// public api

    /**
     * 带有数据的 200 响应信息
     *
     * @param data Object
     * @return Result
     */
    public static Result ok(Object data) {
        return new Result(data);
    }

    /**
     * 带有数据的自定义响应信息
     *
     * @param r    Response
     * @param data Object
     * @return Result
     */
    public static Result ok(Response r, Object data) {
        return new Result(r, data);
    }

    /**
     * 返回 响应 200 的默认成功信息;无数据
     *
     * @return Result
     */
    public static Result ok() {
        return new Result(Response.SUCCESS);
    }

    /**
     * 返回 响应 200 的自定义提示信息;无数据
     *
     * @param msg 自定义提示信息
     * @return Result
     */
    public static Result ok(String msg) {
        return new Result(msg);
    }

    /**
     * 错误返回 代码异常
     *
     * @return Result
     */
    public static Result error() {
        return new Result(Response.FAILED);
    }

    /**
     * 错误返回 主要用于 JSR303 数据校验
     *
     * @param map Map<Object, Object>
     * @return Result
     */
    public static Result error(Map<?, ?> map) {
        return new Result(Response.CHECK_INFO, map);
    }


    /**
     * 自定义的错误响应信息
     *
     * @return Result
     */
    public static Result fail(Response r) {
        return new Result(r);
    }

    /**
     * 无数据 自定义的响应信息
     *
     * @param r ResponseStatusEnum
     * @return Result
     */
    public static Result r(Response r) {
        return new Result(r);
    }


    //// private construct

    /**
     * 带有数据的 200 响应信息
     *
     * @param data 自定义数据
     */
    private Result(Object data) {
        this.code = Response.SUCCESS.code();
        this.msg = Response.SUCCESS.msg();
        this.success = Response.SUCCESS.success();
        this.data = data;
    }

    /**
     * 无数据 自定义响应信息
     *
     * @param r Response
     */
    private Result(Response r) {
        this.code = r.code();
        this.msg = r.msg();
        this.success = r.success();
    }

    /**
     * 自定义数据和响应信息
     *
     * @param r    Response
     * @param data Object
     */
    private Result(Response r, Object data) {
        this.code = r.code();
        this.msg = r.msg();
        this.success = r.success();
        this.data = data;
    }

    /**
     * 返回 响应 200 的自定义的提示信息;无数据
     *
     * @param msg 提示信息
     */
    private Result(String msg) {
        this.code = Response.SUCCESS.code();
        this.success = Response.SUCCESS.success();
        this.msg = msg;
    }
}
