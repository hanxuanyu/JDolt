package com.hxuanyu.jdolt.util;

/**
 * dolt相关sql模板，用于prepared statement 拼装sql语句
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltSqlTemplate {
    private static final String REPLACE_STRING = "###PARAMS_LIST###";


    public static String getProcedureTemplate(String procedureName) {
        return "CALL " + procedureName.toUpperCase() + "(" + REPLACE_STRING + ")";
    }

    public static String getFunctionTemplate(String functionName) {
        return "SELECT " + functionName.toUpperCase() + "(" + REPLACE_STRING + ")";
    }

    public static String buildSqlTemplate(String template, String... params) {
        if (params == null || params.length == 0) {
            return template.replace(REPLACE_STRING, "");
        }

        // 替换模板中的 REPLACE_STRING 为占位符字符串
        if (template == null || !template.contains(REPLACE_STRING)) {
            throw new IllegalArgumentException("template is null or does not contain " + REPLACE_STRING);
        }

        // 构造占位符字符串，例如 "?, ?, ?"
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            placeholders.append("?");
            if (i < params.length - 1) {
                placeholders.append(", ");
            }
        }
        return template.replace(REPLACE_STRING, placeholders.toString());
    }


}


