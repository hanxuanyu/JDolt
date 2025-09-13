package com.hxuanyu.jdolt.model;

import java.util.List;
import java.util.Map;

/**
 * 用户端统一返回报文实体类
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class SqlExecuteResult {
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
    private SqlExecuteResult() {
    }

    /**
     * 全参构造器
     */
    private SqlExecuteResult(Integer status, String msg, List<Map<String, Object>> data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造成功消息，如果本次返回需要返回数据，则调用该方法并将数据传入
     */
    public static SqlExecuteResult success(String msg, List<Map<String, Object>> data) {
        return new SqlExecuteResult(SqlExecuteResult.CALL_STATUS_SUCCESS, msg, data);
    }

    /**
     * 构造成功消息，本方法适用于不需要数据返回的情况
     */
    public static SqlExecuteResult success(String msg) {
        return success(msg, null);
    }

    /**
     * 构造简单的成功消息，使用默认的msg
     */
    public static SqlExecuteResult success() {
        return success("成功");
    }

    /**
     * 构造失败消息，由于请求失败，调用方无法获得正确的数据，因此该类型消息的数据区为null，但是msg中必须注明失败原因，
     * 失败原因由被调用方分析并设置
     *
     * @return
     */
    public static SqlExecuteResult failed(String msg) {
        return new SqlExecuteResult(SqlExecuteResult.CALL_STATUS_FAILED, msg, null);
    }

    /**
     * 构造带有默认msg字段的失败消息
     */
    public static SqlExecuteResult failed() {
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

    /**
     * 输出SQL执行结果到控制台，以表格形式展示
     */
    public void print() {
        if (!isSuccess()) {
            System.out.println("执行失败: " + this.msg);
            return;
        }

        if (data == null || data.isEmpty()) {
            System.out.println("执行成功，数据为空");
            return;
        }

        // 获取所有列名
        List<String> columnNames = new java.util.ArrayList<>(data.get(0).keySet());

        // 计算每列的最大宽度
        Map<String, Integer> columnWidths = new java.util.HashMap<>();
        for (String column : columnNames) {
            columnWidths.put(column, column.length());
        }

        for (Map<String, Object> row : data) {
            for (String column : columnNames) {
                Object value = row.get(column);
                String valueStr = value == null ? "NULL" : value.toString();
                columnWidths.put(column, Math.max(columnWidths.get(column), valueStr.length()));
            }
        }

        // 打印表头
        StringBuilder headerLine = new StringBuilder();
        StringBuilder separator = new StringBuilder();

        for (String column : columnNames) {
            int width = columnWidths.get(column);
            headerLine.append(String.format("| %-" + width + "s ", column));
            separator.append("+-");
            for (int i = 0; i < width; i++) {
                separator.append("-");
            }
            separator.append("-");
        }
        headerLine.append("|");
        separator.append("+");

        System.out.println(separator);
        System.out.println(headerLine);
        System.out.println(separator);

        // 打印数据行
        for (Map<String, Object> row : data) {
            StringBuilder dataLine = new StringBuilder();
            for (String column : columnNames) {
                Object value = row.get(column);
                String valueStr = value == null ? "NULL" : value.toString();
                dataLine.append(String.format("| %-" + columnWidths.get(column) + "s ", valueStr));
            }
            dataLine.append("|");
            System.out.println(dataLine);
        }

        System.out.println(separator);
        System.out.println("共计 " + data.size() + " 条记录");
    }

    /**
     * 输出SQL执行结果到控制台，以CSV格式展示
     *
     * @param delimiter 分隔符
     */
    public void print(String delimiter) {
        if (!isSuccess()) {
            System.out.println("执行失败: " + this.msg);
            return;
        }

        if (data == null || data.isEmpty()) {
            System.out.println("执行成功，但没有返回数据");
            return;
        }

        // 获取所有列名
        List<String> columnNames = new java.util.ArrayList<>(data.get(0).keySet());

        // 打印表头
        System.out.println(String.join(delimiter, columnNames));

        // 打印数据行
        for (Map<String, Object> row : data) {
            StringBuilder dataLine = new StringBuilder();
            for (int i = 0; i < columnNames.size(); i++) {
                Object value = row.get(columnNames.get(i));
                String valueStr = value == null ? "" : value.toString();
                dataLine.append(valueStr);
                if (i < columnNames.size() - 1) {
                    dataLine.append(delimiter);
                }
            }
            System.out.println(dataLine);
        }

        System.out.println("共计 " + data.size() + " 条记录");
    }

    /**
     * 输出SQL执行结果到控制台，以CSV格式展示，使用逗号作为分隔符
     */
    public void printCsv() {
        print(",");
    }

    /**
     * 输出SQL执行结果到控制台，以CSV格式展示，使用制表符作为分隔符
     */
    public void printTsv() {
        print("\t");
    }

    /**
     * 输出SQL执行结果到控制台，使用自定义格式
     *
     * @param useHeader 是否显示表头
     * @param delimiter 分隔符
     * @param showCount 是否显示记录总数
     */
    public void print(boolean useHeader, String delimiter, boolean showCount) {
        if (!isSuccess()) {
            System.out.println("执行失败: " + this.msg);
            return;
        }

        if (data == null || data.isEmpty()) {
            System.out.println("执行成功，但没有返回数据");
            return;
        }

        // 获取所有列名
        List<String> columnNames = new java.util.ArrayList<>(data.get(0).keySet());

        // 打印表头
        if (useHeader) {
            System.out.println(String.join(delimiter, columnNames));
        }

        // 打印数据行
        for (Map<String, Object> row : data) {
            StringBuilder dataLine = new StringBuilder();
            for (int i = 0; i < columnNames.size(); i++) {
                Object value = row.get(columnNames.get(i));
                String valueStr = value == null ? "" : value.toString();
                dataLine.append(valueStr);
                if (i < columnNames.size() - 1) {
                    dataLine.append(delimiter);
                }
            }
            System.out.println(dataLine);
        }

        if (showCount) {
            System.out.println("共计 " + data.size() + " 条记录");
        }
    }

    /**
     * 将SQL执行结果转换为JSON字符串
     *
     * @return JSON格式的结果字符串
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        // 添加status字段
        json.append("\"status\":").append(status).append(",");
        
        // 添加msg字段
        json.append("\"msg\":\"").append(escapeJsonString(msg)).append("\",");
        
        // 添加data字段
        json.append("\"data\":");
        if (data == null) {
            json.append("null");
        } else {
            json.append("[");
            for (int i = 0; i < data.size(); i++) {
                if (i > 0) {
                    json.append(",");
                }
                Map<String, Object> row = data.get(i);
                json.append("{");
                boolean first = true;
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    if (!first) {
                        json.append(",");
                    }
                    first = false;
                    json.append("\"").append(escapeJsonString(entry.getKey())).append("\":");
                    Object value = entry.getValue();
                    if (value == null) {
                        json.append("null");
                    } else if (value instanceof String) {
                        json.append("\"").append(escapeJsonString(value.toString())).append("\"");
                    } else if (value instanceof Number || value instanceof Boolean) {
                        json.append(value.toString());
                    } else {
                        json.append("\"").append(escapeJsonString(value.toString())).append("\"");
                    }
                }
                json.append("}");
            }
            json.append("]");
        }
        
        json.append("}");
        return json.toString();
    }

    /**
     * 输出SQL执行结果到控制台，以JSON格式展示
     */
    public void printJson() {
        System.out.println(toJson());
    }

    /**
     * 转义JSON字符串中的特殊字符
     *
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private String escapeJsonString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
