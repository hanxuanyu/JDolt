package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## {@code dolt_backups}
 *
 * {@code dolt_backups} 返回 {@code repo_state.json} 中 backups 的内容，类似于在命令行运行 {@code dolt backup -v}。
 *
 * {@code dolt_backups} 表当前为只读。请使用 {@code dolt_backup()} 过程来添加、更新或删除备份。
 *
 * ### 模式（Schema）
 *
 * <pre>
 * +-------------+------+------+-----+---------+-------+
 * | Field       | Type | Null | Key | Default | Extra |
 * +-------------+------+------+-----+---------+-------+
 * | name        | text | NO   | PRI |         |       |
 * | url         | text | NO   |     |         |       |
 * +-------------+------+------+-----+---------+-------+
 * </pre>
 *
 * ### 示例查询
 *
 * {@code sql SELECT * FROM dolt_backups;}
 *
 * <pre>
 * +-------------+----------------------------------------+
 * | name        | url                                    |
 * +-------------+----------------------------------------+
 * | backup-west | aws://[ddb-westtable:s3bucket-west]/db |
 * | backup-east | aws://[ddb-easttable:s3bucket-east]/db |
 * | backup-local| file:///path/to/local/backup           |
 * +-------------+----------------------------------------+
 * </pre>
 */
public class DoltBackups extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltBackups> INSTANCES = new ConcurrentHashMap<>();

    protected DoltBackups(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltBackups getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltBackups(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_backups");
    }
}