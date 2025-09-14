package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_merge_status}
 *
 * {@code dolt_merge_status} 系统表用于告知用户当前是否存在进行中的合并。其架构如下：
 *
 * <pre><code class="language-sql">
 * CREATE TABLE `dolt_merge_status` (
 *   -- 是否正在进行合并
 *   `is_merging` tinyint NOT NULL,
 *   -- 启动合并时使用的提交规范（commit spec）
 *   `source` text,
 *   -- 在合并时，提交规范解析到的提交
 *   `source_commit` text,
 *   -- 目标工作集
 *   `target` text,
 *   -- 存在冲突或约束违规的表列表
 *   `unmerged_tables` text
 * )
 * </code></pre>
 *
 * ### 示例
 *
 * 让我们创建一个简单的冲突：
 *
 * <pre><code class="language-bash">
 * dolt sql -q "CREATE TABLE t (a INT PRIMARY KEY, b INT);"
 * dolt add .
 * dolt commit -am "base"
 *
 * dolt checkout -b right
 * dolt sql <<SQL
 * ALTER TABLE t ADD c INT;
 * INSERT INTO t VALUES (1, 2, 1);
 * SQL
 * dolt commit -am "right"
 *
 * dolt checkout main
 * dolt sql -q "INSERT INTO t values (1, 3);"
 * dolt commit -am "left"
 *
 * dolt merge right
 * </code></pre>
 *
 * 执行 {@code SELECT * from dolt_merge_status;} 的结果：
 *
 * <pre><code>
 * +------------+--------+----------------------------------+-----------------+-----------------+
 * | is_merging | source | source_commit                    | target          | unmerged_tables |
 * +------------+--------+----------------------------------+-----------------+-----------------+
 * | true       | right  | fbghslue1k9cfgbi00ti4r8417frgbca | refs/heads/main | t               |
 * +------------+--------+----------------------------------+-----------------+-----------------+
 * </code></pre>
 */
public class DoltMergeStatus extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltMergeStatus> INSTANCES = new ConcurrentHashMap<>();

    protected DoltMergeStatus(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltMergeStatus getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltMergeStatus(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_merge_status");
    }
}