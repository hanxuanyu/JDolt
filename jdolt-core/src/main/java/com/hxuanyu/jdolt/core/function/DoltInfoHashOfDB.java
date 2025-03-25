package com.hxuanyu.jdolt.core.function;

import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_HASHOF_DB()`
 * <p>
 * `DOLT_HASHOF_DB()` 函数返回整个版本化数据库的哈希值。这个哈希值是数据库中所有表（包括模式和数据）的哈希值，并且包含其他版本化项目，例如存储过程和触发器。
 * 该哈希值不包括未版本化的项目，例如被<a href="dolt-system-tables.md#dolt_ignore">忽略</a>的表。该函数可以接受一个可选参数，用于指定一个分支或以下值之一：'STAGED'、'WORKING' 或 'HEAD'（默认情况下，不传入参数等同于 'WORKING'）。
 * <p>
 * 此函数可用于通过在您的应用程序中存储先前的哈希值并与当前哈希值进行比较来监控数据库的变化。例如，您可以使用此函数获取整个数据库的哈希值，如下所示：
 *
 * <pre><code class="language-sql">
 * mysql> SELECT dolt_hashof_db();
 * +----------------------------------+
 * | dolt_hashof_db()                 |
 * +----------------------------------+
 * | 1q8t28sb3h5g2lnhiojacpi7s09p4csj |
 * +----------------------------------+
 * </code></pre>
 * <p>
 * 需要注意的是，如果您连接到分支 'main' 并调用 `dolt_hashof_db('feature')`，则可能会得到与连接到分支 'feature' 并调用 `dolt_hashof_db()` 不同的哈希值。
 * 这种情况发生在分支 'feature' 的工作集存在尚未提交的更改时。在分支 'main' 上调用 `dolt_hashof_db('feature')` 等同于在分支 'feature' 上调用 `dolt_hashof_db('HEAD')`。
 * <p>
 * 一般建议，当需要检测数据库的变化时，先连接到您需要使用的分支，然后调用不带参数的 `dolt_hashof_db()`。任何哈希值的变化都意味着数据库发生了更改。
 */
public class DoltInfoHashOfDB extends DoltRepository implements DoltInfoFunction<DoltInfoHashOfDB.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltInfoHashOfDB> INSTANCES = new ConcurrentHashMap<>();

    private DoltInfoHashOfDB(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltInfoHashOfDB getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltInfoHashOfDB(connectionManager));
    }

    public static class Params extends AbstractFunctionParamBuilder<Params> {

        protected Params(DoltInfoFunction<Params> doltInfoFunction) {
            super(Params.class, doltInfoFunction);
        }

        @MethodExclusive
        public Params withBranch(String branch) {
            validator.checkAndMark("withBranch");
            addFlags(branch);
            return this;
        }

        @MethodExclusive
        public Params withWorking() {
            validator.checkAndMark("withWorking");
            addFlags("WORKING");
            return this;
        }

        @MethodExclusive
        public Params withHead() {
            validator.checkAndMark("withHead");
            addFlags("HEAD");
            return this;
        }

        @MethodExclusive
        public Params withStaged() {
            validator.checkAndMark("withStaged");
            addFlags("STAGED");
            return this;
        }
    }

    @Override
    public Params prepare() {
        return new Params(this);
    }



    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.selectFunction("dolt_hashof_db")
                .withParams(params)
                .build();
    }

}