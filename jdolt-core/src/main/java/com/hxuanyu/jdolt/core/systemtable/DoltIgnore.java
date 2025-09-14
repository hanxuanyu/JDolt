package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_ignore}
 *
 * {@code dolt_ignore} 存储一组“表名模式”，以及每个模式对应的布尔标志，用于指示匹配该模式的表是否不应被暂存用于提交。
 *
 * 这仅影响新表的暂存。已经被暂存或提交的表不受 {@code dolt_ignore} 内容的影响，这些表的更改仍然可以被暂存。
 *
 * ### 模式
 *
 * <pre>{@code
 * +------------+---------+------+-----+
 * | Field      | Type    | Null | Key |
 * +------------+---------+------+-----+
 * | pattern    | text    | NO   | PRI |
 * | ignored    | tinyint | NO   |     |
 * +------------+---------+------+-----+
 * }</pre>
 *
 * ### 说明
 *
 * 模式的格式是 gitignore 模式的简化版本：
 *
 * - 星号“*”或百分号“%”匹配任意数量的字符。
 * - 字符“?”匹配任意单个字符。
 * - 其他所有字符精确匹配。
 *
 * 如果某个表名同时匹配多个对 {@code ignored} 取值不同的模式，则选择最具体的模式（若所有匹配 A 的名称也都匹配 B，但反之不成立，则模式 A 比模式 B 更具体）。如果不存在最具体的模式，那么尝试暂存该表时将导致错误。
 *
 * 匹配 {@code dolt_ignore} 中模式的表可以通过向 {@code dolt add} 或 {@code CALL dolt_add} 传递 {@code --force} 标志来强制提交。
 *
 * {@code dolt diff} 不会显示被忽略的表，{@code dolt show} 也不会显示，除非额外传入 {@code --ignored} 标志。
 *
 * ### 示例查询
 *
 * <pre>{@code
 * INSERT INTO dolt_ignore VALUES ("generated_*", true), ("generated_exception", false);
 * CREATE TABLE foo (pk int);
 * CREATE TABLE generated_foo (pk int);
 * CREATE TABLE generated_exception (pk int);
 * CALL dolt_add("-A");
 * SELECT *
 * FROM dolt_status
 * WHERE staged=true;
 * }</pre>
 *
 * <pre>{@code
 * +---------------------+--------+-----------+
 * | table_name          | staged | status    |
 * +---------------------+--------+-----------+
 * | foo                 | true   | new table |
 * | generated_exception | true   | new table |
 * +---------------------+--------+-----------+
 * }</pre>
 */
public class DoltIgnore extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltIgnore> INSTANCES = new ConcurrentHashMap<>();

    protected DoltIgnore(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltIgnore getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltIgnore(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_ignore");
    }
}