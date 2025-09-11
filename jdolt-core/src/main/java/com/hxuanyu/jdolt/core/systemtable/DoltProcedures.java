package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `dolt_procedures`
 *
 * `dolt_procedures` 存储在数据库中创建的每个存储过程。
 *
 * 此表中的值是与存储过程存储实现相关的内部细节。建议使用内置 SQL 语句来检查和修改存储过程，而不是直接使用此表。
 *
 * ### 架构
 *
 * <pre>
 * +-------------+----------+
 * | field       | type     |
 * +-------------+----------+
 * | name        | longtext |
 * | create_stmt | longtext |
 * | created_at  | datetime |
 * | modified_at | datetime |
 * | sql_mode    | longtext |
 * +-------------+----------+
 * </pre>
 *
 * 使用标准的 `CREATE PROCEDURE` 流程时，`name` 列将始终为小写。Dolt 因此假设 `name` 始终为小写，若手动插入存储过程，也必须使用小写的 `name`。否则，它将对某些操作（例如 `DROP PROCEDURE`）不可见。
 *
 * ### 示例查询
 *
 * <pre>
 * CREATE PROCEDURE simple_proc1(x DOUBLE, y DOUBLE) SELECT x*y;
 * CREATE PROCEDURE simple_proc2() SELECT name FROM category;
 * </pre>
 *
 * {@embed url="https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+*+FROM+dolt_procedures%3B"}
 */
public class DoltProcedures extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltProcedures> INSTANCES = new ConcurrentHashMap<>();

    protected DoltProcedures(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltProcedures getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltProcedures(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_procedures");
    }
}