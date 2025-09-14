package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;
/**
 * ## `dolt_statistics`
 *
 * `dolt_statistics` 包含当前已收集的数据库统计信息。此信息存储在提交图之外，不受版本语义的影响。
 *
 * ### 模式（Schema）
 *
 * <pre><code class="language-sql">
 * +-----------------+----------+------+-----+---------+-------+
 * | Field           | Type     | Null | Key | Default | Extra |
 * +-----------------+----------+------+-----+---------+-------+
 * | database_name   | text     | NO   | PRI | NULL    |       |
 * | table_name      | text     | NO   | PRI | NULL    |       |
 * | index_name      | text     | NO   | PRI | NULL    |       |
 * | row_count       | bigint   | NO   |     | NULL    |       |
 * | distinct_count  | bigint   | NO   |     | NULL    |       |
 * | null_count      | bigint   | NO   |     | NULL    |       |
 * | columns         | text     | NO   |     | NULL    |       |
 * | types           | text     | NO   |     | NULL    |       |
 * | upper_bound     | text     | NO   |     | NULL    |       |
 * | upper_bound_cnt | bigint   | NO   |     | NULL    |       |
 * | created_at      | datetime | NO   |     | NULL    |       |
 * | mcv1            | text     | NO   |     | NULL    |       |
 * | mcv2            | text     | NO   |     | NULL    |       |
 * | mcv3            | text     | NO   |     | NULL    |       |
 * | mcv4            | text     | NO   |     | NULL    |       |
 * | mcvCounts       | text     | NO   |     | NULL    |       |
 * +-----------------+----------+------+-----+---------+-------+
 * </code></pre>
 */
public class DoltStatistics extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltStatistics> INSTANCES = new ConcurrentHashMap<>();

    protected DoltStatistics(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltStatistics getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltStatistics(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_statistics");
    }
}