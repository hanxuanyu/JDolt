package com.hxuanyu.jdolt.util;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class SqlUtils {
    public static String formatCommitSql(String message) {
        return "DOLT_COMMIT('" + message.replace("'", "''") + "')";
    }
}