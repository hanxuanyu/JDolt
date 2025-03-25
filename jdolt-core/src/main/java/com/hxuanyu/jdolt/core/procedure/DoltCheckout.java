package com.hxuanyu.jdolt.core.procedure;


import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `DOLT_CHECKOUT()`
 * <p>
 * 切换当前会话到其他分支。
 * <p>
 * 如果以表名作为参数，则将这些表恢复到当前 HEAD 中的内容。
 * <p>
 * 注意，与 Git 命令行不同，如果您有已修改的工作集，在执行 `DOLT_CHECKOUT()` 后，这些更改仍保留在您修改过的分支上。
 * 未提交的工作集更改不会像在命令行中那样转移到切换到的分支上。我们在 SQL 环境中修改了此行为，因为多个用户可能同时连接到同一个分支。
 * 当用户切换分支时，将来自其他分支的更改带入会对多租户 SQL 环境造成过多干扰。
 *
 * <pre>
 * {@code
 * CALL DOLT_CHECKOUT('-b', 'my-new-branch');
 * CALL DOLT_CHECKOUT('my-existing-branch');
 * CALL DOLT_CHECKOUT('my-table');
 * }
 * </pre>
 * <p>
 * ### 注意事项
 * <p>
 * 带分支参数的 `DOLT_CHECKOUT()` 会对您的会话状态产生两个副作用：
 * <p>
 * 1. 会话的当前数据库（通过 `SELECT DATABASE()` 返回）现在是未加限定的数据库名称。
 * 2. 在此会话的剩余时间内，对该数据库未加限定名称的引用将解析为被检出的分支。
 * <p>
 * 请查看以下语句后的注释以了解此行为的示例，同时阅读 [使用](assets/branches-20250102153305-daob99t.md)​[分支](assets/branches-20250102153305-0czvab3.md)。
 *
 * <pre>
 * {@code
 * set autocommit = on;
 * use mydb/branch1; -- 当前数据库现在是 `mydb/branch1`
 * insert into t1 values (1); -- 修改 `branch1` 分支
 * call dolt_checkout('branch2'); -- 当前数据库现在是 `mydb`
 * insert into t1 values (2); -- 修改 `branch2` 分支
 * use mydb/branch3; -- 当前数据库现在是 `mydb/branch3`
 * insert into mydb.t1 values (3); -- 修改 `branch2` 分支
 * }
 * </pre>
 * <p>
 * ### 选项
 * <p>
 * `-b`: 创建一个具有指定名称的新分支。
 * <p>
 * `-B`: 类似于 `-b`，但如果分支已存在，则会移动分支。
 * <p>
 * `-t`: 创建新分支时，设置“上游”配置。
 * <p>
 * ### 输出模式
 *
 * <pre>
 * {@code
 * +---------+------+-----------------------------+
 * | Field   | Type | Description                 |
 * +---------+------+-----------------------------+
 * | status  | int  | 0 表示成功，1 表示失败      |
 * | message | text | 成功/失败信息               |
 * +---------+------+-----------------------------+
 * }
 * </pre>
 * <p>
 * ### 示例
 *
 * <pre>
 * {@code
 * -- 设置会话的当前数据库
 * USE mydb;
 *
 * -- 创建并切换到一个新分支
 * CALL DOLT_CHECKOUT('-b', 'feature-branch');
 *
 * -- 进行修改
 * UPDATE table
 * SET column = "new value"
 * WHERE pk = "key";
 *
 * -- 暂存并提交所有更改
 * CALL DOLT_COMMIT('-a', '-m', 'committing all changes');
 *
 * -- 返回到主分支
 * CALL DOLT_CHECKOUT('main');
 * }
 * </pre>
 */
public class DoltCheckout extends DoltRepository implements DoltProcedure<DoltCheckout.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCheckout> INSTANCES = new ConcurrentHashMap<>();

    private DoltCheckout(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCheckout getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCheckout(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> procedure) {
            super(Params.class, procedure);
        }

        @MethodExclusive
        public Params checkout(String branch) {
            validator.checkAndMark("checkout");
            addFlags(branch);
            return this;
        }

        @MethodExclusive
        public Params resetTable(String tableName) {
            validator.checkAndMark("resetTable");
            addFlags(tableName);
            return this;
        }

        @MethodExclusive
        public Params newBranch(String newBranch) {
            validator.checkAndMark("newBranch");
            addFlags("-b", newBranch);
            return this;
        }

        @MethodExclusive
        public Params newBranchForced(String newBranch) {
            validator.checkAndMark("newBranchForced");
            addFlags("-B", newBranch);
            return this;
        }


        @MethodExclusive
        public Params newBranchTraced(String newBranch) {
            validator.checkAndMark("newBranchTraced");
            addFlags("-t", newBranch);
            return this;
        }

    }

    /**
     * 准备参数构建器
     */
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.callProcedure("dolt_checkout")
                .withParams(params)
                .build();
    }

}
