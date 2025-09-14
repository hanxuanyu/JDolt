package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `dolt_remote_branches`
 *
 * `dolt_remote_branches` 包含你已获取的远程分支的信息。它的架构与 `dolt_branches` 类似，但在该上下文中 `remote`、`branch` 和 `dirty` 列没有意义，因此不包含。此表仅包含远程分支。
 *
 * ### 架构
 *
 * <pre>{@code
 * +------------------------+----------+
 * | Field                  | Type     |
 * +------------------------+----------+
 * | name                   | TEXT     |
 * | hash                   | TEXT     |
 * | latest_committer       | TEXT     |
 * | latest_committer_email | TEXT     |
 * | latest_commit_date     | DATETIME |
 * | latest_commit_message  | TEXT     |
 * +------------------------+----------+
 * }</pre>
 *
 * ### 示例查询
 *
 * 通过一个查询获取所有本地和远程分支。远程分支的名称将带有前缀 `remotes/<remoteName>`。
 *
 * <pre>{@code
 * SELECT *
 * FROM dolt_branches
 * UNION
 * SELECT * FROM dolt_remote_branches;
 * }</pre>
 *
 * <pre>{@code
 * +-----------------+----------------------------------+------------------+------------------------+-------------------------+----------------------------+
 * | name            | hash                             | latest_committer | latest_committer_email | latest_commit_date      | latest_commit_message      |
 * +-----------------+----------------------------------+------------------+------------------------+-------------------------+----------------------------+
 * | main            | r3flrdqk73lkcrugtbohcdbb3hmr2bev | Zach Musgrave    | zach@dolthub.com       | 2023-02-01 18:59:55.156 | Initialize data repository |
 * | remotes/rem1/b1 | r3flrdqk73lkcrugtbohcdbb3hmr2bev | Zach Musgrave    | zach@dolthub.com       | 2023-02-01 18:59:55.156 | Initialize data repository |
 * +-----------------+----------------------------------+------------------+------------------------+-------------------------+----------------------------+
 * }</pre>
 */
public class DoltRemoteBranches extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltRemoteBranches> INSTANCES = new ConcurrentHashMap<>();

    protected DoltRemoteBranches(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltRemoteBranches getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltRemoteBranches(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_remote_branches");
    }
}