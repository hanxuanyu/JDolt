package com.hxuanyu.jdolt.core.procedure;


import com.hxuanyu.jdolt.annotation.MethodAllowGroup;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `DOLT_CLEAN()`
 * <p>
 * 删除工作集中的未跟踪表。
 * <p>
 * 如果传入表名作为参数，则仅删除指定的未跟踪表。
 * <p>
 * 使用 `--dry-run` 标志，可以测试删除未跟踪表是否会返回零状态。
 *
 * <pre>{@code
 * CALL DOLT_CLEAN();
 * CALL DOLT_CLEAN('untracked-table');
 * CALL DOLT_CLEAN('--dry-run');
 * }</pre>
 * <p>
 * ### 选项
 * <p>
 * `--dry-run`：测试从工作集中删除未跟踪表。
 * <p>
 * ### 输出模式
 *
 * <pre>{@code
 * +--------+------+---------------------------+
 * | 字段   | 类型 | 描述                      |
 * +--------+------+---------------------------+
 * | status | int  | 成功为0，失败为1          |
 * +--------+------+---------------------------+
 * }</pre>
 * <p>
 * ### 示例
 *
 * <pre>{@code
 * -- 创建三个新表
 * create table tracked (x int primary key);
 * create table committed (x int primary key);
 * create table untracked (x int primary key);
 *
 * -- 提交第一个表
 * call dolt_add('committed');
 * call dolt_commit('-m', 'commit a table');
 * +----------------------------------+
 * | hash                             |
 * +----------------------------------+
 * | n7gle7jv6aqf72stbdicees6iduhuoo9 |
 * +----------------------------------+
 *
 * -- 跟踪第二个表
 * call dolt_add('tracked');
 *
 * -- 查看数据库状态
 * select * from dolt_status;
 * +------------+--------+-----------+
 * | table_name | staged | status    |
 * +------------+--------+-----------+
 * | tracked    | true   | new table |
 * | untracked  | false  | new table |
 * +------------+--------+-----------+
 *
 * -- 清除未跟踪表
 * call dolt_clean('untracked');
 *
 * -- 查看最终状态
 * select * from dolt_status;
 * +------------+--------+-----------+
 * | table_name | staged | status    |
 * +------------+--------+-----------+
 * | tracked    | true   | new table |
 * +------------+--------+-----------+
 *
 * -- 已提交和已跟踪的表被保留
 * show tables;
 * +----------------+
 * | Tables_in_tmp3 |
 * +----------------+
 * | committed      |
 * | tracked        |
 * +----------------+
 * }</pre>
 */
public class DoltClean extends DoltRepository implements DoltProcedure<DoltClean.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltClean> INSTANCES = new ConcurrentHashMap<>();

    private DoltClean(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltClean getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltClean(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }


        @MethodAllowGroup({"withTableName"})
        public Params withTableName(String tableName) {
            validator.checkAndMark("withTableName");
            addFlags(tableName);
            return this;
        }

        @MethodMutexGroup("dryRun")
        public ProcedureResult dryRun() {
            validator.checkAndMark("dryRun");
            addFlags("--dry-run");
            return execute();
        }

    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_clean"), params);
    }

}
