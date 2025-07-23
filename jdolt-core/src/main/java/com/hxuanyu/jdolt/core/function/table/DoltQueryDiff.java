package com.hxuanyu.jdolt.core.function.table;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractTableFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code DOLT_QUERY_DIFF()} 表函数用于计算任意两个查询之间的数据差异，生成的表类似于 {@code DOLT_DIFF()} 表函数。
 * 
 * <h3>权限要求</h3>
 * 
 * 使用 {@code DOLT_QUERY_DIFF()} 表函数需要对每个查询中使用的所有表具有 {@code SELECT} 权限。
 * 
 * <h3>参数选项</h3>
 * 
 * <pre>{@code
 * DOLT_QUERY_DIFF(<from_query>, <to_query>)
 * }</pre>
 * 
 * {@code DOLT_QUERY_DIFF()} 表函数接受两个必需参数：
 * <ul>
 *   <li>{@code from_query} — 作为差异起始点的SQL查询。</li>
 *   <li>{@code to_query} — 作为差异终止点的SQL查询。</li>
 * </ul>
 * 
 * <h3>返回模式</h3>
 * 
 * 返回的结果集包含以下列：
 * <ul>
 *   <li>对于 {@code from_query} 结果集中的每一列 {@code X}，结果集中会有一个名为 {@code from_X} 的列。</li>
 *   <li>对于 {@code to_query} 结果集中的每一列 {@code Y}，结果集中会有一个名为 {@code to_Y} 的列。</li>
 *   <li>{@code diff_type} — 表示行的差异类型，可能的值为：{@code added}、{@code modified} 或 {@code deleted}。</li>
 * </ul>
 * 
 * <h3>示例</h3>
 * 
 * 在本示例中，我们有一个名为 {@code t} 的表，分别位于两个分支 {@code main} 和 {@code other} 中。
 * 
 * 在 {@code main} 分支中，表 {@code t} 的数据如下：
 * 
 * <pre>
 * +---+----+
 * | i | j  |
 * +---+----+
 * | 0 | 0  |
 * | 1 | 10 |
 * | 3 | 3  |
 * | 4 | 4  |
 * +---+----+
 * </pre>
 * 
 * 在 {@code other} 分支中，表 {@code t} 的数据如下：
 * 
 * <pre>
 * +---+---+
 * | i | j |
 * +---+---+
 * | 0 | 0 |
 * | 1 | 1 |
 * | 2 | 2 |
 * | 4 | 4 |
 * +---+---+
 * </pre>
 * 
 * 我们可以使用 {@code DOLT_QUERY_DIFF()} 表函数计算这两个表之间的差异：
 * 
 * <pre>
 * dolt> select * from dolt_query_diff('select * from t as of main', 'select * from t as of other');
 * +--------+--------+------+------+-----------+
 * | from_i | from_j | to_i | to_j | diff_type |
 * +--------+--------+------+------+-----------+
 * | 1      | 10     | 1    | 1    | modified  |
 * | NULL   | NULL   | 2    | 2    | added     |
 * | 3      | 3      | NULL | NULL | deleted   |
 * +--------+--------+------+------+-----------+
 * 3 rows in set (0.00 sec)
 * </pre>
 * 
 * <h3>注意</h3>
 * 
 * 查询差异计算采用暴力算法，因此对于大型结果集会较慢。
 * 该算法对结果集的大小是超线性的（{@code n^2}）。
 * 随着时间推移，我们将优化算法，利用存储引擎的特性来提高性能。
 */
@MethodInvokeRequiredGroup(value = {"fromQuery", "toQuery"})
public class DoltQueryDiff extends DoltRepository implements DoltTableFunction<DoltQueryDiff.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltQueryDiff> INSTANCES = new ConcurrentHashMap<>();

    private DoltQueryDiff(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltQueryDiff getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltQueryDiff(connectionManager));
    }

    public static class Params extends AbstractTableFunctionParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        /**
         * 设置作为差异起始点的SQL查询
         *
         * @param fromQuery 起始查询
         * @return 当前参数构建器实例
         */
        public Params fromQuery(String fromQuery) {
            validator.checkAndMark("fromQuery");
            addFlags(fromQuery);
            return this;
        }

        /**
         * 设置作为差异终止点的SQL查询
         *
         * @param toQuery 终止查询
         * @return 当前参数构建器实例
         */
        @MethodDependsOn({"fromQuery"})
        public Params toQuery(String toQuery) {
            validator.checkAndMark("toQuery");
            addFlags(toQuery);
            return this;
        }
    }

    @Override
    public Params prepare() {
        return new Params(this);
    }

    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.select()
                .fromFunction("dolt_query_diff")
                .withParams(params)
                .build();
    }
}