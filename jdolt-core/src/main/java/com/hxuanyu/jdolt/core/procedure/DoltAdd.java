package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_ADD()`
 * <p>
 * 将当前会话中的工作区更改添加到暂存区。功能与CLI中的`dolt add`完全相同，并接受相同的参数。
 * <p>
 * 将表添加到暂存区后，可以使用`DOLT_COMMIT()`提交这些更改。
 *
 * <pre>
 * {@code
 * CALL DOLT_ADD('-A');
 * CALL DOLT_ADD('.');
 * CALL DOLT_ADD('table1', 'table2');
 * }
 * </pre>
 * <p>
 * ### 选项
 * <p>
 * - `table`：要添加到暂存区的表。可以使用缩写`.`来添加所有表。
 * - `-A`：暂存所有有更改的表。
 * <p>
 * ### 输出模式
 *
 * <pre>
 * {@code
 * +--------+------+---------------------------+
 * | Field  | Type | Description               |
 * +--------+------+---------------------------+
 * | status | int  | 成功返回0，失败返回1      |
 * +--------+------+---------------------------+
 * }
 * </pre>
 * <p>
 * ### 示例
 *
 * <pre>
 * {@code
 * -- 设置当前会话的数据库
 * USE mydb;
 *
 * -- 进行修改
 * UPDATE table
 * SET column = "new value"
 * WHERE pk = "key";
 *
 * -- 暂存所有更改
 * CALL DOLT_ADD('-A');
 *
 * -- 提交更改
 * CALL DOLT_COMMIT('-m', 'committing all changes');
 * }
 * </pre>
 */
public class DoltAdd extends DoltRepository implements DoltProcedure {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltAdd> INSTANCES = new ConcurrentHashMap<>();

    private DoltAdd(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltAdd getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltAdd(connectionManager));
    }

    public static class Params extends AbstractParamBuilder<Params> {

        protected Params() {
            super(Params.class);
        }


        @MethodMutexGroup({"addCurrent", "withTable", "addAll"})
        public Params addAll() {
            validator.checkAndMark("addAll");
            addFlag("-A");
            return this;
        }

        @MethodMutexGroup({"addCurrent", "withTable", "addAll"})
        public Params addCurrent() {
            validator.checkAndMark("addCurrent");
            addFlags(".");
            return this;
        }

        @MethodMutexGroup({"addCurrent", "addAll"})
        public Params withTable(String table) {
            validator.checkAndMark("withTable");
            addFlag(table);
            return this;
        }

        @MethodMutexGroup({"addCurrent", "addAll"})
        public Params withTable(String... tables) {
            validator.checkAndMark("withTable");
            addFlags(tables);
            return this;
        }
    }

    /**
     * 准备参数构建器
     */
    public Params prepare() {
        return new Params();
    }

    /**
     * 执行存储过程
     */
    public ProcedureResult execute(Params params) {
        return call(params.toProcedureArgs());
    }

    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_add"), params);
    }

}