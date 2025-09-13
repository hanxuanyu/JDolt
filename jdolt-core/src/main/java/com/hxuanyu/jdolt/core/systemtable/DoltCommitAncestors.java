package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;
/**
 * ## {@code dolt_commit_ancestors}
 *
 * {@code dolt_commit_ancestors} 表记录数据库中每个提交的祖先。每个提交有一个或两个祖先；合并提交（merge commit）则有两个。
 *
 * ### 架构（Schema）
 *
 * 每个提交哈希在该表中有一条或两条记录，取决于它是否有一个或两个父提交。数据库的根提交（root commit）其父提交为 {@code NULL}。
 * 对于合并提交，合并基（merge base）的 {@code parent_index} 为 0，被合并的提交的 {@code parent_index} 为 1。
 *
 * <pre><code class="language-text">
 * +--------------+------+------+-----+---------+-------+
 * | Field        | Type | Null | Key | Default | Extra |
 * +--------------+------+------+-----+---------+-------+
 * | commit_hash  | text | NO   | PRI |         |       |
 * | parent_hash  | text | NO   | PRI |         |       |
 * | parent_index | int  | NO   | PRI |         |       |
 * +--------------+------+------+-----+---------+-------+
 * </code></pre>
 */
public class DoltCommitAncestors extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommitAncestors> INSTANCES = new ConcurrentHashMap<>();

    protected DoltCommitAncestors(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommitAncestors getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommitAncestors(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_commit_ancestors");
    }
}