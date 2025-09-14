package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## {@code dolt_workspace_$TABLENAME}
 *
 * 此系统表显示工作区中哪些行发生了更改，以及它们是否已被暂存（staged）。 它是从 HEAD 到 STAGED，以及从 STAGED 到 WORKING 的更改行的并集。任何在{@code dolt_status} 表中列出的表，都会有一个非空的对应 {@code dolt_workspace_$TABLENAME} 表。列出的更改 都相对于当前分支的 HEAD。
 *
 * 这些表可以被修改，用于更新哪些更改将被暂存以供提交。{@link https://www.dolthub.com/blog/2024-08-16-workspace-review/ 工作区审查}
 *
 * ### 模式（Schema）
 *
 * 源表的模式会影响工作区表的模式。前三列总是相同的，然后使用源表的模式来创建 {@code to_} 和 {@code from_} 列。
 *
 * {@code dolt_workspace_$TABLENAME} 中的每一行对应于源表中的单行更新。
 *
 * <pre><code class="language-sql">
 * +------------------+----------+
 * | field            | type     |
 * +------------------+----------+
 * | id               | int      |
 * | staged           | bool     |
 * | diff_type        | varchar  |
 * | to_x             | ...      |
 * | to_y             | ...      |
 * | from_x           | ...      |
 * | from_y           | ...      |
 * +------------------+----------+
 * </code></pre>
 *
 * 当下次调用 {@code dolt_commit()} 将要提交这些更改时，{@code staged} 列为 {@code TRUE}。{@code staged = FALSE} 的更改存在于你的工作区中，这意味着你会在会话中的所有查询里看到它们，但在执行{@code dolt_commit()} 时它们不会被记录。
 *
 * 有两种方式可以通过这些表改变你的工作区状态。
 *
 * <ul>
 *   <li>可以切换任意行的 {@code staged} 列。若从 false 改为 true，则该行的值会被移入暂存区（staging）。如果该行已经有暂存的更改，它们会被覆盖。若从 true 改为 false，则该行会被取消暂存。如果该行在工作区中还有其他更改，工作区中的更改会被保留，而暂存的更改会被丢弃。</li>
 *   <li>对于任何 {@code staged = FALSE} 的行，你都可以删除它。这将导致源表中该行的更改被还原。</li>
 * </ul>
 *
 * ### 示例查询
 *
 * <pre><code class="language-sql">
 * SELECT *
 * FROM dolt_workspace_mytable
 * WHERE staged=false
 * </code></pre>
 *
 * <pre><code class="language-sql">
 * +----+--------+-----------+-------+----------+---------+------------+
 * | id | staged | diff_type | to_id | to_value | from_id | from_value |
 * +----+--------+-----------+-------+----------+---------+------------+
 * | 0  | false  | modified  | 3     | 44       | 3       | 31         |
 * | 1  | false  | modified  | 4     | 68       | 4       | 1          |
 * | 2  | false  | modified  | 9     | 47       | 9       | 59         |
 * +----+--------+-----------+-------+----------+---------+------------+
 * 3 rows in set (0.00 sec)
 * </code></pre>
 *
 * <pre><code class="language-sql">
 * UPDATE dolt_workspace_mytable SET staged = TRUE WHERE to_id = 3;
 * CALL dolt_commit("-m", "Added row id 3 in my table");
 * </code></pre>
 *
 * ### 备注
 *
 * {@code dolt_workspace_$TABLENAME} 表是在检查时基于会话状态生成的， 因此在一个有多个编辑者的分支上，它们不能被视为稳定。
 */
public class DoltWorkspace extends DoltSystemTableWithSuffix {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltWorkspace> INSTANCES = new ConcurrentHashMap<>();

    protected DoltWorkspace(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltWorkspace getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltWorkspace(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_workspace");
    }
}