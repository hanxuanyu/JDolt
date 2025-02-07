package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_CONFLICTS_RESOLVE()`
 * <p>
 * 当合并操作发现冲突更改时，会将其记录在 `dolt_conflicts` 表中。<br>
 * 冲突发生在两个版本之间：我们的版本（目标分支头部的行）和他们的版本（源分支头部的行）。<br>
 * `dolt conflicts resolve` 会自动通过为每行选择我们的版本或他们的版本来解决冲突。
 *
 * <pre>{@code
 * CALL DOLT_CONFLICTS_RESOLVE('--ours', <table>);
 * CALL DOLT_CONFLICTS_RESOLVE('--theirs', <table>);
 * }</pre>
 * <p>
 * ### 参数选项
 * <p>
 * `<table>`：要解决冲突的表的列表。可以使用 `.` 来解决所有表的冲突。
 * <p>
 * `--ours`：对于所有冲突，选择我们分支的版本并解决冲突。
 * <p>
 * `--theirs`：对于所有冲突，选择他们分支的版本并解决冲突。
 * <p>
 * ### 输出模式
 *
 * <pre>{@code
 * +--------+------+---------------------------+
 * | Field  | Type | Description               |
 * +--------+------+---------------------------+
 * | status | int  | 成功为 0，失败为 1        |
 * +--------+------+---------------------------+
 * }</pre>
 * <p>
 * ### 示例
 *
 * <pre>{@code
 * -- 设置当前会话的数据库
 * USE mydb;
 *
 * -- 尝试合并
 * CALL DOLT_MERGE('feature-branch');
 *
 * -- 检查是否存在冲突
 * SELECT * FROM dolt_conflicts;
 *
 * -- 使用我们分支的行解决表 t1 和 t2 的冲突。
 * CALL DOLT_CONFLICTS_RESOLVE('--ours', 't1', 't2');
 * }</pre>
 */
public class DoltConflictsResolve extends DoltRepository implements DoltProcedure<DoltConflictsResolve.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltConflictsResolve> INSTANCES = new ConcurrentHashMap<>();

    private DoltConflictsResolve(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltConflictsResolve getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltConflictsResolve(connectionManager));
    }

    public static class Params extends AbstractParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"acceptOurs", "acceptTheirs"})
        public Params acceptOurs() {
            validator.checkAndMark("acceptOurs");
            addFlags("--ours");
            return this;
        }

        @MethodMutexGroup({"acceptTheirs", "acceptOurs"})
        public Params acceptTheirs() {
            validator.checkAndMark("acceptTheirs");
            addFlags("--theirs");
            return this;
        }

        @MethodInvokeRequired
        public Params withTables(String... tables) {
            validator.checkAndMark("withTables");
            addFlags(tables);
            return this;
        }

    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("DOLT_CONFLICTS_RESOLVE"), params);
    }

}
