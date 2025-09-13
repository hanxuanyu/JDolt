package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.util.builder.AbstractSystemTableParamBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## {@code dolt_blame_$tablename}
 *
 * 对于每个具有主键的用户表，都会有一个名为 {@code dolt_blame_$tablename} 的可查询系统视图，可用于查看每行当前值由哪个用户和哪个提交负责。
 * 它等价于 {@code dolt blame} CLI 命令。没有主键的表不会有对应的 {@code dolt_blame_$tablename}。
 *
 * ### 架构（Schema）
 *
 * {@code dolt_blame_$tablename} 系统视图包含以下列：
 *
 * <pre><code class="text">
 * +-------------------+----------+
 * | field             | type     |
 * +-------------------+----------+
 * | commit            | text     |
 * | commit_date       | datetime |
 * | committer         | text     |
 * | email             | text     |
 * | message           | text     |
 * | primary key cols  |          |
 * +-------------------+----------+
 * </code></pre>
 *
 * 其余列依赖于用户表的架构。用户表主键中的每一列都会包含在 {@code dolt_blame_$tablename} 系统表中。
 *
 * ### 查询细节
 *
 * 对 {@code dolt_blame_$tablename} 系统视图执行 {@code SELECT *} 查询会显示底层用户表中每一行的主键列，以及最后一次修改该行的提交元数据。
 * 请注意，如果表在工作集（working set）中存在未提交的更改，这些更改不会显示在 {@code dolt_blame_$tablename} 系统视图中。
 *
 * {@code dolt_blame_$tablename} 仅对具有主键的表可用。尝试查询没有主键的表的 {@code dolt_blame_$tablename} 将返回一条错误信息。
 *
 * ### 示例查询
 *
 * 考虑以下示例表 {@code city}：
 *
 * {@code
 * https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=describe+city%3B
 * }
 *
 * 要查找是谁设置了当前值，我们可以查询 {@code dolt_blame_city} 表：
 *
 * {@code
 * https://www.dolthub.com/repositories/dolthub/first-hour-db/embed/main?q=select+*+from+dolt_blame_city+limit+20%3B
 * }
 */
public class DoltBlame extends DoltSystemTableWithSuffix {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltBlame> INSTANCES = new ConcurrentHashMap<>();

    protected DoltBlame(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltBlame getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltBlame(connectionManager));
    }

    public Params prepare() {
        return new Params(this).from("dolt_blame");
    }

}