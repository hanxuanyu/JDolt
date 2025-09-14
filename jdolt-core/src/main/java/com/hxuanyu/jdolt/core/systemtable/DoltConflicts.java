package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * <h2><code>dolt_conflicts</code></h2>
 *
 * <p>
 * <code>dolt_conflicts</code> 是一个系统表，其中为工作集中每个存在未解决合并冲突的表提供一行记录。
 * </p>
 *
 * <pre><code class="language-sql">
 * +---------------+-----------------+------+-----+---------+-------+
 * | Field         | Type            | Null | Key | Default | Extra |
 * +---------------+-----------------+------+-----+---------+-------+
 * | table         | text            | NO   | PRI |         |       |
 * | num_conflicts | bigint unsigned | NO   |     |         |       |
 * +---------------+-----------------+------+-----+---------+-------+
 * </code></pre>
 *
 * <p>
 * 在 SQL 会话中解决冲突时查询此表。有关在 SQL 中解决合并冲突的更多信息，请参阅
 * <a href="chrome-extension://difoiogjjojoaoomphldepapgpbgkhkb/standalone.html#dolt_conflicts_usdtablename">dolt_conflicts_$TABLENAME</a>
 * 表的文档。
 * </p>
 */
public class DoltConflicts extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltConflicts> INSTANCES = new ConcurrentHashMap<>();

    protected DoltConflicts(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltConflicts getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltConflicts(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_conflicts");
    }
}