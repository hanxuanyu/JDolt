package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_VERIFY_CONSTRAINTS()`
 * <p>
 * 验证工作集中的更改（插入、更新和/或删除）是否满足已定义的表约束。如果有任何约束被违反，它们将被写入到
 * [DOLT_CONSTRAINT_VIOLATIONS](./dolt-system-tables.md#doltconstraintviolations) 表中。
 * <p>
 * 默认情况下，`DOLT_VERIFY_CONSTRAINTS` 不会检测之前已提交的行更改的约束。可以指定 `--all` 选项来验证数据库中的所有行。
 * 如果在之前的提交中禁用了 `FOREIGN_KEY_CHECKS`，您可能需要使用 `--all` 选项以确保当前状态的一致性，并且不会遗漏任何违反的约束。
 * <p>
 * ### 参数和选项
 * <p>
 * `<table>`：要检查约束的表。如果省略，则检查所有表。
 * <p>
 * `-a`, `--all`：<br />对每一行验证约束。
 * <p>
 * `-o`, `--output-only`：<br />禁用将结果写入 [DOLT_CONSTRAINT_VIOLATIONS](./dolt-system-tables.md#doltconstraintviolations) 系统表。
 * <p>
 * ### 输出模式
 *
 * <pre>
 * +------------+------+-----------------------------------------+
 * | Field      | Type | Description                             |
 * +------------+------+-----------------------------------------+
 * | violations | int  | 如果发现违规为 1，否则为 0              |
 * +------------+------+-----------------------------------------+
 * </pre>
 * <p>
 * ### 示例
 * <p>
 * 以下示例基于以下模式：
 *
 * <pre>{@code
 * CREATE TABLE parent (
 *   pk int PRIMARY KEY
 * );
 *
 * CREATE TABLE child (
 *   pk int PRIMARY KEY,
 *   parent_fk int,
 *   FOREIGN KEY (parent_fk) REFERENCES parent(pk)
 * );
 * }</pre>
 * <p>
 * 一个简单的案例：
 *
 * <pre>{@code
 * -- 启用 dolt_force_transaction_commit，以便我们可以检查工作集中的违规
 * SET dolt_force_transaction_commit = ON;
 * SET FOREIGN_KEY_CHECKS = OFF;
 * INSERT INTO PARENT VALUES (1);
 * -- 违反了子表的外键约束
 * INSERT INTO CHILD VALUES (1, -1);
 *
 * CALL DOLT_VERIFY_CONSTRAINTS();
 * /*
 * +------------+
 * | violations |
 * +------------+
 * | 1          |
 * +------------+
 * *\/
 *
 * SELECT * from dolt_constraint_violations;
 * /*
 * +-------+----------------+
 * | table | num_violations |
 * +-------+----------------+
 * | child | 1              |
 * +-------+----------------+
 * *\/
 *
 * SELECT violation_type, pk, parent_fk from dolt_constraint_violations_child;
 * /*
 * +----------------+----+-----------+
 * | violation_type | pk | parent_fk |
 * +----------------+----+-----------+
 * | foreign key    | 1  | -1        |
 * +----------------+----+-----------+
 * *\/
 * }</pre>
 * <p>
 * 使用 `--all` 验证所有行：
 *
 * <pre>{@code
 * SET DOLT_FORCE_TRANSACTION_COMMIT = ON;
 * SET FOREIGN_KEY_CHECKS = OFF;
 * INSERT INTO PARENT VALUES (1);
 * INSERT INTO CHILD VALUES (1, -1);
 * CALL DOLT_COMMIT('-am', 'violating rows');
 *
 * CALL DOLT_VERIFY_CONSTRAINTS();
 * /*
 * 由于工作集中没有更改，因此未返回违规。
 *
 * +------------+
 * | violations |
 * +------------+
 * | 0          |
 * +------------+
 * *\/
 *
 * SELECT * from dolt_constraints_violations_child;
 * /*
 * +----------------+----+-----------+----------------+
 * | violation_type | pk | parent_fk | violation_info |
 * +----------------+----+-----------+----------------+
 * +----------------+----+-----------+----------------+
 * *\/
 *
 * CALL DOLT_VERIFY_CONSTRAINTS('--all');
 * /*
 * 当考虑所有行时，发现约束违规。
 *
 * +------------+
 * | violations |
 * +------------+
 * | 1          |
 * +------------+
 * *\/
 *
 * SELECT * from dolt_constraint_violations_child;
 * /*
 * +----------------+----+-----------+
 * | violation_type | pk | parent_fk |
 * +----------------+----+-----------+
 * | foreign key    | 1  | -1        |
 * +----------------+----+-----------+
 * *\/
 * }</pre>
 * <p>
 * 仅检查特定表：
 *
 * <pre>{@code
 * SET DOLT_FORCE_TRANSACTION_COMMIT = ON;
 * SET FOREIGN_KEY_CHECKS = OFF;
 * INSERT INTO PARENT VALUES (1);
 * INSERT INTO CHILD VALUES (1, -1);
 *
 * CALL DOLT_VERIFY_CONSTRAINTS('parent');
 * /*
 * +------------+
 * | violations |
 * +------------+
 * | 0          |
 * +------------+
 * *\/
 *
 * CALL DOLT_VERIFY_CONSTRAINTS('child');
 * /*
 * +------------+
 * | violations |
 * +------------+
 * | 1          |
 * +------------+
 * *\/
 *
 * SELECT * from dolt_constraint_violations_child;
 * /*
 * +----------------+----+-----------+
 * | violation_type | pk | parent_fk |
 * +----------------+----+-----------+
 * | foreign key    | 1  | -1        |
 * +----------------+----+-----------+
 * *\/
 * }</pre>
 */
public class DoltVerifyConstraints extends DoltRepository implements DoltProcedure<DoltVerifyConstraints.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltVerifyConstraints> INSTANCES = new ConcurrentHashMap<>();

    private DoltVerifyConstraints(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltVerifyConstraints getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltVerifyConstraints(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"withTable"})
        public Params withTable(String table) {
            validator.checkAndMark("withTable");
            addFlags(table);
            return this;
        }

        @MethodMutexGroup({"withTables"})
        public Params withTables(String... tables) {
            validator.checkAndMark("withTables");
            addFlags(tables);
            return this;
        }

        @MethodMutexGroup({"all"})
        public Params all() {
            validator.checkAndMark("all");
            addFlags("--all");
            return this;
        }

        @MethodMutexGroup({"outputOnly"})
        public Params outputOnly() {
            validator.checkAndMark("outputOnly");
            addFlags("--output-only");
            return this;
        }
    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.callProcedure("dolt_verify_constraints")
                .withParams(params)
                .build();
    }
}