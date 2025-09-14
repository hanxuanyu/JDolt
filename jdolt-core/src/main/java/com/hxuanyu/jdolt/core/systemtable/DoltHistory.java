package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `dolt_history_$TABLENAME`
 *
 * 对于每个名为 `$TABLENAME` 的用户表，都会有一个只读的系统表 `dolt_history_$TABLENAME`，可用于在当前分支的历史中查询某行在每个提交中的取值。
 *
 * ### 架构（Schema）
 *
 * 每个 Dolt 历史表都包含 `commit_hash`、`committer` 和 `commit_date` 列，以及当前检出分支上用户表架构中的所有列。
 *
 * <pre>
 * +-------------+----------+
 * | field       | type     |
 * +-------------+----------+
 * | commit_hash | TEXT     |
 * | committer   | TEXT     |
 * | commit_date | DATETIME |
 * | other cols  |          |
 * +-------------+----------+
 * </pre>
 *
 * ### 示例架构
 *
 * 考虑一个名为 `mytable` 的表，其架构如下：
 *
 * <pre>
 * +------------+--------+
 * | field      | type   |
 * +------------+--------+
 * | x          | INT    |
 * +------------+--------+
 * </pre>
 *
 * `dolt_history_mytable` 的架构将为：
 *
 * <pre>
 * +-------------+----------+
 * | field       | type     |
 * +-------------+----------+
 * | x           | INT      |
 * | commit_hash | TEXT     |
 * | committer   | TEXT     |
 * | commit_date | DATETIME |
 * +-------------+----------+
 * </pre>
 *
 * ### 示例查询
 *
 * 假设数据库中包含上述 `mytable` 表，并具有以下提交图：
 *
 * <pre>
 *    B---E  feature
 *   /
 *  A---C---D  main
 * </pre>
 *
 * 当检出 `feature` 分支时，下面的查询将返回如下结果，显示从当前分支可达的每个祖先提交中的行。
 *
 * {% embed url="https://www.dolthub.com/repositories/dolthub/docs_examples/embed/feature?q=SELECT+*+FROM+dolt_history_mytable%3B" %}
 */
public class DoltHistory extends DoltSystemTableWithSuffix {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltHistory> INSTANCES = new ConcurrentHashMap<>();

    protected DoltHistory(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltHistory getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltHistory(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_history");
    }
}