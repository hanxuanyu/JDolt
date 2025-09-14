package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `dolt_column_diff`
 *
 * `dolt_column_diff` 系统表展示了当前数据库中，从活动分支的 HEAD 可达的每个提交中，哪些表和列发生了更改。若单个提交中修改了多个列，则 `dolt_column_diff` 系统表会为每个列生成一行记录，且它们的提交哈希相同。工作集中已暂存的更改会以 `commit_hash` 值为 `STAGED` 的形式包含在内。工作集中未暂存的更改会以 `commit_hash` 值为 `WORKING` 的形式包含在内。
 *
 * ### 架构（Schema）
 *
 * `DOLT_COLUMN_DIFF` 系统表包含以下列
 *
 * <pre><code class="language-text">
 * +-------------+----------+
 * | field       | Type     |
 * +-------------+----------+
 * | commit_hash | text     |
 * | table_name  | text     |
 * | column_name | text     |
 * | committer   | text     |
 * | email       | text     |
 * | date        | datetime |
 * | message     | text     |
 * | diff_type   | text     |
 * +-------------+----------+
 * </code></pre>
 *
 * ### 查询说明
 *
 * `dolt_column_diff` 展示从当前分支 HEAD 出发（包含任何工作集更改）的变更。如果某个提交没有对表做任何更改（例如空提交），则不会在 `dolt_column_diff` 的结果中出现。
 *
 * ### 示例查询
 *
 * 以来自 DoltHub 的 `first-hour-db` 数据库为例，下列查询使用 `dolt_column_diff` 系统表来查找列 `name` 被更新的提交与表。
 *
 * {% embed url="[https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+commit_hash%2C+date%0AFROM+dolt_column_diff+where+column_name+%3D+%27name%27%0A%3B%0A](https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+commit_hash%2C+date%0AFROM+dolt_column_diff+where+column_name+%3D+%27name%27%0A%3B%0A)" %}
 *
 * 如果我们聚焦到 `dolt_schemas` 表，可以统计在所有提交过程中，每个列被更新的次数。
 *
 * {% embed url="[https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+column_name%2C+count%28commit_hash%29+as+total_column_changes%0AFROM+dolt_column_diff%0AWHERE+table_name+%3D+%27dolt_schemas%27%0AGROUP+BY+column_name%3B](https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=SELECT+column_name%2C+count%28commit_hash%29+as+total_column_changes%0AFROM+dolt_column_diff%0AWHERE+table_name+%3D+%27dolt_schemas%27%0AGROUP+BY+column_name%3B)" %}
 *
 * 从这些结果可以看出，描述在押原因的字段更新频率远高于记录在押人员人口统计信息的字段。
 */
public class DoltColumnDiff extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltColumnDiff> INSTANCES = new ConcurrentHashMap<>();

    protected DoltColumnDiff(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltColumnDiff getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltColumnDiff(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_column_diff");
    }
}