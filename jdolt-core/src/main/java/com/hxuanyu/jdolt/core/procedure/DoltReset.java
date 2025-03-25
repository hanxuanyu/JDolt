package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `DOLT_RESET()`
 * <p>
 * 默认模式将暂存表重置为其 HEAD 状态。也可以用于将数据库重置到特定的提交。
 * 其工作方式与 CLI 中的 `dolt reset` 完全相同，并接受相同的参数。
 * <p>
 * 与其他数据修改操作一样，在执行重置后，必须通过 `COMMIT` 提交事务，
 * 才能使对受影响表的任何更改对其他客户端可见。
 * <p>
 * 示例用法:
 * <pre>
 * {@code
 * CALL DOLT_RESET('--hard', 'featureBranch');
 * CALL DOLT_RESET('--hard', 'commitHash123abc');
 * CALL DOLT_RESET('myTable'); // 软重置
 * }
 * </pre>
 * <p>
 * 注意事项:
 * <ul>
 *   <li>使用 `--hard` 选项时，`dolt_reset()` 过程会隐式提交当前事务并开始一个新的事务。</li>
 * </ul>
 * <p>
 * ### 选项
 *
 * <ul>
 *   <li>`--hard`：重置工作表和暂存表。自 <commit> 以来对工作树中已跟踪表的任何更改都将被丢弃。</li>
 *   <li>`--soft`：不会修改工作表，但会移除所有暂存待提交的表。这是默认行为。</li>
 * </ul>
 */
public class DoltReset extends DoltRepository implements DoltProcedure<DoltReset.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltReset> INSTANCES = new ConcurrentHashMap<>();

    private DoltReset(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltReset getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltReset(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"withRevision"})
        public Params withRevision(String revision) {
            validator.checkAndMark("withRevision");
            addFlags(revision);
            return this;
        }

        @MethodMutexGroup({"hard"})
        public Params hard() {
            validator.checkAndMark("hard");
            addFlags("--hard");
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_reset"), params);
    }

}