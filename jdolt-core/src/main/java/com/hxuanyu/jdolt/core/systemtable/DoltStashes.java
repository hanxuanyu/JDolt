package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_stashes}
 *
 * {@code dolt_stashes} 系统表返回当前数据库中所有暂存（stash）的信息。
 *
 * {@code dolt_stashes} 表是只读的。使用 {@link dolt_stash()} 存储过程或 {@code dolt stash} 命令行函数来创建、应用或删除暂存。
 *
 * <h3>架构</h3>
 *
 * <pre>{@code
 * +----------------+------+------+-----+
 * | Field          | Type | Null | Key |
 * +----------------+------+------+-----+
 * | name           | text | NO   |     |
 * | stash_id       | text | NO   |     |
 * | branch         | text | NO   |     |
 * | hash           | text | NO   |     |
 * | commit_message | text | NO   |     |
 * +----------------+------+------+-----+
 * }</pre>
 *
 * <h3>字段说明</h3>
 * <ul>
 *   <li>{@code name}: 暂存条目的名称</li>
 *   <li>{@code stash_id}: 暂存的唯一标识符（例如："stash@{0}"）</li>
 *   <li>{@code branch}: 创建该暂存的分支</li>
 *   <li>{@code hash}: 被暂存更改的提交哈希</li>
 *   <li>{@code commit_message}: 创建该暂存时所在提交的消息</li>
 * </ul>
 *
 * <h3>示例查询</h3>
 *
 * <pre>{@code
 * SELECT *
 * FROM dolt_stashes
 * WHERE name = 'myStash';
 * }</pre>
 *
 * <pre>{@code
 * +---------+------------+--------+----------------------------------+------------------+
 * | name    | stash_id   | branch | hash                             | commit_message   |
 * +---------+------------+--------+----------------------------------+------------------+
 * | myStash | stash@{0}  |  main  | pnpq4p07977jjbpkg6ojj2mpjp2kru9r | Created a table  |
 * +---------+------------+--------+----------------------------------+------------------+
 * }</pre>
 */
public class DoltStashes extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltStashes> INSTANCES = new ConcurrentHashMap<>();

    protected DoltStashes(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltStashes getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltStashes(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_stashes");
    }
}