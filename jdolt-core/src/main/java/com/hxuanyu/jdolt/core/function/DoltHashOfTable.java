package com.hxuanyu.jdolt.core.function;

import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * `DOLT_HASHOF_TABLE()` 函数返回一个表的值哈希。该哈希是表中所有行的哈希值，并且依赖于它们的序列化格式。
 * 因此，即使一个表包含相同的行，如果序列化格式发生了变化，其哈希值也可能不同。然而，如果表的哈希值没有变化，
 * 则可以保证该表的数据没有发生变化。
 * <p>
 * 此函数可以用于监测数据的变化，通过在应用程序中存储之前的哈希值并将其与当前哈希值进行比较。例如，
 * 您可以使用此函数获取名为 `color` 的表的哈希值，如下所示：
 *
 * <pre>{@code
 * mysql> SELECT dolt_hashof_table('color');
 * +----------------------------------+
 * | dolt_hashof_table('color')       |
 * +----------------------------------+
 * | q8t28sb3h5g2lnhiojacpi7s09p4csjv |
 * +----------------------------------+
 * 1 row in set (0.01 sec)
 * }</pre>
 */
public class DoltHashOfTable extends DoltRepository implements DoltFunction<DoltHashOfTable.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltHashOfTable> INSTANCES = new ConcurrentHashMap<>();

    private DoltHashOfTable(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltHashOfTable getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltHashOfTable(connectionManager));
    }

    public static class Params extends AbstractFunctionParamBuilder<Params> {

        protected Params(DoltFunction<Params> doltFunction) {
            super(Params.class, doltFunction);
        }

        @MethodInvokeRequired
        @MethodMutexGroup({"withTable"})
        public Params withTable(String table) {
            validator.checkAndMark("withTable");
            addFlags(table);
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getFunctionTemplate("dolt_hashof_table"), params);
    }

}