package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_status}
 *
 * {@code dolt_status} 返回数据库会话的状态，类似于在命令行运行 {@code dolt status}。
 *
 * ### 架构
 *
 * <pre>{@code
 * +------------+---------+------+-----+
 * | Field      | Type    | Null | Key |
 * +------------+---------+------+-----+
 * | table_name | text    | NO   | PRI |
 * | staged     | tinyint | NO   | PRI |
 * | status     | text    | NO   | PRI |
 * +------------+---------+------+-----+
 * }</pre>
 *
 * ### 示例查询
 *
 * <pre>{@code
 * SELECT *
 * FROM dolt_status
 * WHERE staged=false;
 * }</pre>
 *
 * <pre>{@code
 * +------------+--------+-----------+
 * | table_name | staged | status    |
 * +------------+--------+-----------+
 * | one_pk     | false  | new table |
 * +------------+--------+-----------+
 * }</pre>
 */
public class DoltStatus extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltStatus> INSTANCES = new ConcurrentHashMap<>();

    protected DoltStatus(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltStatus getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltStatus(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_status");
    }
}