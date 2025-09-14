package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * <h2><code>dolt_schema_conflicts</code></h2>
 *
 * <p><code>dolt_schema_conflicts</code> 是一个系统表，对于工作集（working set）中每个存在未解决架构冲突的表，该表包含一行记录。</p>
 *
 * <pre><code class="language-sql">
 * &gt; SELECT table_name, description, base_schema, our_schema, their_schema FROM dolt_schema_conflicts;
 * +------------+--------------------------------------+-------------------------------------------------------------------+-------------------------------------------------------------------+-------------------------------------------------------------------+
 * | table_name | description                          | base_schema                                                       | our_schema                                                        | their_schema                                                      |
 * +------------+--------------------------------------+-------------------------------------------------------------------+-------------------------------------------------------------------+-------------------------------------------------------------------+
 * | people     | different column definitions for our | CREATE TABLE `people` (                                           | CREATE TABLE `people` (                                           | CREATE TABLE `people` (                                           |
 * |            | column age and their column age      |   `id` int NOT NULL,                                              |   `id` int NOT NULL,                                              |   `id` int NOT NULL,                                              |
 * |            |                                      |   `last_name` varchar(120),                                       |   `last_name` varchar(120),                                       |   `last_name` varchar(120),                                       |
 * |            |                                      |   `first_name` varchar(120),                                      |   `first_name` varchar(120),                                      |   `first_name` varchar(120),                                      |
 * |            |                                      |   `birthday` datetime(6),                                         |   `birthday` datetime(6),                                         |   `birthday` datetime(6),                                         |
 * |            |                                      |   `age` int DEFAULT '0',                                          |   `age` float,                                                    |   `age` bigint,                                                   |
 * |            |                                      |   PRIMARY KEY (`id`)                                              |   PRIMARY KEY (`id`)                                              |   PRIMARY KEY (`id`)                                              |
 * |            |                                      | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin; |
 * +------------+--------------------------------------+-------------------------------------------------------------------+-------------------------------------------------------------------+-------------------------------------------------------------------+
 * </code></pre>
 *
 * <p>在 SQL 会话中解决架构冲突时查询此表。关于在合并（merge）过程中解决架构冲突的更多信息，请参阅关于<a href="chrome-extension://difoiogjjojoaoomphldepapgpbgkhkb/merges.md#conflicts">冲突</a>的文档。</p>
 */
public class DoltSchemaConflicts extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltSchemaConflicts> INSTANCES = new ConcurrentHashMap<>();

    protected DoltSchemaConflicts(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltSchemaConflicts getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltSchemaConflicts(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_schema_conflicts");
    }
}