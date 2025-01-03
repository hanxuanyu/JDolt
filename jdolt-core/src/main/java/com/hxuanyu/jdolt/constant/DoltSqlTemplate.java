package com.hxuanyu.jdolt.constant;

/**
 * dolt相关sql模板，用于prepared statement 拼装sql语句
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltSqlTemplate {

    /**
     * 将新增的表添加到暂存区
     * - '-A': 将所有新增的表添加至暂存区
     * - '.': 将所有新增的表添加至暂存区区
     * - 'table1', 'table2' ...: 将指定的表列表添加至暂存区
     */
    public static final String SQL_DOLT_ADD = "CALL DOLT_ADD(?)";

    /**
     * 将当前数据推送到指定的备份源或从备份源恢复数据
     * - 'sync', 'name': 将数据备份至已经存在的备份库
     * - 'sync-url', 'http-url': 将数据备份至指定的http地址
     * - 'add', 'backup-name', 'http-url' : 新增备份库
     * - 'remove', 'backup-name': 删除备份库
     * - 'restore', 'backup-name': 从指定备份库删除
     */
    public static final String SQL_DOLT_BACKUP = "CALL DOLT_BACKUP(?)";

    /**
     * 分支新增、删除等操作
     * - ''
     */
    public static final String SQL_DOLT_BRANCH = "CALL DOLT_BRANCH(?)";

}


