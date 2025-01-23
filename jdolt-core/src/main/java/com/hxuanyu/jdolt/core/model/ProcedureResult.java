package com.hxuanyu.jdolt.core.model;

import java.util.List;
import java.util.Map;

/**
 * 用户端统一返回报文实体类
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class ProcedureResult {
    /**
     * 成功状态码
     */
    public static final Integer CALL_STATUS_SUCCESS = 0;
    /**
     * 失败状态码
     */
    public static final Integer CALL_STATUS_FAILED = 1;
    /**
     * 返回码，用于标识是否成功
     */
    private Integer status;
    /**
     * 返回信息，用于描述调用或请求状态，如果成功则添加成功的信息，若失败须写明失败原因
     */
    private String msg;
    /**
     * 调用或请求的实际结果，如果请求失败，该值应为空
     */
    private List<Map<String, Object>> data;

    /**
     * 构造器
     */
    private ProcedureResult() {
    }

    /**
     * 全参构造器
     */
    private ProcedureResult(Integer status, String msg, List<Map<String, Object>> data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造成功消息，如果本次返回需要返回数据，则调用该方法并将数据传入
     */
    public static ProcedureResult success(String msg, List<Map<String, Object>> data) {
        return new ProcedureResult(ProcedureResult.CALL_STATUS_SUCCESS, msg, data);
    }

    /**
     * 构造成功消息，本方法适用于不需要数据返回的情况
     */
    public static ProcedureResult success(String msg) {
        return success(msg, null);
    }

    /**
     * 构造简单的成功消息，使用默认的msg
     */
    public static ProcedureResult success() {
        return success("成功");
    }

    /**
     * 构造失败消息，由于请求失败，调用方无法获得正确的数据，因此该类型消息的数据区为null，但是msg中必须注明失败原因，
     * 失败原因由被调用方分析并设置
     *
     * @return
     */
    public static ProcedureResult failed(String msg) {
        return new ProcedureResult(ProcedureResult.CALL_STATUS_FAILED, msg, null);
    }

    /**
     * 构造带有默认msg字段的失败消息
     */
    public static  ProcedureResult failed() {
        return failed("失败");
    }


    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ProcedureResult{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public boolean isSuccess() {
        return this.status.equals(CALL_STATUS_SUCCESS);
    }
}
